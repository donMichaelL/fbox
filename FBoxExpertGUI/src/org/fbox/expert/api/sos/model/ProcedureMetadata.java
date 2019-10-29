package org.fbox.expert.api.sos.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ProcedureMetadata {
	private String procedureId;
	private Set<String> phenomena;
	private String latitude;
	private String longtitude;
	private boolean isActive;
	private boolean isMobile;
	
	public ProcedureMetadata() {
		phenomena = new HashSet<String>();
	}
	
	public ProcedureMetadata(String procedureId, String latitude, String longtitude, boolean isActive, boolean isMobile) {
		this.phenomena = new HashSet<String>();
		this.procedureId = procedureId;
		this.latitude = latitude;
		this.longtitude = longtitude;
		this.isActive = isActive;
		this.isMobile = isMobile;
	}
	
	public ProcedureMetadata(DataStreamMetadata dsm) {
		this.procedureId = dsm.getProcedureId();
		this.phenomena = new HashSet<String>();
		this.latitude = dsm.getLatitude();
		this.longtitude = dsm.getLongtitude();
		this.isActive = dsm.isActive();
		this.isMobile = dsm.isMobile();
	}
	
	public String getProcedureId() {
		return procedureId;
	}
	
	public void setProcedureId(String procedureId) {
		this.procedureId = procedureId;
	}
	
	public Set<String> getPhenomena() {
		return phenomena;
	}
	
	public void setPhenomena(Set<String> phenomena) {
		this.phenomena = phenomena;
	}
	
	public String getLatitude() {
		return latitude;
	}
	
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	public String getLongtitude() {
		return longtitude;
	}
	
	public void setLongtitude(String longtitude) {
		this.longtitude = longtitude;
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public boolean isMobile() {
		return isMobile;
	}
	
	public void setMobile(boolean isMobile) {
		this.isMobile = isMobile;
	}
	
	
	
	
}
