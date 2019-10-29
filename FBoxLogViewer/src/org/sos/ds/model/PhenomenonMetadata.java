package org.sos.ds.model;

import java.util.HashSet;


public class PhenomenonMetadata {
	private String phenomenonId;
	private String unitOfMeasurement;
	
	
	
	public PhenomenonMetadata(String phenomenonId, String unitOfMeasurement) {
		this.phenomenonId = phenomenonId;
		this.unitOfMeasurement = unitOfMeasurement;
	}
	
	public PhenomenonMetadata(PhenomenonMetadata pm) {
		this.phenomenonId = pm.phenomenonId;
		this.unitOfMeasurement = pm.unitOfMeasurement;
	}

	public String getPhenomenonId() {
		return phenomenonId;
	}
	
	public void setPhenomenonId(String phenomenonId) {
		this.phenomenonId = phenomenonId;
	}
	
	public String getUnitOfMeasurement() {
		return unitOfMeasurement;
	}
	
	public void setUnitOfMeasurement(String unitOfMeasurement) {
		this.unitOfMeasurement = unitOfMeasurement;
	}

	// "hashCode" only with phenomenonId property
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((phenomenonId == null) ? 0 : phenomenonId.hashCode());
		/* result = prime
				* result
				+ ((unitOfMeasurement == null) ? 0 : unitOfMeasurement
						.hashCode()); */
		return result;
	}

	// "equals" only with phenomenonId property
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PhenomenonMetadata other = (PhenomenonMetadata) obj;
		if (phenomenonId == null) {
			if (other.phenomenonId != null)
				return false;
		} else if (!phenomenonId.equals(other.phenomenonId))
			return false;
		/*
		if (unitOfMeasurement == null) {
			if (other.unitOfMeasurement != null)
				return false;
		} else if (!unitOfMeasurement.equals(other.unitOfMeasurement))
			return false;
		*/
		return true;
	}
	
	
	
	

}
