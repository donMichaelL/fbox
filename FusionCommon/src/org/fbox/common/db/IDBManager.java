package org.fbox.common.db;

import java.util.Set;

public interface IDBManager {
	public Set<ConfigurationParameter> getConfigParameters();
	public ConfigurationParameter getConfigParameter(String paramName);
	public int updateConfigParameter(ConfigurationParameter configParameter);
	public int updateConfigParameter(String paramName, String value);
}
