package org.fbox.common.data;

public enum DataType {

	NUMERIC, CATEGORY, SPATIAL;
	
	public static DataType select(String type) {
		switch (type.toUpperCase()) {
		case "NUMERIC":
			return DataType.NUMERIC;
		case "TEXT":			
			return DataType.CATEGORY;
		case "SPATIAL":
			return DataType.SPATIAL;			
		default:
			return null;
		}
	}
	
}
