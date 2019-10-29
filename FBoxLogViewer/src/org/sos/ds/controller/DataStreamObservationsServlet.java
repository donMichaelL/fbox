package org.sos.ds.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

import org.sos.ds.model.Observation;
import org.sos.setup.SetupConstants;
import org.jdbc.util.JDBCUtils;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Servlet implementation class DataStreamObservationsServlet
 * Returns the observations () for a specific 
 */
@WebServlet("/api/observations")
public class DataStreamObservationsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
     
	@Resource(lookup="java:jboss/datasources/sos_me_DS")
	private DataSource sosDataSource;  // Primary sos db
	
	@Resource(lookup="java:jboss/datasources/sos_me_scenario_DS")
	private DataSource sosScenarioDataSource;  // Scenario sos db
	
	// Request parameters
	private static final String PROCEDURE_ID_PARAM = "procId";
	private static final String PHENOMENON_ID_PARAM = "phenId";
	private static final String OBSERVATIONS_LIMIT_PARAM = "limit";
	
	// Limit the observations in response
	private static final int OBSERVATIONS_LIMIT_DEFAULT = 1;
	private static final int OBSERVATIONS_LIMIT_MAX = 1000;
	
	// Boolean flag for master/slave db mode (default value: false)
	private String isEnabledScenarioDdStr;
		
	/*
	 * Init is guaranteed to be called before the servlet handles its first request.
	 * Also, it is used for one-time initialization 
	 */
	@Override
	public void init() throws ServletException {
		try {
			Properties props = new Properties();
			InputStream inStream = getServletContext().getResourceAsStream("/WEB-INF/" + SetupConstants.SOS_DB_PROPERTIES_FILENAME);
			props.load(inStream);
			this.isEnabledScenarioDdStr = props.getProperty(SetupConstants.SOS_ME_SCENARIO_DB_PROPERTY);   
			System.out.println("[FBoxLogViewer] Scenario db enabled: " + isEnabledScenarioDdStr);
			inStream.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
			int obsLimit = OBSERVATIONS_LIMIT_DEFAULT;
			
			// Get parameters
			String procId = request.getParameter(PROCEDURE_ID_PARAM);
			String phenId = request.getParameter(PHENOMENON_ID_PARAM);
			String obsLimitParam = request.getParameter(OBSERVATIONS_LIMIT_PARAM);
			
			if (procId != null && phenId != null) {
				
				if (obsLimitParam != null) {
					obsLimit = Integer.parseInt(obsLimitParam);
					
					// Limit the number of observations in response
					if(obsLimit > OBSERVATIONS_LIMIT_MAX)
						obsLimit = OBSERVATIONS_LIMIT_MAX;
				}
				
				try {
					// Get observations from primary db
					List<Observation> obsList = new LinkedList<Observation>(); 
					obsList.addAll(getObservations(sosDataSource, procId, phenId, obsLimit));			
					
					// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ //		
					// ~~~~~ Get observations from slave db ~~~~~ //
					if (this.isEnabledScenarioDdStr.equals("true"))
						obsList.addAll(getObservations(sosScenarioDataSource, procId, phenId, obsLimit));   
					// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ //
				
					
					// ~~~~~ Response ~~~~~ //
					// Format response
					ObjectMapper mapper = new ObjectMapper(); 
					String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obsList);
				
					// Send response
					response.setContentType("application/json");
					PrintWriter out = response.getWriter();
					out.write(json);
				
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
	
	}

	/**
	 * 
	 * @param dataSource
	 * @param procedureId
	 * @param phenomenonId
	 * @param obsLimit
	 * @return
	 * @throws SQLException
	 */
	public List<Observation> getObservations(DataSource dataSource, String procedureId, String phenomenonId, int obsLimit) throws SQLException {
		
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		
		List<Observation> obsList = null;	// List that contains the observations
		
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement(); 
			
			String sqlQuery = "SELECT obs.time_stamp, obs.text_value, obs.numeric_value, obs.spatial_value " +
					"FROM observation obs " +
					"WHERE obs.procedure_id = '" + procedureId + "' " +
					"AND phenomenon_id = '" + phenomenonId + "' " +
					"ORDER BY obs.time_stamp DESC " +
					"LIMIT " + obsLimit;
		
//			 System.out.println("[INFO] SQLQuery: " + sqlQuery);
			
			resultSet = statement.executeQuery(sqlQuery);
			
			obsList = new LinkedList<Observation>();
			
			while(resultSet.next()) {
				Observation obs = new Observation();
				// TimeZone for SOS representation
				String timeStampWithTimeZone = resultSet.getString(1) + "00";
				obs.setTimeStamp(timeStampWithTimeZone);
				
				if (resultSet.getString(2) != null)
					obs.setValue(resultSet.getString(2));
				else if (resultSet.getString(3) != null)
					obs.setValue(resultSet.getString(3));
				else if (resultSet.getObject(4) != null)
					obs.setValue(resultSet.getObject(4).toString());
				else 
					System.out.println("[Observation] [WARN] No value to set!");
				
				// Add observation
				obsList.add(obs);
			}
			
		} finally {
			JDBCUtils.close(resultSet);
			JDBCUtils.close(statement);
			JDBCUtils.close(connection);
		}
		
		return obsList;
	}
}

