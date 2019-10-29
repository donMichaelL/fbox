package org.fbox.network;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import org.fbox.common.data.DataStream;
import org.fbox.common.data.IDataElement;
import org.fbox.common.network.data.DataStreamSelector;

public interface IDataStreamProvider {
	
	// Retrieve all registered data streams. 
	public Set<DataStream> getDataStreams();
	
	// Retrieve a set of data streams according to the specified DataStreamSelector.
	public Set<DataStream> getDataStreams(DataStreamSelector dsSelector);
	
	// Retrieve observations for the specified data streams in the interval [minTimestamp, maxTimestamp]
	public ArrayList<IDataElement> getObservations(Set<DataStream> dataStreamSet, Date minTimestamp, Date maxTimestamp);

}
