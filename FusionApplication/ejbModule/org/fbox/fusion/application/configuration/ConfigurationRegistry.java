package org.fbox.fusion.application.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import org.fbox.common.application.data.ContextorSource;
import org.fbox.common.data.DataStream;
import org.fbox.common.exception.ApplicationConfigurationException;
import org.fbox.util.BidirectionalMultiValueHashMap;
import org.fbox.util.HashMapHelper;

public class ConfigurationRegistry {
	
	//the list of configured streamers
	public HashMap<String, Set<DataStream>> configuredStreamers;		
	public LinkedHashMap<String, ArrayList<String>> initialInputToOutputContextorMap=new LinkedHashMap<String, ArrayList<String>>();
	public  LinkedHashMap<String, ArrayList<ContextorSource>> initialOutputToInputContextorMap=new LinkedHashMap<String, ArrayList<ContextorSource>>();	
	//holds the final list of generated Contextors IDs for each initial Contextor ID
	public BidirectionalMultiValueHashMap<String, String> finalInputToOutputMap=new BidirectionalMultiValueHashMap<String, String>();
	public BidirectionalMultiValueHashMap<String, ContextorSource> finalOutputToInputMap=new BidirectionalMultiValueHashMap<String, ContextorSource>();
	//holds the final list of generated Contextors IDs for each initial Contextor ID
	public BidirectionalMultiValueHashMap<String, String> generatedContextorMap=new BidirectionalMultiValueHashMap<String, String>();

	public void setStreamerConfiguration(HashMap<String, Set<DataStream>> streamerConfiguration) throws ApplicationConfigurationException{
		if (this.configuredStreamers!=null)
			throw new ApplicationConfigurationException("Streamer Configuration has already been set. It can be set only once"); 
		else {			
			this.configuredStreamers = streamerConfiguration;						
		}
	}

	public HashMap<String, ArrayList<String>> getInitialInputToOutputMap() {
		return initialInputToOutputContextorMap;
	}

	public HashMap<String, ArrayList<ContextorSource>> getInitialOutputToInputMap() {
		return initialOutputToInputContextorMap;
	}

	public HashMap<String, ArrayList<String>> getInputToOutputContextorMap() {
		LinkedHashMap<String, ArrayList<String>> mapToReturn=new LinkedHashMap<String, ArrayList<String>>();
		if (mapToReturn!=null) {
			Set<String> keys=finalInputToOutputMap.keySet();
			for (String key : keys) {
				mapToReturn.put(key,finalInputToOutputMap.get(key));
			}
		}
		
		return mapToReturn;
	}

	public HashMap<String, ArrayList<ContextorSource>> getOutputToInputContextorMap() {
		LinkedHashMap<String, ArrayList<ContextorSource>> mapToReturn=new LinkedHashMap<String, ArrayList<ContextorSource>>();
		if (mapToReturn!=null) {
			Set<String> keys=finalOutputToInputMap.keySet();
			for (String key : keys) {
				mapToReturn.put(key,finalOutputToInputMap.get(key));
			}
		}
		
		return mapToReturn;
	}

	public HashMap<String, ArrayList<String>> getGeneratedContextorsMap() {
		LinkedHashMap<String, ArrayList<String>> mapToReturn=new LinkedHashMap<String, ArrayList<String>>();
		if (mapToReturn!=null) {
			Set<String> keys=generatedContextorMap.keySet();
			for (String key : keys) {
				mapToReturn.put(key,generatedContextorMap.get(key));
			}
		}
		
		return mapToReturn;
	}	
	
	
			        
	public void printOutputToInputContextorMap() {
		new HashMapHelper().printMultiValueMap(getOutputToInputContextorMap());
	}

	public void printInputToOutputContextorMap() {
		new HashMapHelper().printMultiValueMap(getInputToOutputContextorMap());
	}

	public void printGeneratedContextorMap() {
		new HashMapHelper().printMultiValueMap(getGeneratedContextorsMap());
	}
	    
	public void clear() {
		//initialize registries
		configuredStreamers=null;
		initialInputToOutputContextorMap.clear();
		initialOutputToInputContextorMap.clear();
		finalInputToOutputMap.clear();
		finalOutputToInputMap.clear();
		generatedContextorMap.clear();
	}
	
    /*    
    public void printInputToOutputMap() {
    	finalInputToOutputMap.printKeyToValueMap();
    }

    public void printOutputTotInputMap() {
    	finalOutputToInputMap.printKeyToValueMap();
    }*/
}
