package org.fbox.fusion.application.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

import org.fbox.common.application.data.ContextorSource;
import org.fbox.common.registry.RegistryInsertionError;
import org.fbox.util.HashMapHelper;

/**
 * Session Bean implementation class StreamerRegistry
 */
@Singleton
@LocalBean
public class InputOutputMapRegistry {
	
	
	private LinkedHashMap<String, ArrayList<String>> inputOutputMap;
	private LinkedHashMap<String, ArrayList<ContextorSource>>  outputInputMap;	

    /**
     * Default constructor. 
     */
    public InputOutputMapRegistry() {
    }

    @PostConstruct
    private void init() {
    	//System.out.println("Execute init...");
    	inputOutputMap=new LinkedHashMap<String,ArrayList<String>>();
    	outputInputMap=new LinkedHashMap<String,ArrayList<ContextorSource>>();
    }
    
    public HashMap<String, ArrayList<String>> getInputOutputMap() {
		return inputOutputMap;
	}

	public void setInputOutputMap(LinkedHashMap<String, ArrayList<String>> inputOutputMap) {
		this.inputOutputMap = inputOutputMap;
	}

	public HashMap<String, ArrayList<ContextorSource>> getOutputInputMap() {
		return outputInputMap;
	}

	public void setOutputInputMap(LinkedHashMap<String, ArrayList<ContextorSource>> outputInputMap) {
		this.outputInputMap = outputInputMap;
	}

	public synchronized  void addMapping(ContextorSource src, String dst) throws RegistryInsertionError {
    	
    	//update src-->dst map
    	ArrayList<String> existingSrcMapping=inputOutputMap.get(src.getId());
    	if (existingSrcMapping==null) {// a new src mapping
    		existingSrcMapping=new ArrayList<String>();
    		existingSrcMapping.add(dst);
    		inputOutputMap.put(src.getId(),existingSrcMapping);
    		System.out.println("1.A new destination(" + dst +") was added for src="+src);
    	} else { //a mapping for the specific src already exists 
    		if (!existingSrcMapping.contains(dst)) {
    			existingSrcMapping.add(dst);
    			System.out.println("2.A new destination(" + dst +") was added for src="+src);
    		} else
    			throw new RegistryInsertionError("A destination with the specific id ("+ dst +") already exists for the specified source("+ src +")");
    	}    

    	//update dst-->src map
    	ArrayList<ContextorSource> existingDstMapping=outputInputMap.get(dst);
    	if (existingDstMapping==null) {// a new dst mapping
    		existingDstMapping=new ArrayList<ContextorSource>();
    		existingDstMapping.add(src);
    		outputInputMap.put(dst,existingDstMapping);
    		System.out.println("1.A new source(" + src +") was added for dst="+dst);    		
    	} else { //a mapping for the specific dst already exists 
    		if (!existingDstMapping.contains(src)) {
    			existingDstMapping.add(src);
    			System.out.println("2.A new source(" + src +") was added for dst="+dst);   
    		} else
    			throw new RegistryInsertionError("A source with the specific id ("+ src +") has already been defined for the specified destination("+ dst +")");
    	}        		    		    	    	
    }
	
	public synchronized  void addSources(ArrayList<ContextorSource> sources, String dst) throws RegistryInsertionError {
		
		for (ContextorSource src : sources) {
			addMapping(src, dst);
		}		
	}

	public synchronized  void replaceSourceForDestination(String oldSrc, String newSrc, String dst) throws RegistryInsertionError {
		
    	ArrayList<String> existingSrcMapping=inputOutputMap.get(dst);
    	if (existingSrcMapping!=null) {
    		for (String src : existingSrcMapping) {
    			if (src.equals(oldSrc)) {
    				src=newSrc;
    				break;
    			}
    		}    			
    	}
    	
    	ArrayList<ContextorSource> existingDstMapping=outputInputMap.get(dst);
    	if (existingDstMapping!=null) {
    		for (ContextorSource d : existingDstMapping) {
    			if (d.getId().equals(oldSrc)) {
    				d.setId(newSrc);
    				break;
    			}
    		}       		
    	}    	    	
	}	
	
    public ArrayList<String> getDestinations(String src) {
    	return inputOutputMap.get(src);
    }

    public ArrayList<String> getSourcesAsString(String dst) {    	
    	ArrayList<String> result=null;
    	
    	ArrayList<ContextorSource> sources=outputInputMap.get(dst);
    	if (sources != null) {
        	result=new ArrayList<String>();
        	for (ContextorSource source : sources) {
        		result.add(source.getId());
        	}        	
    	}
    	return result;

    }            

    public ArrayList<ContextorSource> getSources(String dst) {
    	return outputInputMap.get(dst);
    }    
    
    
	public void printInputOutputMap() {
		new HashMapHelper().printSingleValueMap(inputOutputMap);
	}

	public void printOutputInputMap() {
		new HashMapHelper().printSingleValueMap(outputInputMap);
	}    
	
	public synchronized void clear() {
		inputOutputMap.clear();
		outputInputMap.clear();
	}
}
