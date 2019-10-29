package org.fbox.network.sos.db;

import java.awt.List;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import org.fbox.common.data.DataStream;
import org.fbox.common.data.DataType;
import org.fbox.common.network.data.DataStreamSelector;
import org.fbox.network.sos.db.dao.ObservationDAO;
import org.fbox.network.sos.util.DateTimeUtils;
import org.postgresql.util.PGobject;
// import org.postgis.PGgeometry;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

@Stateless
@Local ({ISosDBManager.class})
@LocalBean
public class SosMobileExtDBManager implements ISosDBManager {
	
	@Resource(lookup="java:jboss/datasources/sos_me_DS")
//	@Resource(lookup="java:jboss/datasources/sos_me_scenario_DS")
	private DataSource sosDataSource;
	
	
	public String assembleGetDataStreamsQuery(DataStreamSelector dsSelector) {
		String sqlQuery = null;
		
		String selectStatement = "SELECT pp.procedure_id, phen.phenomenon_id, phen.valuetype ";
		String fromStatement   = "FROM proc_phen pp, phenomenon phen ";
		String whereStatement  = "WHERE pp.phenomenon_id  = phen.phenomenon_id";
		
		// Constraint on specific phenomenon
		if(dsSelector.getPhenomenon() != null) {
			String phenomenonStrContsraint = " AND phen.phenomenon_id='" + dsSelector.getPhenomenon() + "'";
			
			// System.out.println(phenomenonStrContsraint);
			whereStatement = whereStatement + phenomenonStrContsraint;
		}
		
		// Constraint on specific sensor IDs
		Set<String> sensorIdsSet = dsSelector.getSensorIds();
		if(sensorIdsSet != null) {
			Iterator<String> iterator = sensorIdsSet.iterator();
			
			String sensorIdsStrConstraint = " AND (pp.procedure_id = '" + iterator.next() + "'";
		
			while(iterator.hasNext()) {
				sensorIdsStrConstraint = sensorIdsStrConstraint + " OR pp.procedure_id = '" + iterator.next() + "' "; 
			}
				
			sensorIdsStrConstraint = sensorIdsStrConstraint + ")";
			System.out.println(sensorIdsStrConstraint);
			
			whereStatement = whereStatement + sensorIdsStrConstraint;
		}
		
		// Spatial constraint
		if(dsSelector.getSpatial() != null) {
			fromStatement = fromStatement + ", procedure proc ";
			whereStatement = whereStatement + " AND pp.procedure_id = proc.procedure_id";
			
			String spatialStrConstraint = " AND ST_WITHIN(proc.actual_position, ST_GeomFromText('" +
					dsSelector.getSpatial() + "',4326))";
						
			whereStatement = whereStatement + spatialStrConstraint;
		}
		
		sqlQuery =  selectStatement + fromStatement + whereStatement;
		
		return sqlQuery;
	}
	
	public String assembleGetObservationsQuery(Set<DataStream> dataStreams, Date minTimestamp, Date maxTimestamp) { // dataStreams values may change during deployment phase ---> use a local list instead!! (avoid concurrent modification exceptions)
		String sqlQuery = null;
		
		String queryPrefix = "SELECT * FROM observation";
		String whereStr = "";
		
		Set<DataStream> localDataStreams = new HashSet<DataStream>();
		localDataStreams.addAll(dataStreams);
		
		// ~~~ sensorIds and phenomenon constraint ~~~
		Iterator<DataStream> iterator = localDataStreams.iterator();
		
		if(iterator.hasNext()) { 
			
			whereStr = " WHERE ";
			
			DataStream  ds = iterator.next();
			
			whereStr = whereStr + "((procedure_id = '" + ds.getSensorId() + "'";
			
			if(ds.getPhenomenon() != null)
				whereStr = whereStr + " AND phenomenon_id='" + ds.getPhenomenon() + "')";
			
			while(iterator.hasNext()) {
				
				ds = iterator.next();
				
				whereStr = whereStr + " OR " ;
				
				whereStr = whereStr + "(procedure_id = '" + ds.getSensorId() + "'";
				
				if(ds.getPhenomenon() != null)
					whereStr = whereStr + " AND phenomenon_id='" + ds.getPhenomenon() + "')";
				
			}
			
			whereStr = whereStr + ")";
		}
		
		// TODO: Supports  only the same phenomenon for all sid. Do we want to support sensor streams with different phneomenon?  
		// Phenomenon restriction
		//if (sensorStreamSet.size() > 0) {
		//	whereStr = whereStr + " AND phenomenon_id='" + sensorStreamSet.iterator().next().getPhenomenon() + "'";
		//}
		
		// time constraint
		SimpleDateFormat sdf = new SimpleDateFormat(DateTimeUtils.DATE_FORMAT);
			
		if(maxTimestamp != null)
			whereStr = whereStr + " AND time_stamp <= TIMESTAMP WITH TIME ZONE '" + sdf.format(maxTimestamp) + "'";
		
		if(minTimestamp != null) {
			whereStr = whereStr + " AND time_stamp > TIMESTAMP WITH TIME ZONE '" + sdf.format(minTimestamp) + "'"; 
		}
		
		String querySuffix = " ORDER BY time_stamp ASC";
		
		sqlQuery = queryPrefix + whereStr + querySuffix;
		
		return sqlQuery;
	}
	
	@Override
	public Set<DataStream> getDataStreams(DataStreamSelector dsSelector) throws SQLException {
		
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		Set<DataStream> dataStreams = new HashSet<DataStream>();
		
		try {
			connection = sosDataSource.getConnection();
			statement = connection.createStatement();
		
			String sqlQuery = assembleGetDataStreamsQuery(dsSelector);
			
			//System.out.println("[DBM] SQLQuery: " + sqlQuery);
			
			// Execute query
			resultSet = statement.executeQuery(sqlQuery);
			while (resultSet.next()) {
				
				DataType sensorType;
				if(resultSet.getString(3).equals("numericType")) {
					 sensorType = DataType.NUMERIC;
				} else if(resultSet.getString(3).equals("textType")) {
					sensorType = DataType.CATEGORY;
				} else if (resultSet.getString(3).equals("textType")) {
					sensorType = DataType.SPATIAL;
				} else {
					sensorType = null;
				}
				
				// TODO: Unique id for dataStream is set as "procedureId!phenomenonId"
				DataStream dataStream = new DataStream(resultSet.getString(1) + "!" + resultSet.getString(2), resultSet.getString(1), resultSet.getString(2), sensorType);
				dataStreams.add(dataStream);
			}

		} finally {
			JDBCUtils.close(resultSet);
			JDBCUtils.close(statement);
			JDBCUtils.close(connection);
		}
		
		return dataStreams;
	}
	
	
	@Override
	public ArrayList<ObservationDAO> getObservations(Set<DataStream> dataStreams, Date minTimestamp, Date maxTimestamp) throws SQLException { 
		
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		ArrayList<ObservationDAO> obsList = new ArrayList<ObservationDAO>();
		
		try {
			connection = sosDataSource.getConnection();
			statement = connection.createStatement();
		
		
			// Poll SQL query
			String sqlQuery = assembleGetObservationsQuery(dataStreams, minTimestamp, maxTimestamp);
			
			// Print sqlQuery
			// System.out.println("[SMEDBM] [INFO] SQLQuery: " + sqlQuery);
			
			// Execute query
			resultSet = statement.executeQuery(sqlQuery);
		
			while (resultSet.next()) {
				ObservationDAO obs = new ObservationDAO();
				obs.setTimeStamp(resultSet.getTimestamp(1));
				obs.setProcedureId(resultSet.getString(2));
				obs.setFeatureOfInterestId(resultSet.getString(3));
				obs.setPhenomenonId(resultSet.getString(4));
				obs.setOfferingId(resultSet.getString(5));
				
				// ********** text_value (6) ********** //
				obs.setTextValue(resultSet.getString(6));
				
				// ********** numeric_value (7) ********* //
				System.out.println("[INFO] Retrieving numeric_value... ");
				
				double d = resultSet.getDouble(7);
				if(resultSet.wasNull())	// Check if the last column read had a value of SQL NULL
					obs.setNumericValue(Double.NaN);
				else
					obs.setNumericValue(d);
				
				// ********** spatial_value (8) ********* //
				System.out.println("[INFO] Retrieving spatial value....");
				
				PGobject obj = (PGobject) resultSet.getObject(8);
				if(resultSet.wasNull())
					obs.setSpatialValue(null);
				else {
					String spatialValueInEwkt = obj.toString();
					// System.out.println("===> spatialValueInEWKT: " + spatialValueInEwkt);
					
					Geometry geom = PostGISUtils.EwktToGeometry(spatialValueInEwkt);
					obs.setSpatialValue(geom);
				}
				
				
				// Spatial value
				// PGgeometry pgGeom = (PGgeometry) resultSet.getObject(8);
				// System.out.println("Type: " + pgGeom.getType());
				// System.out.println("Value: " + pgGeom.getValue());
				
				obs.setMimeType(resultSet.getString(9));
				obs.setObservationId(resultSet.getString(10));
				
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
