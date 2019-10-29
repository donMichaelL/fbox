package org.fbox.common.network;

import java.util.Set;

import org.fbox.common.data.DataStream;
import org.fbox.common.network.data.DataStreamSelector;

public interface INetworkFusion {
	public Set<DataStream> registerDataStreamSource(String dataStremSourceId, DataStreamSelector dsSelector);
	public boolean deRegisterDataStreamSource(String dataStremSourceId);
	public void deRegisterSensor(String sensorId);
	public void deRegisterDataStream(String dataStreamId); 
}
