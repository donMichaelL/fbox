package org.sos.ds.ui.table.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.sos.ds.model.LastObservation;
import org.sos.ds.ui.table.model.DataTablesParamUtility;
import org.sos.ds.ui.table.model.DataTablesLastObservationModel;
import org.sos.ds.ui.table.model.DataTablesParamModel;
import org.sos.setup.SetupConstants;

import org.jdbc.util.JDBCUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class LastObservationsServlet
 */
@WebServlet("/api/lastObservations")
public class DataStreamLastObservationsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Resource(lookup="java:jboss/datasources/sos_me_DS")
	private DataSource sosDataSource;  // Primary sos db
	
	@Resource(lookup="java:jboss/datasources/sos_me_scenario_DS")
	private DataSource sosScenarioDataSource;  // Scenario sos db
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		// System.out.println("===> [INFO] LastObservation servlet called!");
		DataTablesParamModel param = DataTablesParamUtility.getParam(request);
		int sEcho = Integer.parseInt(param.sEcho);
		int iTotalRecords;
		int iTotalDisplayRecords;
		
		try {
			// Get last observations from primary db
			List<LastObservation> lObsList = new LinkedList<LastObservation>(); 
			lObsList.addAll(getLastObservations(sosDataSource));
			
			// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ //
			// ~~~~~ Loading properties ~~~~~ //
			// System.out.println("Loading properties....");
			Properties props = new Properties();
			InputStream inStream = getServletContext().getResourceAsStream("/WEB-INF/" + SetupConstants.SOS_DB_PROPERTIES_FILENAME);
			props.load(inStream);
			String isEnabledScenarioDatabaseStr = props.getProperty(SetupConstants.SOS_ME_SCENARIO_DB_PROPERTY);   
			// System.out.println("Scenario db enabled: " + isEnabledScenarioDatabaseStr);
			inStream.close();
			
			// ~~~~~ Get last observations from slave db ~~~~~ //
			if (isEnabledScenarioDatabaseStr.equals("true"))
				lObsList.addAll(getLastObservations(sosScenarioDataSource));  
			// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ //
						
			
			iTotalRecords = lObsList.size();
			
			List<LastObservation> lObsToSent = new LinkedList<LastObservation>();
			
			// ~~~~~ Searching capabilities ~~~~~ //
			if(param.sSearch != null && param.sSearch != "") {
			//	System.out.println("Searching.... " + param.sSearch.toLowerCase());
				for(LastObservation lObs : lObsList) {
					if( lObs.getTimeStamp().toLowerCase().contains(param.sSearch.toLowerCase())   ||
						lObs.getProcedureId().toLowerCase().contains(param.sSearch.toLowerCase()) || 
					    lObs.getPhenomenonId().toLowerCase().contains(param.sSearch.toLowerCase()) ||
					    lObs.getValue().toLowerCase().contains(param.sSearch.toLowerCase())) 
					{
						// System.out.println("Adding...");
						lObsToSent.add(lObs); // add ... that matches given search criterion
					}
				}
			}
			else
				lObsToSent = lObsList;
			
			iTotalDisplayRecords = lObsToSent.size();
			
			
			// ~~~~~ Filtering Capabilities ~~~~~ //
			final int sortColumnIndex = param.iSortColumnIndex;
			final int sortDirection = param.sSortDirection.equals("asc") ? -1 : 1;
			
			Collections.sort(lObsToSent, new Comparator<LastObservation>() {
				@Override
				public int compare(LastObservation lObs1, LastObservation lObs2) {
					switch(sortColumnIndex) {
					case 0:
						return lObs1.getTimeStamp().compareTo(lObs2.getTimeStamp()) * sortDirection;
					case 1:
						return lObs1.getProcedureId().compareTo(lObs2.getProcedureId()) * sortDirection;
					case 2:
						return lObs1.getPhenomenonId().compareTo(lObs2.getPhenomenonId()) * sortDirection;
					case 3:
						return lObs1.getValue().compareTo(lObs2.getValue()) * sortDirection;
					}
					return 0;
				}
				
			});
			
			
			// ~~~~~ Paging Capabilities ~~~~~ //
			if(lObsToSent.size() < param.iDisplayStart + param.iDisplayLength) {
				lObsToSent = lObsToSent.subList(param.iDisplayStart, lObsToSent.size());
			}
			else {
				lObsToSent = lObsToSent.subList(param.iDisplayStart, param.iDisplayStart + param.iDisplayLength);
			}
			
			
			// ~~~~~ Response ~~~~~ //
			DataTablesLastObservationModel dtPosModel = new DataTablesLastObservationModel();
			dtPosModel.setsEcho(sEcho);
			dtPosModel.setiTotalRecords(iTotalRecords);
			dtPosModel.setiTotalDisplayRecords(iTotalDisplayRecords);
			dtPosModel.setaaData(lObsToSent);
			
			// Fromat response
			ObjectMapper mapper = new ObjectMapper(); 
			String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dtPosModel);
			
			// System.out.println(json);
			
			// Send response
			PrintWriter out = response.getWriter();
			out.write(json);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param dataSource
	 * @return
	 * @throws SQLException
	 */
	public List<LastObservation> getLastObservations(DataSource dataSource) throws SQLException {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		
		List<LastObservation> lObsList = null;
		
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement(); 
			
			// First, find max per data stream (procedureId + phenomenonId)
			/* String sqlQuery =  "SELECT obs.time_stamp, obs.procedure_id, obs.phenomenon_id, obs.text_value, obs.numeric_value, obs.spatial_value " +
							"FROM (" +
								"SELECT procedure_id, phenomenon_id, MAX(time_stamp) AS lastTimestamp " +
								"FROM observation " + 
								"GROUP BY procedure_id, phenomenon_id" +
							") AS proclast, observation obs " +
							"WHERE obs.procedure_id = proclast.procedure_id AND obs.phenomenon_id = proclast.phenomenon_id AND obs.time_stamp = proclast.lastTimestamp"; */
	
			// First, find max per procedure. Assumption all data streams of a procedure has the same max timestamp.
			String sqlQuery =  "SELECT obs.time_stamp, obs.procedure_id, obs.phenomenon_id, obs.text_value, obs.numeric_value, obs.spatial_value " +
					"FROM (" +
						"SELECT procedure_id, MAX(time_stamp) AS lastTimestamp " +
						"FROM observation " + 
						"GROUP BY procedure_id" +
					") AS proclast, observation obs " +
					"WHERE obs.procedure_id = proclast.procedure_id AND obs.time_stamp = proclast.lastTimestamp";
			
					
//			 System.out.println("[INFO] SQLQuery: " + sqlQuery);
			
			resultSet = statement.executeQuery(sqlQuery);
			
			lObsList = new LinkedList<LastObservation>();
			
			while(resultSet.next()) {
				LastObservation lObs = new LastObservation();
				// TimeZone for SOS representation
				String timeStampWithTimeZone = resultSet.getString(1) + "00";
				lObs.setTimeStamp(timeStampWithTimeZone);
				lObs.setProcedureId(resultSet.getString(2));
				lObs.setPhenomenonId(resultSet.getString(3));
				
				if (resultSet.getString(4) != null)
					lObs.setValue(resultSet.getString(4));
				else if (resultSet.getString(5) != null)
					lObs.setValue(resultSet.getString(5));
				else if (resultSet.getObject(6) != null)
					lObs.setValue(resultSet.getObject(6).toString());
				else 
					System.out.println("[LastObservation] [WARN] No value to set!");
				
				lObsList.add(lObs);
			}
			
		} finally {
			JDBCUtils.close(resultSet);
			JDBCUtils.close(statement);
			JDBCUtils.close(connection);
		}
		
		return lObsList;
	}
}
