package org.fbox.common.data;

import java.io.Serializable;
import java.util.HashMap;


public interface IContext extends Serializable {
	public HashMap<String, Object> getContextEntries();
	public Object getContextParameter(String key);
	public void setContextParameter(String name, Object value);
	public void clear();
}
