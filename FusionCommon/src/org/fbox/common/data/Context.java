package org.fbox.common.data;

import java.util.HashMap;

public class Context implements IContext {

	HashMap<String, Object> contextMap;
	
	public Context() {
		contextMap=new HashMap<String, Object>();
	}
	

	@Override
	public HashMap<String, Object> getContextEntries() {
		return contextMap;
	}

	@Override
	public Object getContextParameter(String key) {
		return contextMap.get(key);
	}

	@Override
	public void setContextParameter(String name, Object value) {
		contextMap.put(name, value);
	}
	
	@Override
	public String toString() {
		return contextMap+":"+contextMap;
	}

	@Override
	public void clear() {
		contextMap.clear();
	}
}
