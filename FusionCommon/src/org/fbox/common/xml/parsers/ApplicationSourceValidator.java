package org.fbox.common.xml.parsers;

import java.util.HashMap;

import org.fbox.common.exception.ApplicationValidationException;
import org.fbox.common.xml.data.ApplicationDAO;
import org.fbox.common.xml.data.ContextorDAO;
import org.fbox.common.xml.data.StreamerSelectorDAO;

public class ApplicationSourceValidator {

	public boolean validate(ApplicationDAO source) throws ApplicationValidationException {
		
		HashMap<String, StreamerSelectorDAO> streamSelectorMap=source.getStreamers();
		
		if (streamSelectorMap.isEmpty())
			throw new ApplicationValidationException("No StreamSelectors have been defined");
		
		HashMap<String, ContextorDAO> contextorMap=source.getContextors();		
		for (ContextorDAO contextor : contextorMap.values()) {
			
		}
		
		
		
		return false;
	}
}
