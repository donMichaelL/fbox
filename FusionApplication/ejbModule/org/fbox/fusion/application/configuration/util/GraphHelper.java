package org.fbox.fusion.application.configuration.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.fbox.common.application.data.ContextorSource;

public class GraphHelper {

	   public static Graph createGraphFromOutputInput(HashMap<String, ArrayList<ContextorSource>> sourcesToOuputMap) {
	        
	    	Graph graph = new Graph();
	    	
	    	Set<Entry<String, ArrayList<ContextorSource>>> nodesMap=sourcesToOuputMap.entrySet();
	    	for (Entry<String, ArrayList<ContextorSource>> node : nodesMap) {
	    		String destinationNode= node.getKey();
	    		for (ContextorSource sourceNode : node.getValue()) {
	    			graph.addEdge(sourceNode.getId(), destinationNode);
	    		}
	    	}
	     	
			return graph;    	
	    }


	   public static Graph createGraphFromInputOutput(HashMap<String, ArrayList<String>> nodesMap) {
	        
	    	Graph graph = new Graph();
	    	
	    	Set<Entry<String, ArrayList<String>>> nodes=nodesMap.entrySet();
	    	for (Entry<String, ArrayList<String>> node : nodes) {
	    		String sourceNode= node.getKey();
	    		for (String destinationNode : node.getValue()) {
	    			graph.addEdge(destinationNode, sourceNode);
	    		}
	    	}
	     	
			return graph;    	
	    }
}
