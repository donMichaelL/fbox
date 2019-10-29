package org.fbox.network;

import java.util.ArrayList;
import java.util.HashMap;

import org.fbox.common.data.IDataElement;

public interface INetworkModule {
	
	// Checks for new observations
	public void checkObservations();
	
	// Dispatch observations to the Message Queue 
	public void dispatchObservations (ArrayList<IDataElement> deList);
	
	public HashMap<String, Integer> getMessagesNumberPerDataStream();
	public HashMap<String, ArrayList<IDataElement>> getMessagesPerDataStream();
	
}
