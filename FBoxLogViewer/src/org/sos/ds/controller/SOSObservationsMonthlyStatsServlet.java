package org.sos.ds.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Comparator;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.jdbc.util.JDBCUtils;
import org.sos.ds.model.SOSObservationsStats;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class SOSMonthlyStatisticsServlet
 */
@WebServlet("/api/sos/stats/obs/monthly")
public class SOSObservationsMonthlyStatsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	// Master sos db
	@Resource(lookup="java:jboss/datasources/sos_me_DS")
	private DataSource sosDataSource; 
	
	// Slave sos db
	@Resource(lookup="java:jboss/datasources/sos_me_scenario_DS")
	private DataSource sosScenarioDataSource;
	
	// Request parameters
	private static final String SLAVE_SOS_DB_PARAM = "slavedb";
		
	// private static final String MASTER_SOS_DB_DESC = "master-sos-db";
	// private static final String SLAVE_SOS_DB_DESC = "slave-sos-db";
	private static final int SLAVE_SOS_DB_STATISTICS_DEFAULT = 0;
	

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		int slavedb = SLAVE_SOS_DB_STATISTICS_DEFAULT;
		
		// Get http parameters
		String slaveDbStr = request.getParameter(SLAVE_SOS_DB_PARAM);
		
		if(slaveDbStr != null) {
			slavedb = Integer.parseInt(slaveDbStr);
		}
		
		if (slavedb == 0 || slavedb == 1 ) {
			List<SOSObservationsStats> sosMonthlyStatsList = new LinkedList<SOSObservationsStats>();	
			try {
				if (slavedb == 0) {	
					sosMonthlyStatsList.addAll(getSosMonthlyStatistics(sosDataSource));
				} else {
					sosMonthlyStatsList.addAll(getSosMonthlyStatistics(sosScenarioDataSource));
				}
				
				// Sort result
				Collections.sort(sosMonthlyStatsList, new Comparator<SOSObservationsStats>() {
					@Override
					public int compare(SOSObservationsStats sosMonthly1, SOSObservationsStats sosMonthly2) {
						int sortDirection = -1; // asceding. Default sorting: descending
						return sosMonthly1.getTimestamp().compareTo(sosMonthly2.getTimestamp()) * sortDirection;
					}
				});
				
				// Format response
				ObjectMapper mapper = new ObjectMapper(); 
				String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(sosMonthlyStatsList);	
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.write(json);
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * 
	 */
	public List<SOSObservationsStats> getSosMonthlyStatistics (DataSource datasource) throws SQLException {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		
		// List that contains the phenomena metadata
		List<SOSObservationsStats> sosMonthlyStatsList = null;
		
		try {
			connection = datasource.getConnection();
			statement = connection.createStatement();
			
			String sqlQuery = "SELECT date_trunc('month', time_stamp) AS date, COUNT(observation_id) AS obs_count " +
					  		  "FROM observation " +
					  		  "GROUP BY date_trunc('month', time_stamp) " +
					  		  "ORDER BY date_trunc('month', time_stamp)";
			
			resultSet = statement.executeQuery(sqlQuery);
			
			sosMonthlyStatsList = new LinkedList<SOSObservationsStats>();
			
			while(resultSet.next()) {
				SOSObservationsStats sosMonthlyStat = new SOSObservationsStats(resultSet.getString(1), resultSet.getString(2));
				sosMonthlyStatsList.add(sosMonthlyStat);
			}	
			
		} finally {
			JDBCUtils.close(resultSet);
			JDBCUtils.close(statement);
			JDBCUtils.close(connection);
		}
		
		return sosMonthlyStatsList;
	}
}
