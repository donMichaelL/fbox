package org.fbox.common.xml.data;

import java.util.Enumeration;

public class AlgorithmDAO extends GenericParameterizedDAO {
	
	public String getName() {
		return super.getId();
	}

	public void setName(String name) {
		super.setId(name);
	}
    
	@Override
	public String toString() {
		String sb="name="+getName();
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
		sb+="}";

		return sb;
	}

}
