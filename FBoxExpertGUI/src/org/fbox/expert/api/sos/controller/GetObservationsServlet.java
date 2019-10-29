package org.fbox.expert.api.sos.controller;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.fbox.expert.api.sos.model.Observation;
import org.fbox.expert.util.JDBCUtils;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Servlet implementation class LastObservationsServlet
 */
@WebServlet("/api/sos/getObservations")
public class GetObservationsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
     
	@Resource(lookup="java:jboss/datasources/sos_me_DS")
	private DataSource sosDataSource;
	
	private static final String PROCEDURE_ID_PARAM = "procId";
	private static final String PHENOMENON_ID_PARAM = "phenId";
	private static final String OBSERVATIONS_LIMIT_PARAM = "limit";
	private static final int OBSERVATIONS_LIMIT_DEFAULT = 1;
	private static final int OBSERVATIONS_LIMIT_MAX = 100;
	
	// Test constants
	private static final String TEST_PROCEDURE_ID = "urn:ogc:object:feature:Sensor:IDIRA:WEATHER:STATION:73";
	private static final String TEST_PHENOMENON_ID = "urn:ogc:def:phenomenon:OGC:1.0.30:humidity";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    //public GetObservationsServlet() {
    //    super();
        // TODO Auto-generated constructor stub
    //}

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
					
					if(obsLimit > OBSERVATIONS_LIMIT_MAX)
						obsLimit = OBSERVATIONS_LIMIT_MAX;
				}
				
				try {
					List<Observation> obsList = getObservations(procId, phenId, obsLimit);
					
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

	public List<Observation> getObservations(String procedureId, String phenomenonId, int obsLimit) throws SQLException {
		
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		
		List<Observation> obsList = null;	// List that contains the observations
		
		try {
			connection = sosDataSource.getConnection();
			statement = connection.createStatement(); 
			
			String sqlQuery = "SELECT obs.time_stamp, obs.text_value, obs.numeric_value, obs.spatial_value " +
					"FROM observation obs " +
					"WHERE obs.procedure_id = '" + procedureId + "' " +
					"AND phenomenon_id = '" + phenomenonId + "' " +
					"ORDER BY obs.time_stamp DESC " +
					"LIMIT " + obsLimit;
		
			// System.out.println("[INFO] SQLQuery: " + sqlQuery);
			
			resultSet = statement.executeQuery(sqlQuery);
			
			obsList = new LinkedList<Observation>();
			
			while(resultSet.next()) {
				Observation obs = new Observation();
				obs.setTimeStamp(resultSet.getString(1));
				
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
