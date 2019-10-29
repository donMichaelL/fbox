package org.fbox.common.xml.data;

import java.util.Enumeration;

public class AdapterDAO extends GenericParameterizedDAO {
	
	public String getType() {
		return super.getId();
	}

	public void setType(String type) {
		super.setId(type);
	}	
    
	@Override
	public String toString() {
		String sb="(type="+getType();
		Enumeration<Object> names= getParameters().keys();
		int size=parameters.size();
		sb+=", iparams={";
		while (names.hasMoreElements()) {
			size--;
			String name=(String)names.nextElement();
			sb+="[name="+ name + ", value=" +parameters.getProperty(name)+"]";
			if (size!=0)
				sb+=", ";				
		}
		sb+="})";

		return sb;
	}
}
