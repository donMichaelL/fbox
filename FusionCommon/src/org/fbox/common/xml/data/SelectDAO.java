package org.fbox.common.xml.data;

import java.io.Serializable;

public class SelectDAO implements Serializable{
	String type;
	String value;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "type="+type +", value="+value;
	}
}
