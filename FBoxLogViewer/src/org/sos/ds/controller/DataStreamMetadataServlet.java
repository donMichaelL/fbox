package org.sos.ds.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.jdbc.util.JDBCUtils;
import org.sos.ds.model.DataStreamMetadata;
import org.sos.ds.model.LastObservation;
import org.sos.setup.SetupConstants;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class SensorStreamMetadataServlet
 */
@WebServlet("/api/datastreams")
public class DataStreamMetadataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	// Master sos db
	@Resource(lookup="java:jboss/datasources/sos_me_DS")
	private DataSource sosDataSource;   
	
	// Slave sos db
	@Resource(lookup="java:jboss/datasources/sos_me_scenario_DS")
	private DataSource sosScenarioDataSource;  
	
	// Boolean flag for master/slave db mode (default value: false)
	private String isEnabledScenarioDdStr;
	
	// Servlet Parameters
	private static final String PHENOMENA_ID_PARAM = "phenId";
	private static final String LAST_VALUE_THRESHOLD_PARAM = "lvThreshold";
	private static final String OPERATOR_PARAM = "op";
	

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
	 * GET method 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// Get http parameters
		String phenIdParam = request.getParameter(PHENOMENA_ID_PARAM);
		String lastValueThresholdStr = request.getParameter(LAST_VALUE_THRESHOLD_PARAM);
		String operatorStr = request.getParameter(OPERATOR_PARAM);
		
		if (phenIdParam != null && lastValueThresholdStr != null && operatorStr != null) {
			// Parse threshold value
			double lastValueThreshold = Double.parseDouble(lastValueThresholdStr);
			
			// Parse operator
			String operator = "";
			switch(operatorStr) {
				case "gt":
					operator = ">";
					break;
				case "lt":
					operator = "<";
					break;
				case "eq":
					operator = "=";
					break;
				case "gteq":
					operator = ">=";
					break;
				case "lteq":
					operator = "<=";
					break;
				default: 
					operatorStr = "gt";
					throw new IllegalArgumentException("Invalid operator: " + operatorStr);
			}
			
			try {
				// Get all registered datastreams (filtered by phenomenonId) from master db
				Map<String, DataStreamMetadata> dsMetadataMap = new HashMap<String, DataStreamMetadata>();
				dsMetadataMap.putAll(getDataStreamsMetadata(sosDataSource, phenIdParam));
				
				// Get active datasreams through last observation (filtered by phenomenonId and threshold value)
				List<LastObservation> lastObsList = new LinkedList<LastObservation>();
				lastObsList.addAll(getLastObservations(sosDataSource, phenIdParam, operator, lastValueThreshold));
				
				// ^^^^^^^^^^^^^^^ Slave DB ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ //
				// Get all registered datastreams (filtered by phenomenonId)
				// Get active datasreams through last observation (filtered by phenomenonId and threshold value)
				if (this.isEnabledScenarioDdStr.equals("true")) {
					dsMetadataMap.putAll(getDataStreamsMetadata(sosScenarioDataSource, phenIdParam));
					lastObsList.addAll(getLastObservations(sosScenarioDataSource, phenIdParam, operator, lastValueThreshold));
				}
				// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ //
				
				// Create result list
				List<DataStreamMetadata> dsFilteredList = new LinkedList<DataStreamMetadata>();
				
				// Iterate active datastreams through last observations				
				for(LastObservation lastObs :lastObsList) {
					// Search and remove the datastream from the HasMap (key: procedureId) 
					DataStreamMetadata dsMetadata = dsMetadataMap.remove(lastObs.getProcedureId());
					
					if(dsMetadata != null) {
						// Enrich datastream metadata with last value and timestamp
						dsMetadata.setTimeStamp(lastObs.getTimeStamp());
						dsMetadata.setValue(lastObs.getValue());
						
						// Add to result list
						dsFilteredList.add(dsMetadata);
					}				
				}
				
				// ~~~~~ Format response in json ~~~~~ //
				ObjectMapper mapper = new ObjectMapper(); 
				String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dsFilteredList);
							
				// System.out.println(json);
				
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.write(json);
			} catch (SQLException e) {
				e.printStackTrace();
			}				
		}		
	}
	
	/**
	 * Get all registered datastreams (filtered by phenomenonId)
	 * @param phenomenonId
	 * @return
	 * @throws SQLException
	 */
	public Map<String, DataStreamMetadata> getDataStreamsMetadata(DataSource dataSource, String phenomenonId) throws SQLException {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		
		Map<String, DataStreamMetadata> dsMetadataMap = null;
		
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement(); 
			
			String sqlQuery = "SELECT phist.procedure_id, phen.phenomenon_id, ST_Y(phist.position) AS latitude, ST_X(phist.position) AS longitude, phist.active, phist.mobile " +
					   "FROM (" +
					   		"SELECT procedure_id, MAX(time_stamp) AS lastTimestamp " +
					   		"FROM procedure_history phist " +
					   		"GROUP BY procedure_id" +
					   	") AS procLastPosition, procedure_history phist, proc_phen pp, phenomenon phen " +
					   	"WHERE phist.procedure_id = procLastPosition.procedure_id " + 
						"AND phist.time_stamp = procLastPosition.lastTimestamp " + 
						"AND pp.phenomenon_id  = phen.phenomenon_id " + 
						"AND phist.procedure_id = pp.procedure_id " + 
						"AND pp.phenomenon_id = '" + phenomenonId + "' " +
						"ORDER BY phist.procedure_id";
		
			// System.out.println("[INFO] SQLQuery: " + sqlQuery);
			
			resultSet = statement.executeQuery(sqlQuery);
			
			dsMetadataMap = new HashMap<String, DataStreamMetadata>();
			
			while(resultSet.next()) {
				DataStreamMetadata dsMetadata = new DataStreamMetadata();
				dsMetadata.setProcedureId(resultSet.getString(1));
				dsMetadata.setPhenomenonId(resultSet.getString(2));
				dsMetadata.setLatitude(resultSet.getString(3));
				dsMetadata.setLongtitude(resultSet.getString(4));
				dsMetadata.setActive(resultSet.getBoolean(5));
				dsMetadata.setMobile(resultSet.getBoolean(6));
				
				dsMetadataMap.put(dsMetadata.getProcedureId(), dsMetadata);
			}
			
		} finally {
			JDBCUtils.close(resultSet);
			JDBCUtils.close(statement);
			JDBCUtils.close(connection);
		}
		
		return dsMetadataMap;
		
	}
	
	/**
	 * Get active datastreams through last observation (filtered by phenomenonId and threshold value)
	 * @param phenomenonId
	 * @param threshold
	 * @return
	 * @throws SQLException
	 */
	public List<LastObservation> getLastObservations(DataSource dataSource, String phenomenonId, String operator, double threshold) throws SQLException {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		
		List<LastObservation> lObsList = null;
		
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement(); 
	
			// First, find max per procedure. Assumption all data streams of a procedure has the same max timestamp.
			String sqlQuery =  "SELECT obs.time_stamp, obs.procedure_id, obs.phenomenon_id, obs.text_value, obs.numeric_value, obs.spatial_value " +
					"FROM (" +
						"SELECT procedure_id, MAX(time_stamp) AS lastTimestamp " +
						"FROM observation " + 
						"GROUP BY procedure_id" +
					") AS proclast, observation obs " +
					"WHERE obs.procedure_id = proclast.procedure_id AND obs.time_stamp = proclast.lastTimestamp " +
					"AND obs.phenomenon_id = '" + phenomenonId + "' " + 
					"AND obs.numeric_value " + operator + " " + threshold;
					
			// System.out.println("[INFO] SQLQuery: " + sqlQuery);
			
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
