package org.fbox.common.xml.data;

import java.io.Serializable;

public enum CombineType implements Serializable{

	SEQUENTIAL, PAIRED;

	public static CombineType select(String type) {
		switch (type.toUpperCase()) {
		case "SEQUENTIAL":
			return CombineType.SEQUENTIAL;
		case "PAIRED":			
			return CombineType.PAIRED;			
		default:
			return CombineType.SEQUENTIAL;
		}
	}	
}
