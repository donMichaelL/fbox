package org.fbox.common.xml.data;

import java.io.Serializable;

public class GenericDAO  implements Serializable {
	
	String id;
	
	protected String getId() {
		return id;
	}

	protected void setId(String id) {
		this.id = id;
	}
	
}
