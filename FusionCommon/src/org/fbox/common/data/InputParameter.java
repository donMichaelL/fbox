package org.fbox.common.data;

import java.io.Serializable;

public class InputParameter implements Comparable<InputParameter>, Serializable {

	String name;
	String value;

	public InputParameter(String name, String value) {
		super();
		this.name = name;
		this.value = value;
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

	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof InputParameter)
			return this.name.equals(((InputParameter)obj).name);
		else
			return false;
	}

	@Override
	public int compareTo(InputParameter arg0) {
		return this.name.compareTo(arg0.name);
	}
	
}
