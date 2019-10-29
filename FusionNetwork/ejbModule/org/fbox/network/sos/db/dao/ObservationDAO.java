package org.fbox.network.sos.db.dao;

import java.io.Serializable;
import java.util.Date;

import com.vividsolutions.jts.geom.Geometry;

public class ObservationDAO implements Serializable {
	
	// private Timestamp timeStamp;
	private Date timeStamp;
	private String procedureId;
	private String featureOfInterestId;
	private String phenomenonId;
	private String offeringId;
	private String textValue;
	private double numericValue;
	private Geometry spatialValue;
	private String mimeType;
	private String observationId;
	
	// This field is not present in the Observation table
	private Geometry positionOfRetrieval;
	
	
	public ObservationDAO() {
		
	}
	
	
	public ObservationDAO(Date timeStamp, String procedureId, String featureOfInterestId,
			String phenomenonId, String offeringId, String textValue, double numericValue,
			Geometry spatialValue, String mimeType, String observationId) {
		
		this.timeStamp = timeStamp;
		this.procedureId = procedureId;
		this.featureOfInterestId = featureOfInterestId;
		this.phenomenonId = phenomenonId;
		this.offeringId = offeringId;
		this.textValue = textValue;
		this.numericValue = numericValue;
		this.spatialValue = spatialValue;
		this.mimeType = mimeType;
		this.observationId = observationId;
	}
	
	public Date getTimeStamp() {
		return timeStamp;
	}
	
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public String getProcedureId() {
		return procedureId;
	}
	
	public void setProcedureId(String procedureId) {
		this.procedureId = procedureId;
	}
	
	public String getFeatureOfInterestId() {
		return featureOfInterestId;
	}
	
	public void setFeatureOfInterestId(String featureOfInterestId) {
		this.featureOfInterestId = featureOfInterestId;
	}
	
	public String getPhenomenonId() {
		return phenomenonId;
	}
	
	public void setPhenomenonId(String phenomenonId) {
		this.phenomenonId = phenomenonId;
	}
	
	public String getOfferingId() {
		return offeringId;
	}
	
	public void setOfferingId(String offeringId) {
		this.offeringId = offeringId;
	}
	
	public String getTextValue() {
		return textValue;
	}
	
	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}
	
	public double getNumericValue() {
		return numericValue;
	}
	
	public void setNumericValue(double numericValue) {
		this.numericValue = numericValue;
	}
	
	public Geometry getSpatialValue() {
		return spatialValue;
	}
	
	public void setSpatialValue(Geometry spatialValue) {
		this.spatialValue = spatialValue;
	}
	
	public String getMimeType() {
		return mimeType;
	}
	
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	public String getObservationId() {
		return observationId;
	}
	
	public void setObservationId(String observationId) {
		this.observationId = observationId;
	}
	
	public String toString() {
		String valueStr;
		
		if(this.textValue != null)
			valueStr = this.textValue;
		else if (!Double.isNaN(this.numericValue)) {
			valueStr = Double.toString(this.numericValue);
		}
		else {
			valueStr = this.spatialValue.toString();
		}
		
		return this.timeStamp + " | " + this.procedureId + " | " + valueStr;
				
	}

}
