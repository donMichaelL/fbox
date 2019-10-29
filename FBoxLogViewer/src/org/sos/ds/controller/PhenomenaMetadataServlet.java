package org.sos.ds.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.jdbc.util.JDBCUtils;
import org.sos.ds.model.PhenomenonMetadata;
import org.sos.setup.SetupConstants;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class PhenomenaServlet
 */
@WebServlet("/api/phenomena")
public class PhenomenaMetadataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
      
	@Resource(lookup="java:jboss/datasources/sos_me_DS")
	private DataSource sosDataSource;   // Master sos db
	
	@Resource(lookup="java:jboss/datasources/sos_me_scenario_DS")
	private DataSource sosScenarioDataSource;  // Slave sos db
	
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
		try {
			// ~~~~~ Get phenomena from master db ~~~~~ // 
			List<PhenomenonMetadata> pmList = new LinkedList<PhenomenonMetadata>();
			pmList.addAll(getPhenomenaMetadata(sosDataSource));
			
			
			// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ //
			// ~~~~~ Get phenomena from slave db ~~~~~ //
			if (this.isEnabledScenarioDdStr.equals("true"))
				pmList.addAll(getPhenomenaMetadata(sosScenarioDataSource));
			// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ //
			
			// Convert List (with duplicates) to Set (without duplicates)
			Set<PhenomenonMetadata> pmSet = new HashSet<PhenomenonMetadata>(pmList);
			
			
			/* Future extension: Filter response according to requested phenomena 
			if(phenIds != null) {
				// See ProcedureMetadataServlet  
			} */
			
			// Convert Set to List (without duplicates)
			List<PhenomenonMetadata> pmListWithoutDuplicates = new LinkedList<PhenomenonMetadata>(pmSet);
			
			// Sort result
			Collections.sort(pmListWithoutDuplicates, new Comparator<PhenomenonMetadata>() {
				@Override
				public int compare(PhenomenonMetadata pm1, PhenomenonMetadata pm2) {
					// int sortDirection = -1; // asceding. Default sorting: descending
					return pm1.getPhenomenonId().compareTo(pm2.getPhenomenonId()); // * sortDirection
				}
			});
						
			// Format response to json and send response
			ObjectMapper mapper = new ObjectMapper(); 
			String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(pmListWithoutDuplicates);
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.write(json);
			out.flush();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param datasource
	 * @return
	 * @throws SQLException
	 */
	public List<PhenomenonMetadata> getPhenomenaMetadata(DataSource datasource) throws SQLException {
		
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		
		// List that contains the phenomena metadata
		List<PhenomenonMetadata> pmList = null;	
		
		try {
			connection = datasource.getConnection();
			statement = connection.createStatement(); 
			
			String sqlQuery = "SELECT * " +
							  "FROM phenomenon"; 
			
			resultSet = statement.executeQuery(sqlQuery);
			
			pmList = new LinkedList<PhenomenonMetadata>();
			
			while(resultSet.next()) {
				PhenomenonMetadata pm = new PhenomenonMetadata(resultSet.getString(1), resultSet.getString(3));
				pmList.add(pm);
			}		
		} finally {
			JDBCUtils.close(resultSet);
			JDBCUtils.close(statement);
			JDBCUtils.close(connection);
		}
		
		return pmList;		
	}
}
