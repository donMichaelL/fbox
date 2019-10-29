package org.fbox.common.xml.data;

import java.util.ArrayList;


public class OutputDAO extends GenericDAO {
	
	FormatterDAO formatter;
	AdapterDAO adapter;
	ArrayList<SourceDAO> sources;
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
	
	public FormatterDAO getFormatter() {
		return formatter;
	}

	public void setFormatter(FormatterDAO formatter) {
		this.formatter = formatter;
	}

	public AdapterDAO getAdapter() {
		return adapter;
	}

	public void setAdapter(AdapterDAO adapter) {
		this.adapter = adapter;
	}

	public ArrayList<SourceDAO> getSources() {
		return sources;
	}

	public void setSources(ArrayList<SourceDAO> sources) {
		this.sources = sources;
	}

	public SourceDAO getSource(String sourceId) {
		for (SourceDAO source : sources) {
			if (source.getId().equals(sourceId))
				return source;
		}
		
		System.out.println("here!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+sourceId);
		return null;
	}
	
	public String toString() {
		return "id="+this.id + ", formatter=["+ this.formatter +"], adapter=["+this.adapter+"]" + ", sources={"+ this.sources +"}";
	}
	
}
