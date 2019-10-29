package org.fbox.network.sos;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.fbox.common.data.DataStream;
import org.fbox.common.data.IDataElement;
import org.fbox.common.network.data.DataStreamSelector;
import org.fbox.common.network.data.MeasuredDataElement;
import org.fbox.network.IDataStreamProvider;
import org.fbox.network.sos.db.SosMobileExtDBManager;
import org.fbox.network.sos.db.dao.ObservationDAO;

import com.vividsolutions.jts.geom.Geometry;

@Stateless
@Local ({IDataStreamProvider.class})
@LocalBean
public class SosDataStreamProvider implements IDataStreamProvider {
	
	@EJB
	private SosMobileExtDBManager dbManager;

	@Override
	public Set<DataStream> getDataStreams() {
		return null;
	}

	@Override
	public Set<DataStream> getDataStreams(DataStreamSelector dsSelector) {
		
		Set<DataStream> dataStreams = new HashSet<DataStream>();
		
		// TODO: Convert internal representation of SOS data model to DataStream
		// dbManager should not return DataStream objects. It should be independent from 
		// the implementation details of NetworkModule
		try {
			dataStreams = dbManager.getDataStreams(dsSelector);
		} catch (SQLException e) {
			System.out.println("[SDSP] - [ERROR] Error in retrieving data streams.");
			e.printStackTrace();
		}
		
		return dataStreams;
	}

	@Override
	public ArrayList<IDataElement> getObservations(Set<DataStream> dataStreams, Date minTimeStamp, Date maxTimeStamp) {
		
		ArrayList<IDataElement> deList = new ArrayList<IDataElement>();
		
		if(dataStreams.size() > 0) {
			ArrayList<ObservationDAO> obsList = new ArrayList<ObservationDAO>();
			
			try {
				obsList = dbManager.getObservations(dataStreams, minTimeStamp, maxTimeStamp);
			} catch (SQLException e) {
				System.out.println("[SDSP] - [ERROR] Error in retrieving observations.");
				e.printStackTrace();
			}
			
			System.out.println("[SSDP]:#Observations: " + obsList.size());
			
			// Convert each ObservationDAO to IDataElement...
			for (ObservationDAO obs : obsList) {
				System.out.println("[SSDP]: " + obs.toString());
				
				// **************** procedureId!phenomenonID ******* //
				// Get sensorId
				String sid = obs.getProcedureId() + "!" + obs.getPhenomenonId();
				// ******************************************************************* //
				
				// Get observation's timestamp
				Date date = obs.getTimeStamp();	
				
				// Get values
				String textValue = obs.getTextValue();
				Double numericValue = obs.getNumericValue();
				Geometry spatialValue = obs.getSpatialValue();
				
				MeasuredDataElement mde = new MeasuredDataElement(sid);
				if(!numericValue.isNaN()) {
					mde.setValue(numericValue);
				} else if(textValue != null) {
					mde.setValue(textValue);
				} else if (spatialValue!=null) {
					mde.setValue(spatialValue);
				} else {
					//TODO throw an exception
				}
				mde.setLocation(null); // TODO: Position of retrieval (null) should be specified.
				mde.setTimestamp(date);
				
				// Message for the queue
				deList.add(mde);
				
				// Message for the queue
				//deList.add(mde);
			}
		}
		
		return deList;
	}

}
