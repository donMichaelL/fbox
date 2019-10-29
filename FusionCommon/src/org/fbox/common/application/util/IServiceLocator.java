package org.fbox.common.application.util;

import org.fbox.common.IStructure;
import org.fbox.common.algorithms.IAlgorithm;
import org.fbox.common.application.configuration.IConfigure;
import org.fbox.common.exception.ServiceLocatorException;
import org.fbox.common.output.IAdapter;
import org.fbox.common.output.IFormatter;


public interface IServiceLocator {
	
	 public IAlgorithm getAlgorithm(String algorithmName) throws ServiceLocatorException;
	 public IStructure getAlgorithmStructure(String algorithmName) throws ServiceLocatorException;
	 public IConfigure getConfigurator(String applicationName) throws ServiceLocatorException;
	 public String getXsltConfiguration(String xsltConfId) throws ServiceLocatorException;
	 public IFormatter<?> getFormatter(String formatterName)throws ServiceLocatorException;
	 public IAdapter getAdapter(String adpaterName) throws ServiceLocatorException;

}
