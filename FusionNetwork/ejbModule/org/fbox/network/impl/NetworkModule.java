package org.fbox.network.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.Schedule;

import org.fbox.common.data.DataStream;
import org.fbox.common.data.IDataElement;
import org.fbox.common.network.IDataProvider;
import org.fbox.common.network.INetworkFusion;
import org.fbox.common.network.data.DataStreamSelector;
import org.fbox.network.IDataStreamProvider;
import org.fbox.network.INetworkModule;
import org.fbox.network.persistence.impl.NetworkStorageBean;
import org.fbox.network.persistence.impl.PullTimer;

@Stateless
@Remote(INetworkFusion.class)
@Local(INetworkModule.class)
public class NetworkModule implements INetworkModule, INetworkFusion {
	
	// Provides access to the actual data of DataStreams
	@EJB
	private IDataStreamProvider dataStreamProvider;

	// Registry with DataStreamSources
	@EJB
	private DataStreamSourceRegistry dataStreamSourceRegistry; 
	
	// Add storage functionality to NetwortkModule
	@EJB
	private NetworkStorageBean networkStorageBean;
	
	// Forwards data to other module/subsystems (e.g. queues) 
	@EJB
	private IDataProvider dataProvider; 
	
	// *** Constants ***/
	// The id of the timer. This timer is used in order to pull observations from the dataStreamProvider.
	private static final String PULL_TIMER_ID = "nm:pull-timer";
	
	
	// ******** Admin additions ****** // 
	private static HashMap<String, Integer> messagesNumberPerDataStream;
	private static HashMap<String, ArrayList<IDataElement>> messagesPerDataStream;
	
	public HashMap<String, Integer> getMessagesNumberPerDataStream() {
		return messagesNumberPerDataStream;
	}

	public HashMap<String, ArrayList<IDataElement>> getMessagesPerDataStream() {
		return messagesPerDataStream;
	}
	// *******************************// 
	
	public NetworkModule() {
	}
	
	
	@PostConstruct
	private void init() {
    	if(messagesNumberPerDataStream == null)
    		messagesNumberPerDataStream = new HashMap<String, Integer>();
    	if(messagesPerDataStream == null)
    		messagesPerDataStream = new HashMap<String, ArrayList<IDataElement>>();
		
		// Get the current date
		Date currentDate = new Date(System.currentTimeMillis());

		PullTimer pullTimer = new PullTimer();
		pullTimer.setTimerId(PULL_TIMER_ID);
		pullTimer.setTimestamp(currentDate);
		pullTimer.setDescription("Timer for pulling observations in Network Module.");
		
		networkStorageBean.initPullTimer(pullTimer);
	}
	
	
	// Periodic polling
	@Schedule (second="*/5", minute="*", hour="*", persistent=false)
	public void checkObservations() {
		
		System.out.println("--------------------------------------");
		System.out.println("[NM] Check for observations...");
		
		// Get the last timestamp of "PULL_TIMER_ID"
		PullTimer lastPullTimer = networkStorageBean.getPullTimer(PULL_TIMER_ID);
		
		// Get the last timestamp (stored in db)
		Date lastCheckedTimestamp = lastPullTimer.getTimestamp();
		
		Date currentTimestamp = new Date();
		
		// Get observations...in interval [lastCheckedTimestamp, currentTimestamp]
		System.out.println("[NM] Pull observations in interval [" + lastCheckedTimestamp.toString() + " , " + currentTimestamp.toString() + "]");
		ArrayList<IDataElement> deList = new ArrayList<IDataElement>();
		deList = pullObservations(lastCheckedTimestamp, currentTimestamp);
		
		// Post new observations...
		System.out.println("############SIZE is="+deList.size());
		if(deList.size() > 0) {

			// Update with the max timestamp
			lastCheckedTimestamp = deList.get(deList.size()-1).getTimestamp(); //getMaxTimestamp(deList);
			PullTimer pullTimer = new PullTimer();
			pullTimer.setTimerId(PULL_TIMER_ID);
			pullTimer.setTimestamp(lastCheckedTimestamp);
			pullTimer.setDescription("Updated");
			networkStorageBean.updatePullTimer(pullTimer);
			//System.out.println("[NM] Pulltimer updated to: " + lastCheckedTimestamp);
			
			//finally send the data
			dispatchObservations(deList);
		}
		
		System.out.println("[NM] Check for observations completed!");
		System.out.println("--------------------------------------");
	
	}
	
	public ArrayList<IDataElement> pullObservations (Date timeLimit1, Date timeLimit2) {
		System.out.println("[NM] Start pulling observations...");
		
		// Get sensorIds from registry
		// Set<String> sids = aggregatorRegistry.getSensorIds();
		
		Set<DataStream> dataStreams = dataStreamSourceRegistry.getDataStreams();
		
		// Get observations
		ArrayList<IDataElement> deList = new ArrayList<IDataElement>();
		if(dataStreams.size() > 0) {
			System.out.println("[NM] Getting data for " + dataStreams.size() + " registered data streams...");
			deList = dataStreamProvider.getObservations(dataStreams, timeLimit1, timeLimit2);
		}
		else {
			System.out.println("[NM] No registered data streams for getting data.");
		}
		
		return deList;
	}
	
	public void dispatchObservations (ArrayList<IDataElement> deList) {
		System.out.println("[NM] Posting " + deList.size() + " messages to queue...");
		
		for(IDataElement de : deList) {
	    	synchronized (messagesNumberPerDataStream) {
	       		Integer numberOfMeasurements=messagesNumberPerDataStream.get(de.getId());
	    	   		if (numberOfMeasurements!=null) {
	    	   			messagesNumberPerDataStream.put(de.getId(), ++numberOfMeasurements);
	    	   		} else {
	    	   			messagesNumberPerDataStream.put(de.getId(), new Integer(1));
	    	    	}
	        	}
	    	
	    	synchronized (messagesPerDataStream) {
	       		ArrayList<IDataElement> measurements=messagesPerDataStream.get(de.getId());
	    	   		if (measurements!=null) {
	    	   			measurements.add(de);
	    	   		} else {
	    	   			messagesPerDataStream.put(de.getId(), new ArrayList<IDataElement>());
	    	    	}
	        	}
	    	
			dataProvider.addDataInQueue(de.getId(), de);
		}
		
		System.out.println("[NM]: End of posting!");
	}

	
	
	@Override
	public Set<DataStream> registerDataStreamSource(String dataStreamSourceId, DataStreamSelector dsSelector) {
		
		Set<DataStream> dataStreams = new HashSet<DataStream>();
		dataStreams = dataStreamProvider.getDataStreams(dsSelector);
		
		System.out.println("[NM] Registering DataStreamSource " + dataStreamSourceId + " with " + dataStreams.size() + " data streams...");
		
		// if(dataStreams.size() != 0) {
			dataStreamSourceRegistry.addDataStreamSource(new DataStreamSource(dataStreamSourceId, dsSelector, dataStreams));
		//}
		//else 
			if(dataStreams.size() == 0)
				System.out.println("[NM] - [WARN] - Empty data stream set for DataStreamSource with id " + dataStreamSourceId + " !");
		
		return dataStreams;
	}

	@Override
	public boolean deRegisterDataStreamSource(String dataStreamSourceId) {
		
		boolean status = false;
		
		if(dataStreamSourceRegistry.getDataStreamSources().get(dataStreamSourceId) != null) { 
			System.out.println("[NM] Deregistering DataStreamSource " + dataStreamSourceId + " with " + dataStreamSourceRegistry.getDataStreamSources().get(dataStreamSourceId).getDataStreams().size() + " data streams...");
	
			if(dataStreamSourceRegistry.removeDataStreamSource(dataStreamSourceId) == true) {
				
				System.out.println("[NM] DataStreamSource " + dataStreamSourceId + " deregistered successfully!");
				status = true;
			} 	// else {
				// System.out.println("[NM] - [WARN] Deregistering DataStreamSource " + dataStreamSourceId + "  failed!");
				// status = false;
				// }	
		} else {
			System.out.println("[NM] Deregistering DataStreamSource " + dataStreamSourceId + " . The specified DataStreamSource does not exists!");
		}
		
		return status;
	}

	@Override
	public void deRegisterDataStream(String dataStreamId) {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public void deRegisterSensor(String sensorId) {
		// TODO Auto-generated method stub
		
	}


	
}
