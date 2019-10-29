package org.fbox.common.network;

import java.util.HashMap;

import org.fbox.common.data.IDataElement;

public interface IDataProvider {
	  public void addDataInQueue(String dataStreamId, IDataElement data);
	  public HashMap<String, Integer> getMessagesPerDataStream();
}
