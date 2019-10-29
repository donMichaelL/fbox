package org.sos.ds.unsorted;

public class LastPosition {
	private String procedureID;
	private String timeStamp;
	private String latitude;
	private String longtitude;
	
	
	public LastPosition() {
		
	}
	
	public LastPosition(String procedureID, String timeStamp,
			String latitude, String longtitude) {
		super();
		this.procedureID = procedureID;
		this.timeStamp = timeStamp;
		this.latitude = latitude;
		this.longtitude = longtitude;
	}

	public String getProcedureID() {
		return procedureID;
	}
	
	public void setProcedureID(String procedureID) {
		this.procedureID = procedureID;
	}
	
	public String getTimeStamp() {
		return timeStamp;
	}
	
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
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
	
	@Override
	public String toString() {
		return "LastPositionDAO [procedureID=" + procedureID + ", timeStamp="
				+ timeStamp + ", latitude=" + latitude + ", longtitude="
				+ longtitude + "]";
	}
	
	
	
}
