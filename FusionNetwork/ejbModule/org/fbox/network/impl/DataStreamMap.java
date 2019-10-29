package org.fbox.network.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DataStreamMap {

	private HashMap<String, Set<String>> dataStreamIndex;
	
	public DataStreamMap() {
		dataStreamIndex = new HashMap<String, Set<String>>();
	}
	
	
	public HashMap<String, Set<String>> getDataStreamIndex() {
		return dataStreamIndex;
	}


	public synchronized void addDataStreamSource(String dataStreamId, String dataStreamSourceId) {
		Set<String> dataSreamSourceSet = null;
		
		if(!dataStreamIndex.containsKey(dataStreamId)) {
			dataSreamSourceSet = new HashSet<String>();
			//new DataStreamMapEntry(String dataStreamId, String dataStreamSourceId)
		} else {
			dataSreamSourceSet = dataStreamIndex.get(dataStreamId);
		}
		
		if (dataSreamSourceSet.add(dataStreamSourceId) == true)
			dataStreamIndex.put(dataStreamId, dataSreamSourceSet);
	}
	
	public synchronized boolean removeDataStreamSource(String dataStreamId, String dataStreamSourceId) {
		
		// After the removal of the dataStreamSourceId, check if the dataStreamId has no other sources
		Set<String> dataSreamSourceSet = dataStreamIndex.get(dataStreamId);
		
		if(dataSreamSourceSet != null) {
			if(dataSreamSourceSet.remove(dataStreamSourceId) == true){
				if(dataSreamSourceSet.isEmpty()) {
					removeDataStream(dataStreamId);
				}
				
				return true;
			} else {
				System.out.println("[DSM] - [WARN] No mapping for dataStreamSourceId " + dataStreamSourceId + " found!" );
			}
			
		} else {
			System.out.println("[DSM] - [WARN] No mapping for dataStreamId " + dataStreamId + " found!" );
		}
		
		return false;
		
	}
	
	public synchronized void removeDataStream(String dataStreamId) {
		dataStreamIndex.remove(dataStreamId);
	}
	
	@Override
	public String toString() {
		return "DataStreamMap [dataStreamMap=" + dataStreamIndex + "]";
	}


	public static void main(String[] args) {
		System.out.println("Hello");

		DataStreamMap dsm = new DataStreamMap();
		
		dsm.addDataStreamSource("ds100", "source101");
		System.out.println(dsm.toString());
		
		dsm.addDataStreamSource("ds100", "source102");
		System.out.println(dsm.toString());
		
		dsm.removeDataStreamSource("ds100", "source101");
		System.out.println(dsm.toString());
		
		dsm.removeDataStreamSource("ds100", "source102");
		System.out.println(dsm.toString());
		
		/*
		dsm.addDataStreamSource("ds200", "source201");
		System.out.println(dsm.toString());
		
		dsm.addDataStreamSource("ds200", "source202");
		System.out.println(dsm.toString());
		
		dsm.removeDataStreamSource("ds200", "source201");
		System.out.println(dsm.toString());
		
		dsm.removeDataStreamSource("ds200", "source202");
		System.out.println(dsm.toString());
		*/
		
	}

}
