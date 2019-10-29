package org.fbox.fusion.algorithms.siso.ejb.impl.threshold;

public enum ComputationType {

	HARD, SOFT, IDENTITY;
	
	public static ComputationType select(String computation) {
		switch (computation.toUpperCase()) {
		case "HARD":
			return ComputationType.HARD;
		case "SOFT":			
			return ComputationType.SOFT;
		case "IDENTITY":
			return ComputationType.IDENTITY;			
		default:
			return null;
		}
	}
}
