package org.fbox.fusion.persistence.impl;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;

public class Adapter implements Serializable {
	private String adapterId;
	private String jndi;
	
	public String getAdapterId() {
		return this.adapterId;
	}
	
	public void setAdapterId(String adapterId) {
		this.adapterId = adapterId;
	}
	
	public String getJndi() {
		return jndi;
	}
	
	public void setJndi(String jndi) {
		this.jndi = jndi;
	}
	
	
	@Override
	public String toString() {
		return "Adapter [Id=" + this.adapterId + ", jndi=" + this.jndi + "]";
	}
	
}
