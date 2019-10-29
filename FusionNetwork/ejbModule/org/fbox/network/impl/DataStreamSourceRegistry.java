package org.fbox.network.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.fbox.common.data.DataStream;
import org.fbox.common.data.DataType;
import org.fbox.common.network.data.DataStreamSelector;

@Startup
@Singleton
public class DataStreamSourceRegistry {

	// The mapping between the unique dataStreamSourceId and the DataStreamSource
	private HashMap<String, DataStreamSource> dataStreamSources; 
	
	// TODO: HashMap....map dataStream with dataStreamSources i.e. <DataStream, List<dataStreamSourceId>
	// The set with all dataStreams of the DataStreamSourceRegistry
	private Set<DataStream> dataStreams; 
	
	// The mapping between the dataStreamId and the DataStreamSources (i.e. one DataStream could correspond to one or more DataStreamSource(s)).
	// e.g. dataStream1 -> {dataStreamSource2, dataStreamSource4}
	// private HashMap<String, Set<String>> dataStreamMap;
	private DataStreamMap dataStreamMap;
	
	// private String getObservationsQuery; // TODO: Assemble this query only once (useful for STATIC data streams)
	
	public DataStreamSourceRegistry() {
		dataStreamSources = new HashMap<String, DataStreamSource>();
		dataStreams = new HashSet<DataStream>();
		
		dataStreamMap = new DataStreamMap();
	}
	
	public Set<DataStream> getDataStreams() {
		return dataStreams;
	}

	public void setDataStreams(Set<DataStream> dataStreams) {
		this.dataStreams = dataStreams;
	}
	
	public HashMap<String, DataStreamSource> getDataStreamSources() {
		return dataStreamSources;
	}

	public void setDataStreamSources(
			HashMap<String, DataStreamSource> dataStreamSources) {
		this.dataStreamSources = dataStreamSources;
	}
	
	public DataStreamMap getDataStreamMap() {
		return this.dataStreamMap;
	}
	
	
	public boolean existsDataStreamSource(String dataStreamSourceId) {
		if(dataStreamSources.containsKey(dataStreamSourceId))
			return true;
		else
			return false;
	}

	/**
	 * Adds a DataStreamSource in the DataStreamSourceRegistry.
	 * @param dataStreamSource the DataStreamSource to be added 
	 */
	public synchronized void addDataStreamSource(DataStreamSource dataStreamSource) {
		
		if(dataStreamSource != null) {
			System.out.println("[DSS-REGISTRY] Adding DataStreamSource..." + dataStreamSource.getId());
			
			String dataStreamSourceId = dataStreamSource.getId();
			
			if(!dataStreamSources.containsKey(dataStreamSourceId)) {
				
				// Add the new dataStreamSource to registry
				dataStreamSources.put(dataStreamSourceId, dataStreamSource);
				
				// Update the set of unique data streams in the registry
				System.out.println("Streams to add..." + dataStreamSource.getDataStreams());
				dataStreams.addAll(dataStreamSource.getDataStreams());
				
				// Update the mapping between  dataStreams and dataStreamSources
				// For each DataStream update the appropriate entry
				// i.e. dataStreamIdi => {dataStreamSourceId1,...,dataStreamSourceIdn}
				Set<DataStream> dsSet = dataStreamSource.getDataStreams();
				for(DataStream ds : dsSet) {
					dataStreamMap.addDataStreamSource(ds.getId(), dataStreamSourceId);
				}
				
				System.out.println("[DSS-REGISTRY] DataStreamSource " + dataStreamSource.getId() + " inserted successfully!");
			}
			else {
				System.out.println("[DSS-REGISTRY] DataStreamSource with id: " + dataStreamSourceId + " has already been defined!");
			}
			
		}
		
		System.out.println("*******************************************");
		System.out.println(dataStreamSources);
		System.out.println("*******************************************");
	}
	
	/**
	 * Removes a DataStreamSource with the specified  dataStreamSourceId
	 * @param dataStreamSourceId the unique id of the DataStreaSource to be removed
	 */
	public synchronized boolean removeDataStreamSource(String dataStreamSourceId) {
		if(existsDataStreamSource(dataStreamSourceId)) {
			
			// Remove the specific dataStreamSource from "dataStreamSources"
			DataStreamSource dss = dataStreamSources.remove(dataStreamSourceId);
			
			// Update the "dataStreamMap" by updating the mapping between dataStreamIds and the specific dataStreamSourceId
			// For each DataStream update the appropriate entry...
			Set<DataStream> dsSet = dss.getDataStreams();
			for(DataStream ds : dsSet) {
				if(dataStreamMap.removeDataStreamSource(ds.getId(), dataStreamSourceId) == true) {

					// If there is not another mapping for the specific dataStreamId in the "dataStreamMap", 
					// update the "dataStreams" set (i.e., remove the dataStream)
					if(dataStreamMap.getDataStreamIndex().get(ds.getId()) == null) {
						// System.out.println("[DSS-Registry] Removing dataStream " + ds.getId() + " ...");
						if(dataStreams.contains(ds))
							dataStreams.remove(ds);
							System.out.println("[DSS-Registry] DataStream " + ds.getId() + " removed successfully!" );
					}
				}
			}
			return true;
		}
		else {
			System.out.println("[DSS-REGISTRY] DataStreamSource with id: " + dataStreamSourceId + " does not exists in order to be removed!");
			return false;
		}
		
	}
	
	// **************************************** //
	// ********* FOR TESTING PURPOSES ********* //
	// **************************************** //
	public static void main(String[] args) {
		System.out.println("Starting...");

		DataStreamSourceRegistry registry = new DataStreamSourceRegistry();
		
		Set<String> sids = new HashSet<String>();
		sids.add("sid1");
		sids.add("sid2");
		DataStreamSelector selector = new DataStreamSelector("p1", sids, null, false);
		
		// Add the 1rst DataStreamSource
		Set<DataStream> dStreams = new HashSet<DataStream>();
		dStreams.add(new DataStream("stream1","sid1", "phen1",DataType.NUMERIC));
		DataStreamSource source1 = new DataStreamSource("source1", selector, dStreams);
		registry.addDataStreamSource(source1);
		
		// Add the 2nd DataStreamSource
		Set<DataStream> dStreams2 = new HashSet<DataStream>();
		dStreams2.add(new DataStream("stream1","sid1", "phen1",DataType.NUMERIC));
		DataStreamSource source2 = new DataStreamSource("source2", selector, dStreams2);
		registry.addDataStreamSource(source2);
		
		// Prints....
		System.out.println("#DataStreams: " + registry.getDataStreams().size());
		System.out.println(registry.getDataStreamMap().toString());
		
		
	}
}
