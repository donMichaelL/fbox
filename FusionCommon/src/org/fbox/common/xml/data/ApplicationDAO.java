package org.fbox.common.xml.data;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class ApplicationDAO extends GenericDAO {

	String description;
	LinkedHashMap<String, StreamerSelectorDAO> streamers;
	LinkedHashMap<String, ContextorDAO> contextors;
	LinkedHashMap<String, OutputDAO> output;
			
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public HashMap<String, StreamerSelectorDAO> getStreamers() {
		return streamers;
	}

	public void setStreamers(LinkedHashMap<String, StreamerSelectorDAO> streamers) {
		this.streamers = streamers;
	}

	public HashMap<String, ContextorDAO> getContextors() {
		return contextors;
	}

	public void setContextors(LinkedHashMap<String, ContextorDAO> contextors) {
		this.contextors = contextors;
	}

	public HashMap<String, OutputDAO> getOutput() {
		return output;
	}

	public void setOutput(LinkedHashMap<String, OutputDAO> output) {
		this.output = output;
	}
	
	
}
