package org.fbox.fusion.persistence.impl;

import java.io.Serializable;

public class Formatter implements Serializable {
	private String formatterId;
	private String jndi;
	
	public String getFormatterId() {
		return this.formatterId;
	}
	
	public void setFormatterId(String id) {
		this.formatterId = id;
	}
	
	public String getJndi() {
		return jndi;
	}
	
	public void setJndi(String jndi) {
		this.jndi = jndi;
	}

	@Override
	public String toString() {
		return "Formatter [id=" + formatterId + ", xslt=" + jndi + ", toString()="
				+ super.toString() + "]";
	}
	
	
}
