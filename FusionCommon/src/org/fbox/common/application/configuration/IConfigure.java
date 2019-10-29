package org.fbox.common.application.configuration;

import org.fbox.common.exception.ApplicationConfigurationException;
import org.fbox.common.xml.data.ApplicationDAO;

public interface IConfigure {
	public void configure(ApplicationInfo appInfo) throws ApplicationConfigurationException;
	public boolean unconfigure() throws ApplicationConfigurationException;
}
