package org.fbox.expert.api.sos.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.fbox.expert.api.sos.model.DataStreamMetadata;
import org.fbox.expert.api.sos.model.ProcedureMetadata;
import org.fbox.expert.util.JDBCUtils;


import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class SensorStreamMetadataServlet
 */
@WebServlet("/api/sos/getProceduresMetadata")
public class GetProceduresMetadataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
	@Resource(lookup="java:jboss/datasources/sos_me_DS")
	// @Resource(lookup="java:jboss/datasources/idira_sos_me_scenario_DS")
	
	private DataSource sosDataSource;
	
	
	private static final String PHENOMENA_ID_PARAM = "phenIds";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    //public SensorStreamMetadataServlet() {
    //    super();
    //}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String phenIdsParam = request.getParameter(PHENOMENA_ID_PARAM);
		
		String[] phenIds = null;
		if (phenIdsParam != null) {
			 phenIds = phenIdsParam.split(",");
		}
		
		try {
			List<ProcedureMetadata> pmList = getProcedureMetadata();
			
			// Filter response according to requested phenomena 
			if(phenIds != null) {
				List<ProcedureMetadata> pmFilteredList = new LinkedList<ProcedureMetadata>();
			
				Iterator<ProcedureMetadata> pmIterator = pmList.iterator();
				ProcedureMetadata pmCurrent = new ProcedureMetadata();
				Set<String> phenomena = null;
				
				while(pmIterator.hasNext()) {	
					pmCurrent = pmIterator.next();
					phenomena = pmCurrent.getPhenomena();
					
					for(int i=0; i<phenIds.length; i++) {	
						if(phenomena.contains(phenIds[i])) {
							pmFilteredList.add(pmCurrent);
							break;
						}
					}	
				}
				
				pmList = pmFilteredList;
			}
			
			// ~~~~~ Response ~~~~~ //
			// Format response
			ObjectMapper mapper = new ObjectMapper(); 
			String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(pmList);
						
			// System.out.println(json);
			
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.write(json);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<ProcedureMetadata> getProcedureMetadata() throws SQLException {
		
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		
		List<ProcedureMetadata> pmList = null;	// List that contains the procedures' metadata
		
		// A map containing the mapping betwwen a procedure id and its observed phenomena
		// Init map
		Map<String, ProcedureMetadata> procPhenMap = new HashMap<String, ProcedureMetadata>();
		Set<String> procedureIdSet = new HashSet<String>();	// A set containing all procedureIds
		List<DataStreamMetadata> dsmList = null;			// A list containing all data stream metadata
		
		try {
			
			/*
			// Create SQL constraint for phenomenon Ids
			String phenIdsConstraint = "";
			
			if(phenIds != null) {
				
				for(int i=0; i<phenIds.length; i++) {
					if(i == 0)
						phenIdsConstraint += " AND(pp.phenomenon_id='" + phenIds[i] + "'";
					else
						phenIdsConstraint += " OR pp.phenomenon_id='" + phenIds[i] + "'";
				}
				
				if(phenIds.length > 0)
					phenIdsConstraint += ")";
			} */
				
			connection = sosDataSource.getConnection();
			statement = connection.createStatement(); 
			
			String sqlQuery = "SELECT phist.procedure_id, pp.phenomenon_id, ST_Y(phist.position) AS latitude, ST_X(phist.position) AS longitude, phist.active, phist.mobile " +
					   "FROM (" +
					   		"SELECT procedure_id, MAX(time_stamp) AS lastTimestamp " +
					   		"FROM procedure_history phist " +
					   		"GROUP BY procedure_id" +
					   	") AS procLastPosition, procedure_history phist, proc_phen pp /*,phenomenon phen*/" +
					   	"WHERE phist.procedure_id = procLastPosition.procedure_id" + 
						" AND phist.time_stamp = procLastPosition.lastTimestamp" +  
						" AND phist.procedure_id = pp.procedure_id" +
						// phenIdsConstraint +
						" ORDER BY phist.procedure_id";
			
			System.out.println("[INFO] SQLQuery: " + sqlQuery);
			
			resultSet = statement.executeQuery(sqlQuery);
			
			dsmList = new LinkedList<DataStreamMetadata>();
			
			while(resultSet.next()) {
				DataStreamMetadata dsm = new DataStreamMetadata();
				dsm.setProcedureId(resultSet.getString(1));
				dsm.setPhenomenonId(resultSet.getString(2));
				
				BigDecimal latBigDecimal = new BigDecimal(resultSet.getString(3));
				latBigDecimal = latBigDecimal.setScale(6, RoundingMode.HALF_UP);
				dsm.setLatitude(latBigDecimal.toString());
				
				BigDecimal longBigDecimal = new BigDecimal(resultSet.getString(4));
				longBigDecimal = longBigDecimal.setScale(6, RoundingMode.HALF_UP);
				dsm.setLongtitude(longBigDecimal.toString());
				
				dsm.setActive(resultSet.getBoolean(5));
				dsm.setMobile(resultSet.getBoolean(6));
				
				// Add data stream
				dsmList.add(dsm);
				
				// Init map <procedureId, ProcedureMetadata> 
				procPhenMap.put(dsm.getProcedureId(), new ProcedureMetadata(dsm));
				
				// Add procedureId 
				procedureIdSet.add(resultSet.getString(1));
			}
			
			// Iterate through data stream list and map with the appropriate procedureid
			Iterator<DataStreamMetadata> dsmIterator = dsmList.iterator();
			DataStreamMetadata dsmCurrent = new DataStreamMetadata();
			while(dsmIterator.hasNext()) {	
				dsmCurrent = dsmIterator.next();
				
				// add the phenomenonId to set
				procPhenMap.get(dsmCurrent.getProcedureId()).getPhenomena().add(dsmCurrent.getPhenomenonId());
			}
			
			// 
			pmList = new LinkedList<ProcedureMetadata>();
			Iterator<Entry<String, ProcedureMetadata>> procPhenMapIterator = procPhenMap.entrySet().iterator();
			while(procPhenMapIterator.hasNext()) {
				Map.Entry<String, ProcedureMetadata> entry = (Map.Entry<String, ProcedureMetadata>) procPhenMapIterator.next();
				pmList.add(entry.getValue());
			}
			
		} finally {
			JDBCUtils.close(resultSet);
			JDBCUtils.close(statement);
			JDBCUtils.close(connection);
		}
		
		return pmList;
		
	}

}
