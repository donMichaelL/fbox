package org.fbox.fusion.output.formatter.data;

import java.util.ArrayList;

public class AccountedArea {
	
	private ArrayList<AreaPoint> areaTileIDs;
	private Double propValue;
	private Double thresholdedPropValue;
	
	public AccountedArea() {
		areaTileIDs = new ArrayList<AreaPoint>();
		propValue = new Double(0.0);
		thresholdedPropValue = new Double(0.0);
	}

	public ArrayList<AreaPoint> getAreaTileIDs() {
		return areaTileIDs;
	}

	public void setAreaTileIDs(ArrayList<AreaPoint> areaTileIDs) {
		this.areaTileIDs = areaTileIDs;
	}

	public void addAreaTileID(AreaPoint point) {
		this.areaTileIDs.add(point);
	}
	
	public AreaPoint getAreaTileID(int i) {
		return this.areaTileIDs.get(i);
	}
	
	public Double getPropValue() {
		return propValue;
	}

	public void setPropValue(Double propValue) {
		this.propValue = propValue;
	}

	public Double getThresholdedPropValue() {
		return thresholdedPropValue;
	}

	public void setThresholdedPropValue(Double thresholdedPropValue) {
		this.thresholdedPropValue = thresholdedPropValue;
	}
}
