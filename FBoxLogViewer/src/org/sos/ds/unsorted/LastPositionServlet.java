package org.sos.ds.unsorted;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.jdbc.util.JDBCUtils;

import java.sql.Connection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;

import org.sos.ds.ui.table.model.DataTablesParamUtility;
import org.sos.ds.ui.table.model.DataTablesLastPositionModel;
import org.sos.ds.ui.table.model.DataTablesParamModel;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Servlet implementation class SOSManager
 */
@WebServlet("/LastPosition")
public class LastPositionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	@Resource(lookup="java:jboss/datasources/sos_me_DS")
	// @Resource(lookup="java:jboss/datasources/sos_me_scenario_DS")
	
	private DataSource sosDataSource;
	
	/**
     * Default constructor. 
     */
    public LastPositionServlet() {
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		DataTablesParamModel param = DataTablesParamUtility.getParam(request);
		int sEcho = Integer.parseInt(param.sEcho);
		int iTotalRecords;
		int iTotalDisplayRecords;
		
		try {
			List<LastPosition> lpDaoList = getLastPositions();
			
			iTotalRecords = lpDaoList.size();
			
			List<LastPosition> lpToSent = new LinkedList<LastPosition>();
			
			// ~~~~~ Searching capabilities ~~~~~ //
			if(param.sSearch != null && param.sSearch != "") {
			//	System.out.println("Searching.... " + param.sSearch.toLowerCase());
				for(LastPosition lp : lpDaoList) {
					if(lp.getProcedureID().toLowerCase().contains(param.sSearch.toLowerCase()) || 
					   lp.getTimeStamp().toLowerCase().contains(param.sSearch.toLowerCase())   ||
					   lp.getLatitude().toLowerCase().contains(param.sSearch.toLowerCase()) ||
					   lp.getLongtitude().toLowerCase().contains(param.sSearch.toLowerCase())) 
					{
						// System.out.println("Adding...");
						lpToSent.add(lp); // add ... that matches given search criterion
					}
				}
			}
			else
				lpToSent = lpDaoList;
			
			iTotalDisplayRecords = lpToSent.size();
			
			// ~~~~~ Filtering Capabilities ~~~~~ //
			final int sortColumnIndex = param.iSortColumnIndex;
			final int sortDirection = param.sSortDirection.equals("asc") ? -1 : 1;
			
			Collections.sort(lpToSent, new Comparator<LastPosition>() {
				@Override
				public int compare(LastPosition lp1, LastPosition lp2) {
					switch(sortColumnIndex) {
					case 0:
						return lp1.getProcedureID().compareTo(lp2.getProcedureID()) * sortDirection;
					case 1:
						return lp1.getTimeStamp().compareTo(lp2.getTimeStamp()) * sortDirection;
					case 2:
						return lp1.getLatitude().compareTo(lp2.getLatitude()) * sortDirection;
					case 3:
						return lp1.getLongtitude().compareTo(lp2.getLongtitude()) * sortDirection;
					}
					return 0;
				}
				
			});
			
			// ~~~~~ Paging Capabilities ~~~~~ //
			if(lpToSent.size() < param.iDisplayStart + param.iDisplayLength) {
				lpToSent = lpToSent.subList(param.iDisplayStart, lpToSent.size());
			}
			else {
				lpToSent = lpToSent.subList(param.iDisplayStart, param.iDisplayStart + param.iDisplayLength);
			}
			
			// ~~~~~ Response ~~~~~ //
			DataTablesLastPositionModel dtPosModel = new DataTablesLastPositionModel();
			dtPosModel.setsEcho(sEcho);
			dtPosModel.setiTotalRecords(iTotalRecords);
			dtPosModel.setiTotalDisplayRecords(iTotalDisplayRecords);
			dtPosModel.setdata(lpToSent);
			
			// Fromat response
			ObjectMapper mapper = new ObjectMapper(); 
			String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dtPosModel);
			
			// System.out.println(json);
			
			// Send response
			PrintWriter out = response.getWriter();
			out.write(json);
				
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	// 
	public List<LastPosition> getLastPositions() throws SQLException {
		
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		
		List<LastPosition> lpDaoList = null;
		
		try {	
			connection = sosDataSource.getConnection();
			statement = connection.createStatement(); 
			
			String sqlQuery = "SELECT phist1.procedure_id, phist1.time_stamp, ST_Y(phist1.position) AS latitude, ST_X(phist1.position) AS longitude " +
					"FROM procedure_history phist1 LEFT JOIN procedure_history phist2 " +
					"ON (phist1.procedure_id = phist2.procedure_id AND phist1.time_stamp < phist2.time_stamp) " + 
					"WHERE phist2.time_stamp IS NULL";
	
			// System.out.println("[INFO] SQLQuery: " + sqlQuery);
	
			resultSet = statement.executeQuery(sqlQuery);
			
			lpDaoList = new LinkedList<LastPosition>();
			
			while(resultSet.next()) {
				LastPosition lpDao = new LastPosition();
				lpDao.setProcedureID(resultSet.getString(1));
				lpDao.setTimeStamp(resultSet.getString(2));
				lpDao.setLatitude(resultSet.getString(3));
				lpDao.setLongtitude(resultSet.getString(4));
				
				lpDaoList.add(lpDao);
			}
		} finally {
			JDBCUtils.close(resultSet);
			JDBCUtils.close(statement);
			JDBCUtils.close(connection);
		}
		
		return lpDaoList;
	}

}
