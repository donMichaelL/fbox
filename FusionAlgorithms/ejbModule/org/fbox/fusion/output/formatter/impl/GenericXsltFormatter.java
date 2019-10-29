package org.fbox.fusion.output.formatter.impl;

import java.io.StringReader;
import java.io.StringWriter;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.fbox.common.application.util.IServiceLocator;
import org.fbox.common.data.IContext;
import org.fbox.common.data.IDataElement;
import org.fbox.common.exception.ServiceLocatorException;
import org.fbox.common.output.IFormatter;
import org.fbox.fusion.output.formatter.AbstractFormatter;
import org.fbox.util.DataTransformer;

/**
 * Session Bean implementation class GenericXsltFormatter
 */
@Stateless (name="generic")
@Remote( { IFormatter.class })
public class GenericXsltFormatter extends AbstractFormatter<String> {
	
	@EJB(lookup="app/FBoxCore/FusionCore/ServiceLocator", beanInterface=IServiceLocator.class)
	IServiceLocator serviceLocator;
		

	private static final String XSLT_CONFIGURATION_ID="xslt-conf";
	private static final String DEFAULT_FORMATTER_ID = "simpleXml";

    @Override
    public String[] getRequiredParameters() {
    	String[] params={};
    	return params;
    }

	@Override
    public String format(IContext state, IDataElement... data) {

	  	String formattedMessage=null;
		
		
		try {
			Object xsltConfId=state.getContextParameter("XSLT_CONFIGURATION_ID");
			Transformer transformer=TransformerFactory.newInstance().newTransformer(new StreamSource(new StringReader(getXsltSource((xsltConfId!=null?(String)xsltConfId:null)))));
			StreamResult result=new StreamResult();
			result.setWriter(new StringWriter());
			transformer.transform(new StreamSource(new StringReader(DataTransformer.transformDataElementToXml(data[0]))),result);
			formattedMessage=((StringWriter)result.getWriter()).getBuffer().toString();
		} catch (TransformerFactoryConfigurationError | TransformerException e) {
			e.printStackTrace();
		} 

		return formattedMessage;		
    }

	@Override
    public String getType() {
    	return "xslt";
    }

	

	private String getXsltSource(String xsltConfId)  {  
		String xsltDoc=null;
		if(xsltConfId == null) {
			xsltConfId = DEFAULT_FORMATTER_ID;
		}
		
		try {
			xsltDoc=serviceLocator.getXsltConfiguration(xsltConfId);
		} catch (ServiceLocatorException e) {
			e.printStackTrace();
		}	
		
		return xsltDoc;
	}	
	
	public boolean allowsMultipleInputs() {	
		return false;
	}	
}
