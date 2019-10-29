package org.fbox.common.xml.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import org.fbox.common.data.InputParameter;

public class GenericParameterizedDAO extends GenericDAO {
	
	Properties parameters=new Properties();
	
	public Properties getParameters() {
		return parameters;
	}

	public void setParameters(Properties params) {
		this.parameters = params;
	}

	public void addParameter(String name, String value) {
		parameters.put(name, value);
	}
	
	public String getParameter(String name) {
		return parameters.getProperty(name);
	}
	
	public void addParameterList(HashMap<String, String> params) {
		this.parameters.putAll(params);
	}
	
    public HashMap<String, InputParameter> getInputParameters() {
    	HashMap<String, InputParameter> iparams=null;
    	if (parameters!=null) {
    		iparams=new HashMap<String,InputParameter>();
    		Set<Object> keys=parameters.keySet();
    		for (Object key : keys) {
    			iparams.put((String)key, new InputParameter((String)key, parameters.getProperty((String)key)));
    		}
    	}
    	return iparams;
    }	
	
}
