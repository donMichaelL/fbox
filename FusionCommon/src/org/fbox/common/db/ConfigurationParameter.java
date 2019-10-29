package org.fbox.common.db;

public class ConfigurationParameter {
	protected String name;
	protected String value;
	protected String description;
	
	public ConfigurationParameter() {
	
	}
	
	public ConfigurationParameter(String name, String value, String description) {
		super();
		this.name = name;
		this.value = value;
		this.description = description;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "ConfigParameter [name=" + name + ", value=" + value
				+ ", description=" + description + "]";
	}
	
	
	
	
}
