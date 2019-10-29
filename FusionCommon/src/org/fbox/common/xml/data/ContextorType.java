package org.fbox.common.xml.data;

import java.io.Serializable;

public enum ContextorType implements Serializable{

	SINGLE, MULTI;
	
	public static ContextorType select(String type) {
		switch (type.toUpperCase()) {
		case "SINGLE":
			return ContextorType.SINGLE;
		case "MULTI":			
			return ContextorType.MULTI;			
		default:
			return ContextorType.SINGLE;
		}
	}	
}
