package org.fbox.network.impl;

import java.util.HashSet;
import java.util.Set;

import org.fbox.common.data.DataStream;
import org.fbox.common.network.data.DataStreamSelector;

public class DataStreamSource {
	
	private String id;
	private DataStreamSelector dataStreamSelector;
	private Set<DataStream> dataStreams; // Data streams according to "dataStreamSelector" 
	
	
	public DataStreamSource(String id, DataStreamSelector dataStreamSelector, Set<DataStream> dataStreams) {
		this.id = id;
		this.dataStreamSelector = dataStreamSelector;
		this.dataStreams = new HashSet<DataStream>();
		this.dataStreams = dataStreams;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public DataStreamSelector getDataStreamSelector() {
		return dataStreamSelector;
	}
	
	public void setDataStreamSelector(DataStreamSelector dataStreamSelector) {
		this.dataStreamSelector =dataStreamSelector;
	}

	public Set<DataStream> getDataStreams() {
		return dataStreams;
	}

	public void setDataStreams(Set<DataStream> dataStreams) {
		this.dataStreams = dataStreams;
	}

	@Override
	public String toString() {
		return "DataStreamSource [id=" + id + ", dataStreamSelector="
				+ dataStreamSelector + ", dataStreams=" + dataStreams + "]";
	}
	
	

}
