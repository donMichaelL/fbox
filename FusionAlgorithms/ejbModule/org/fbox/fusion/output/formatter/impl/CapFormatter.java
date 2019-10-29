package org.fbox.fusion.output.formatter.impl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.fbox.common.data.IContext;
import org.fbox.common.data.IDataElement;
import org.fbox.common.data.InputParameter;
import org.fbox.common.exception.FormatterException;
import org.fbox.common.exception.FormatterInitializationException;
import org.fbox.common.output.IFormatter;

import org.fbox.fusion.output.adapter.data.CapMessage;
import org.fbox.fusion.output.adapter.data.CapConstants;
import org.fbox.fusion.output.formatter.AbstractFormatter;

@Stateless (name="cap")
@Remote ({IFormatter.class})
public class CapFormatter extends AbstractFormatter<String> {
	
	// CapMessage parameters
	public static final String CAP_PARAM_SENDER = "sender";
	public static final String CAP_PARAM_ADDRESSES = "addresses";
	public static final String CAP_PARAM_EVENT = "event";
	public static final String CAP_PARAM_HEADLINE = "headline";
	public static final String CAP_PARAM_DESCRIPTION = "description";
	public static final String CAP_PARAM_AREADESC = "areaDesc";
	public static final String CAP_PARAM_LOCATION = "location";
	public static final String CAP_PARAM_URGENCY = "urgency";
	public static final String CAP_PARAM_SEVERITY = "severity";
	public static final String CAP_PARAM_APPLICATION_ID = "applicationID";
	public static final String CAP_PARAM_CATEGORY = "category";
	
	@Override
	public String format(IContext state, IDataElement... dataArray) throws FormatterException {

		String formattedData=null;
		
		IDataElement data=dataArray[0];
		
		synchronized (state) {
			 
			//Check the value that comes inside the formatter
			if(data!=null && data.getValue() != null)  {
	
				CapMessage capMsg = new CapMessage();
				Boolean alert=(Boolean)state.getContextParameter("alert");
				String identifier;
				String certainty;
				
				if (data.getValue() instanceof String) {
					
					certainty = (String)data.getValue();
					
					//System.out.println("[CAP] INPUT: "+certainty);
					
					if(certainty.equalsIgnoreCase("no")) {
						//System.out.println("WARNING---->CAP FORMATTER: No need to format a CAP message");
						return null;
					}
				} else
					throw new FormatterException("Invalid data value detected("+data.getValue()+"). Value must be String");
				
				// TODO: Set the alert number and the progressive update dynamically.
				// i.e identifier <sender-applicationID-date-id-update>
				SimpleDateFormat sdfId = new SimpleDateFormat(CapConstants.CAP_DATE_FORMAT_IDENTIFIER);
				
				capMsg.setSender((String)state.getContextParameter(CAP_PARAM_SENDER));			
				capMsg.setScope(CapConstants.DEFAULT_SCOPE);	
				
				// i.e timestamp
				SimpleDateFormat sdfSent = new SimpleDateFormat(CapConstants.CAP_DATE_FORMAT_SENT);
				capMsg.setSent(sdfSent.format(data.getTimestamp()));
				capMsg.setStatus(CapConstants.DEFAULT_STATUS);
				capMsg.setSource(CapConstants.DEFAULT_SOURCE);
				capMsg.setAddresses((String)state.getContextParameter(CAP_PARAM_ADDRESSES));
				capMsg.setCategory((String)state.getContextParameter(CAP_PARAM_CATEGORY));
				capMsg.setEvent((String)state.getContextParameter(CAP_PARAM_EVENT));
				capMsg.setHeadline((String)state.getContextParameter(CAP_PARAM_HEADLINE));
				capMsg.setDescription((String)state.getContextParameter(CAP_PARAM_DESCRIPTION));
				capMsg.setLanguage(CapConstants.DEFAULT_LANGUAGE);
				capMsg.setSenderName(CapConstants.DEFAULT_SENDERNAME);
				
				capMsg.setSeverity((String)state.getContextParameter(CAP_PARAM_SEVERITY));
				capMsg.setUrgency((String)state.getContextParameter(CAP_PARAM_URGENCY));
				
				capMsg.setAreaDesc((String)state.getContextParameter(CAP_PARAM_AREADESC));
				
				//Check if we have to create a circle or a polygon
				StringTokenizer loc = new StringTokenizer((String)state.getContextParameter(CAP_PARAM_LOCATION), " ");
				
				if(loc.countTokens() > 2) //We have a polygon as location 
					capMsg.setPolygon((String)state.getContextParameter(CAP_PARAM_LOCATION));
				else //We have a circle as location 
					capMsg.setCircle((String)state.getContextParameter(CAP_PARAM_LOCATION));
					
				//This is what varies in every message
				capMsg.setCertainty(certainty);
				
				if(!alert) {
					capMsg.setMsgType("Alert");
				
					identifier = state.getContextParameter(CAP_PARAM_SENDER) + "" +
								 state.getContextParameter(CAP_PARAM_APPLICATION_ID) + "-" + 
								 sdfId.format(data.getTimestamp()) + "-" + 
								 "1" + "-" + 
								 "0";
					
					//We have to update registry
					state.setContextParameter("alert", true);
					state.setContextParameter("oldvalue", (String)certainty); //We have to check if we have a null value
					state.setContextParameter("identifier", (String)identifier); 	
					state.setContextParameter("sentTime", (String)sdfSent.format(data.getTimestamp())); 	
					//state.setContextParameter("sender", (String)state.getContextParameter(CAP_PARAM_SENDER)); 		
					
					capMsg.setIdentifier(identifier);
				}
				else {
					capMsg.setMsgType("Update");
					
					//We have to check if we have to perform an update
					if( !((String)state.getContextParameter("oldvalue")).equals(certainty)) { //Perform update
		
						//Get the old values from registry to make the reference
						String refSender = (String)state.getContextParameter(CAP_PARAM_SENDER);
						String refIdentifier = (String) state.getContextParameter("identifier");
						String refSent = (String) state.getContextParameter("sentTime");
						String ref = refSender + "," + refIdentifier + "," + refSent;
						
						//Set the values of the new (update) message in the registry
						state.setContextParameter("oldvalue", (String)certainty);
		
						String record = (String) state.getContextParameter("identifier");
						StringTokenizer st = new StringTokenizer(record, "-");
						identifier = st.nextToken() + "-" + st.nextToken() + "-" + st.nextToken() + "-" + String.valueOf( Integer.parseInt(st.nextToken()) + 1 );
						
						state.setContextParameter("identifier", (String)identifier);
						state.setContextParameter("sentTime", (String)sdfSent.format(data.getTimestamp())); 	
						//state.setContextParameter("sender", (String)state.getContextParameter(CAP_PARAM_SENDER)); 		
					
						capMsg.setIdentifier(identifier);
						capMsg.setReferences(ref);
					}
					else
						return null;
						//throw new FormatterException("No need to format a CAP message"); //We don't have to make any update operation (nothing changed)
				}
				
				//System.out.println(capMsg);
				formattedData= capMsg.toXML();
				System.out.println("[CAP_SISO] OUTPUT: "+formattedData);
			} 
		}
			
		return formattedData;
	}
	
	@Override
	public void initialize(IContext state, HashMap<String, InputParameter> iparams) throws FormatterInitializationException {
		super.initialize(state, iparams);
		
		state.setContextParameter("alert", false);		//Boolean
		state.setContextParameter("oldValue", null); 	//String (certainty)
		state.setContextParameter("identifier", null); 	//String
		state.setContextParameter("sentTime", null); 	//String
	}

	@Override
	public String[] getRequiredParameters() {
		String[] params={CAP_PARAM_SENDER, CAP_PARAM_ADDRESSES, CAP_PARAM_EVENT, CAP_PARAM_HEADLINE, CAP_PARAM_DESCRIPTION, CAP_PARAM_AREADESC, CAP_PARAM_LOCATION, CAP_PARAM_URGENCY, CAP_PARAM_SEVERITY, CAP_PARAM_APPLICATION_ID, CAP_PARAM_CATEGORY};
		return params;
	}

	@Override
	public String getType() {
		return "cap";
	}
	
	@Override
	public boolean allowsMultipleInputs() {	
		return false;
	}	
}
