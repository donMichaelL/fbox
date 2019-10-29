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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

@Stateless (name="capMISO")
@Remote ({IFormatter.class})
public class CapMISOFormatter extends AbstractFormatter<String> {
	
	// CapMessage parameters
	public static final String CAP_PARAM_SENDER = "sender";
	public static final String CAP_PARAM_ADDRESSES = "addresses";
	public static final String CAP_PARAM_EVENT = "event";
	public static final String CAP_PARAM_HEADLINE = "headline";
	public static final String CAP_PARAM_DESCRIPTION = "description";
	public static final String CAP_PARAM_AREADESC = "areaDesc";
	public static final String CAP_PARAM_APPLICATION_ID = "applicationID";
	public static final String CAP_PARAM_CATEGORY = "category";
	
	@Override
	public String format(IContext state, IDataElement... dataArray) throws FormatterException {

		String formattedData=null;
		boolean rightInputs = true;
		int i=0;
		int inputStreams = 0;

		for(IDataElement argData : dataArray) //Count input streams 
			inputStreams++;
		
		if(inputStreams!=4) { //Wrong number of input streams
			System.out.println("[CAP_MISO] WARNING: The input streams must be 4 but we currently have "+inputStreams);
			return null;
			//throw new FormatterException("WARNING---->CAP FORMATTER: The input streams must be "+inputStreams+" but we have "+(i+1));
		}
		
		IDataElement [] misoData = new IDataElement[inputStreams]; 

		//Check values that come inside the formatter
		for(IDataElement argData : dataArray) {
			if(argData != null && argData.getValue() != null) {
				misoData[i] = argData;  //Load data
				i++;
			}
			else {
				rightInputs = false;  //If one input is empty
				break;
			}
		}
		
		if(!rightInputs) {
			System.out.println("[MISO_CAP] WARNING: Input streams must not be \"null\" !");
			return null;
		}
		
		//If all inputs are OK (not null)
		synchronized (state) {

			CapMessage capMsg = new CapMessage();
			Boolean alert=(Boolean)state.getContextParameter("alert");
			String identifier, position="", urgency, severity;
			Geometry location;
			Boolean point = true;

			//Check if the input values are what we expected to be
			if (misoData[0].getValue() instanceof String) {
				
				urgency = (String)misoData[0].getValue();
				
				if(urgency.equalsIgnoreCase("no")) {
					//System.out.println("[CAP_MISO] WARNING: No need to format a CAP message");
					return null;
				}
			} else {
				//throw new FormatterException("Invalid data value detected("+data.getValue()+"). Value must be String");
				System.out.println("[CAP_MISO] WARNING: Invalid data value detected("+misoData[0].getValue()+"). Value must be String");
				return null;
			}
			
			if (misoData[1].getValue() instanceof String) {
				
				severity = (String)misoData[1].getValue();
				
				if(severity.equalsIgnoreCase("no")) {
					System.out.println("[CAP_MISO] WARNING: No need to format a CAP message");
					return null;
				}
			} else {
				//throw new FormatterException("Invalid data value detected("+data.getValue()+"). Value must be String");
				System.out.println("[CAP_MISO] WARNING: Invalid data value detected("+misoData[1].getValue()+"). Value must be String");
				return null;
			}
			
			if(misoData[2].getValue() instanceof Number) {
				if((Double)misoData[2].getValue() != 1.0) {
					System.out.println("[CAP_MISO] WARNING: No need to format a CAP message for this building");
					return null;
				}
			} else {
				//throw new FormatterException("Invalid data value detected("+misoData[2].getValue()+"). Value must be Double");
				System.out.println("[CAP_MISO] WARNING: Invalid data value detected("+misoData[2].getValue()+"). Value must be Double");
				return null;
			}
			
			if( misoData[3].getValue() instanceof Geometry) {

				location = (Geometry)misoData[3].getValue();

				//Now that he have the location, check if it's a point or a polygon
				//Nothing else is supported from CAP library
				String type = location.getGeometryType();

				if(type.equalsIgnoreCase("POINT") || type.equalsIgnoreCase("POLYGON")) { //Keep the location in an array
					Coordinate[] coord = location.getCoordinates();

					if(coord.length != 1)
						point = false;

					//CAP stores a location by the following format: lat1,long1 lat2,long2 ... latN,longN lat1,long1
					for (int j=0 ; j<coord.length ; j++) {
						if (j==0) {
							position = coord[j].y + "," + coord[j].x;
						} else
							position = position + " " + coord[j].y + "," + coord[j].x ;
					}				
				} else {
					//throw new FormatterException("The data type of the geometry should be POINT or POLYGON but current input is ("+type+")");
					System.out.println("[CAP_MISO] WARNING: The data type of the geometry should be POINT or POLYGON but current input is ("+type+")");
					return null;
				}
					
			} else {
				//throw new FormatterException("Invalid data value detected("+misoData[3].getValue()+"). Value must be Double");
				System.out.println("[CAP_MISO] WARNING: Invalid data value detected("+misoData[3].getValue()+"). Value must be Double");
				return null;
			}
		
			//Print input
			/*
			String input = "";
			for (int j=0 ; j<misoData.length ; j++)
				input = input + misoData[j].getValue().toString() + " ";
			System.out.println("[CAP_MISO] INPUT: "+input);
			*/
			
			// i.e identifier <sender-applicationID-date-id-update>
			SimpleDateFormat sdfId = new SimpleDateFormat(CapConstants.CAP_DATE_FORMAT_IDENTIFIER);

			capMsg.setSender((String)state.getContextParameter(CAP_PARAM_SENDER));			
			capMsg.setScope(CapConstants.DEFAULT_SCOPE);	

			// i.e timestamp
			SimpleDateFormat sdfSent = new SimpleDateFormat(CapConstants.CAP_DATE_FORMAT_SENT);
			capMsg.setSent(sdfSent.format(misoData[0].getTimestamp()));
			capMsg.setStatus(CapConstants.DEFAULT_STATUS);
			capMsg.setSource(CapConstants.DEFAULT_SOURCE);
			capMsg.setAddresses((String)state.getContextParameter(CAP_PARAM_ADDRESSES));
			capMsg.setCategory((String)state.getContextParameter(CAP_PARAM_CATEGORY));
			capMsg.setEvent((String)state.getContextParameter(CAP_PARAM_EVENT));
			capMsg.setHeadline((String)state.getContextParameter(CAP_PARAM_HEADLINE));
			capMsg.setDescription((String)state.getContextParameter(CAP_PARAM_DESCRIPTION));
			capMsg.setLanguage(CapConstants.DEFAULT_LANGUAGE);
			capMsg.setSenderName(CapConstants.DEFAULT_SENDERNAME);
			capMsg.setCertainty(CapConstants.DEFAULT_CERTAINTY);

			//This is what varies in every message (severity and urgency)
			capMsg.setSeverity(severity);
			capMsg.setUrgency(urgency);

			capMsg.setAreaDesc((String)state.getContextParameter(CAP_PARAM_AREADESC));

			if(point) {
				position = position + " 1.00"; //To give a small diameter to the point
				capMsg.setCircle(position);    //Visualize it as a circle
			} else
				capMsg.setPolygon(position);

			if(!alert) {
				capMsg.setMsgType("Alert");

				identifier = state.getContextParameter(CAP_PARAM_SENDER) + "" +
						state.getContextParameter(CAP_PARAM_APPLICATION_ID) + "-" + 
						sdfId.format(misoData[0].getTimestamp()) + "-" + 
						"1" + "-" + 
						"0";

				//We have to update registry
				state.setContextParameter("alert", true);
				state.setContextParameter("oldSeverity", (String)severity);
				state.setContextParameter("oldUrgency", (String)urgency);
				state.setContextParameter("identifier", (String)identifier); 	
				state.setContextParameter("sentTime", (String)sdfSent.format(misoData[0].getTimestamp())); 	

				capMsg.setIdentifier(identifier);
			}
			else {
				capMsg.setMsgType("Update");

				//We have to check if we have to perform an update
				if( !((String)state.getContextParameter("oldUrgency")).equals(urgency) || !((String)state.getContextParameter("oldSeverity")).equals(severity)) { //Perform update

					//Get the old values from registry to make the reference
					String refSender = (String)state.getContextParameter(CAP_PARAM_SENDER);
					String refIdentifier = (String) state.getContextParameter("identifier");
					String refSent = (String) state.getContextParameter("sentTime");
					String ref = refSender + "," + refIdentifier + "," + refSent;

					//Set the values of the new (update) message in the registry
					state.setContextParameter("oldSeverity", (String)severity);
					state.setContextParameter("oldUrgency", (String)urgency);

					String record = (String) state.getContextParameter("identifier");
					StringTokenizer st = new StringTokenizer(record, "-");
					identifier = st.nextToken() + "-" + st.nextToken() + "-" + st.nextToken() + "-" + String.valueOf( Integer.parseInt(st.nextToken()) + 1 );

					state.setContextParameter("identifier", (String)identifier);
					state.setContextParameter("sentTime", (String)sdfSent.format(misoData[0].getTimestamp())); 	

					capMsg.setIdentifier(identifier);
					capMsg.setReferences(ref);
				}
				else
					return null;
				//throw new FormatterException("No need to format a CAP message"); //We don't have to make any update operation (nothing changed)
			}

			// System.out.println(capMsg);
			formattedData = capMsg.toXML();
			//System.out.println("[CAP_MISO] OUTPUT: "+formattedData);
		}
		
		return formattedData;
	}
	
	@Override
	public void initialize(IContext state, HashMap<String, InputParameter> iparams) throws FormatterInitializationException {
		super.initialize(state, iparams);
		
		state.setContextParameter("alert", false);		//Boolean
		state.setContextParameter("oldSeverity", null); //String (severity)
		state.setContextParameter("oldUrgency", null); 	//String (urgency)
		state.setContextParameter("identifier", null); 	//String
		state.setContextParameter("sentTime", null); 	//String
	}

	@Override
	public String[] getRequiredParameters() {
		String[] params={CAP_PARAM_SENDER, CAP_PARAM_ADDRESSES, CAP_PARAM_EVENT, CAP_PARAM_HEADLINE, CAP_PARAM_DESCRIPTION, CAP_PARAM_AREADESC, CAP_PARAM_APPLICATION_ID, CAP_PARAM_CATEGORY};
		return params;
	}

	@Override
	public String getType() {
		return "capMISO";
	}
	
	@Override
	public boolean allowsMultipleInputs() {	
		return true;
	}	
}
