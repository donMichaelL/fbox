package org.fbox.persistence.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.fbox.common.db.ConfigurationParameter;

@Entity
@Table (name="tbl_config")
public class ConfigParameter extends ConfigurationParameter {

	public ConfigParameter() {
	
	}
	
	public ConfigParameter(String name, String value, String description) {
		super(name, value, description);
	}
	
	@Id
	@Column(name="param_name")
	public String getName() {
		return super.getName();
	}
	
	@Column(name="value")
	public String getValue() {
		return super.getValue();
	}
	
	@Column(name="description")
	public String getDescription() {
		return super.getDescription();
	}
	
}
