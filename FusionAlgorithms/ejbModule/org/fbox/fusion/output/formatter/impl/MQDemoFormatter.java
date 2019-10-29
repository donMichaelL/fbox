package org.fbox.fusion.output.formatter.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.fbox.common.data.IContext;
import org.fbox.common.data.IDataElement;
import org.fbox.common.data.InputParameter;
import org.fbox.common.exception.FormatterException;
import org.fbox.common.exception.FormatterInitializationException;
import org.fbox.common.output.IFormatter;
import org.fbox.fusion.output.formatter.AbstractFormatter;

@Stateless (name="mqDemoForm")
@Remote ({IFormatter.class})
public class MQDemoFormatter extends AbstractFormatter<String> {
	
	public static final String PUT_DATE = "date";
	public static final String APPLICATION_ID = "application-id";
	
	@Override
	public String format(IContext state, IDataElement... data) throws FormatterException {
		
		String formattedData = "";
		String insert_date = (String)state.getContextParameter(PUT_DATE);
		String appl_id = (String)state.getContextParameter(APPLICATION_ID);
		
		//Check the value that comes inside the formatter
		if(data != null) {

			if (data[0].getValue() instanceof Number) {
				
				if(insert_date.equalsIgnoreCase("true")) {
					String dateFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
					SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
					Date currentTime = new Date();
					String timestamp = sdf.format(currentTime);
					formattedData = formattedData + timestamp + ", ";
				}

				formattedData = formattedData + appl_id;
				formattedData = "["+formattedData+"]    SFE output ------> " + data[0].getValue();

			} else
				throw new FormatterException("Invalid data value detected("+data[0].getValue()+"). Value must be Double");
		}
		
		return formattedData;
	}
	
	@Override
	public void initialize(IContext state, HashMap<String, InputParameter> iparams) throws FormatterInitializationException {
		super.initialize(state, iparams);
	}
	
	@Override
	public String[] getRequiredParameters() {
		String[] params={PUT_DATE, APPLICATION_ID};
		return params;
	}
	
	@Override
	public String getType() {
		return "mqDemoForm";
	}

	@Override
	public boolean allowsMultipleInputs() {
		return false;
	}
}
