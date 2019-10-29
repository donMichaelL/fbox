package org.fbox.fusion.persistence.impl;

import java.io.Serializable;

public class Algorithm implements Serializable {
	
	private String algorithmId;
	private String jndi;
	private int layer;
		
	public String getAlgorithmId() {
		return algorithmId;
	}
	
	public void setAlgorithmId(String algorithmId) {
		this.algorithmId = algorithmId;
	}
	
	public String getJndi() {
		return jndi;
	}
	
	public void setJndi(String jndi) {
		this.jndi = jndi;
	}
	
	public int getLayer() {
		return layer;
	}
	
	public void setLayer(int layer) {
		this.layer = layer;
	}
	
	@Override
	public String toString() {
		return "Algorithm [algorithmId=" + this.algorithmId + ", jndi=" + this.jndi
				+ ", layer=" + this.layer + "]";
	}
	

}
