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

@Stateless (name="simForm")
@Remote ({IFormatter.class})
public class SimFormatter extends AbstractFormatter<String> {
	
	public static final String POLYGON_LOCATION = "location";
	public static final String APPLICATION_ID = "application-id";
	
	private static final String xmlContainer = "<?xml version='1.0'?>\n" + 
										 "<measurements>\n" + 
										 	"\t<sender>{sender}</sender>\n" +
										 	"\t<timestamp>{time}</timestamp>\n" +
										 	"\t<area>\n" +
										 		"\t\t<polygon>{location}</polygon>\n" + 
										 		"\t\t<probability>{fireProb}</probability>\n" +
										 	"\t</area>\n" +
										 "</measurements>";
  
	@Override
	public String format(IContext state, IDataElement... data) throws FormatterException {
		
		String formattedMessage=null;
		String dateFormatID = "yyyy-MM-dd";
		String dateFormatTime = "yyyy-MM-dd'T'HH:mm:ssZ";
		SimpleDateFormat sdf1 = new SimpleDateFormat(dateFormatID);
		SimpleDateFormat sdf2 = new SimpleDateFormat(dateFormatTime);
		
		String location = (String)state.getContextParameter(POLYGON_LOCATION);
		String appl_id = (String)state.getContextParameter(APPLICATION_ID);
		
		//Check the value that comes inside the formatter
		if(data != null) {

			if (data[0].getValue() instanceof Number) {
				
				Date currentTime = new Date();
				String dateID = sdf1.format(currentTime);
				String dateTime = sdf2.format(currentTime);
				
				formattedMessage = xmlContainer;
				
				formattedMessage = formattedMessage.replace("{sender}", "SFE" + appl_id + "_" + dateID);
				formattedMessage = formattedMessage.replace("{time}", dateTime);
				formattedMessage = formattedMessage.replace("{location}", location);
				
				String values = "";
				for(IDataElement argData : data) //Number of input parameters
					values = values + "  " + argData.getValue().toString();
				
				values.trim();
				
				System.out.println("[Formatted Message] Detected: "+values.toString());
				
				formattedMessage = formattedMessage.replace("{fireProb}", values);
				//formattedMessage = formattedMessage.replace("{fireProb}", data[0].getValue().toString());
			} else {
				System.out.println("Invalid data value detected("+data[0].getValue()+"). Value must be Double");
				return null;
				//throw new FormatterException("Invalid data value detected("+data[0].getValue()+"). Value must be Double");
			}
		}
		
		return formattedMessage;
	}
	
	@Override
	public void initialize(IContext state, HashMap<String, InputParameter> iparams) throws FormatterInitializationException {
		super.initialize(state, iparams);
	}
	
	@Override
	public String[] getRequiredParameters() {
		String[] params={POLYGON_LOCATION,APPLICATION_ID};
		return params;
	}
	
	@Override
	public String getType() {
		return "simForm";
	}

	@Override
	public boolean allowsMultipleInputs() {
		return true;
	}
}
