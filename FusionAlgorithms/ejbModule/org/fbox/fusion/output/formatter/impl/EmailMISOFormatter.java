package org.fbox.fusion.output.formatter.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.URLDataSource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.fbox.common.data.IContext;
import org.fbox.common.data.IDataElement;
import org.fbox.common.data.InputParameter;
import org.fbox.common.exception.FormatterException;
import org.fbox.common.exception.FormatterInitializationException;
import org.fbox.common.output.IFormatter;
import org.fbox.fusion.output.adapter.data.CapConstants;
import org.fbox.fusion.output.adapter.data.CapMessage;
import org.fbox.fusion.output.formatter.AbstractFormatter;

@Stateless (name="emailMISO")
@Remote ({IFormatter.class})
public class EmailMISOFormatter extends AbstractFormatter<Multipart> {
	
	//1st input (Form or not)
	//2nd input (Probability)
	@Override
	public Multipart format(IContext state, IDataElement... dataArray) throws FormatterException {
	
		int inputStreams = 0, i=0;
		boolean rightInputs = true;
		boolean form = true;
		Double probability = 0.0;
		Integer area = Integer.parseInt((String)state.getContextParameter("area-location"));
		
		if(!(Boolean) state.getContextParameter("first-time"))
			return null;
			
		for(IDataElement argData : dataArray) //Count input streams 
			inputStreams++;
		
		if(inputStreams!=2) { //Wrong number of input streams
			System.out.println("[EmailForm] WARNING: The input streams must be 2 but we currently have "+inputStreams);
			return null;
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
			System.out.println("[EmailForm] WARNING: Input streams must not be \"null\" !");
			return null;
		}
		
		//If inputs' number is the expected one
		
		//Check if the input values are what we expected to be
		if (misoData[0].getValue() instanceof String) {
			form = Boolean.parseBoolean((String)misoData[0].getValue());
		} else {
			System.out.println("[EmailForm] WARNING: Invalid data value detected("+misoData[0].getValue()+"). Value must be String");
			return null;
		}
		
		if(misoData[1].getValue() instanceof Number) {
			probability = (Double)misoData[1].getValue();
		} else {
			System.out.println("[EmailForm] WARNING: Invalid data value detected("+misoData[1].getValue()+"). Value must be Double");
			return null;
		}
		
		if(form == false) { //Check if we do have to create a message
			System.out.println("[EmailForm] WARNING: No need to send an email");
			return null;
		}
		else
			state.setContextParameter("first-time", new Boolean(false)); //Form an email only once!
		
		//We have to "run" the CAP formatter and create the appropriate CAP message
		String capXML = capFormatter(state,misoData[1]);
				
		Multipart multipart = new MimeMultipart();
		MimeBodyPart attachment1 = new MimeBodyPart();
		MimeBodyPart attachment2 = new MimeBodyPart();
		MimeBodyPart html = new MimeBodyPart();
		
		// TODO Add functionality to load file automatically according to the location specified in CAP parameters (OSM API)
		//Load image from a url
		File file = null;
		String path = "/images/";
		String filename = "loc" + area + ".jpg";
		URLDataSource source = null;
		
		URL image = this.getClass().getResource(path + filename);
			
		if (image == null) {
			System.out.println("[EmailForm] ERROR: Problem in retrieving image from the specified URL!");
			return null;
		}
			
		source = new URLDataSource(image);
		file = new File(image.toString());
		
		String html1 = "SFE detected a possible incident of interest.<br><u>Estimated Probability</u>: <b>"+probability+"</b><br><br>";
		String html2 = "<table border=\"1\">" +
								"<tr bgcolor=\"#C9C3C3\">" +
									"<td colspan=\"2\"><b><center>Details</center></b></td>" +
								"</tr>" +
								"<tr>" +
									"<td bgcolor=\"#E6DBDB\"><b>Event</b></td>" +
									"<td>"+(String)state.getContextParameter("event")+"</td>" +
								"</tr>" +
								"<tr>" +
									"<td bgcolor=\"#E6DBDB\"><b>Urgency</b></td>" +
									"<td>"+(String)state.getContextParameter("urgency")+"</td>" +
								"</tr>" +
								"<tr>" +
									"<td bgcolor=\"#E6DBDB\"><b>Severity</b></td>" +
									"<td>"+(String)state.getContextParameter("severity")+"</td>" +
								"</tr>" +
								"<tr>" +
									"<td bgcolor=\"#E6DBDB\"><b>Certainty</b></td>" +
									"<td>"+mapCertainty(probability)+"</td>" +
								"</tr>" +
								"<tr>" +
									"<td bgcolor=\"#E6DBDB\"><b>Area</b></td>" +
									"<td>"+(String)state.getContextParameter("areaDesc")+"</td>" +
								"</tr>" +
							"</table><br>";
		
		String html3 = "Attachements:<br>" +
						"<ul>" +
							"<li>Map</li>" +
							"<li>CAP message</li>" +
						"</ul>";
		
		try {
			//Description in html
			html.setContent(html1 + html2 + html3, "text/html");
			multipart.addBodyPart(html);
			
			//Attachment of image file
			attachment1.setDataHandler(new DataHandler(source));
			attachment1.setFileName(file.getName());
			multipart.addBodyPart(attachment1);

	        //Attachment of CAP xml
	        attachment2.setText(capXML);
	        attachment2.setFileName("CAPmessage.xml");
	        multipart.addBodyPart(attachment2);
		} catch (MessagingException e) {
			System.out.println("[EmailForm] ERROR: Messaging Exception --> "+e.getMessage());
			return null;
		}
		
		return multipart;
	}
	
	private String capFormatter (IContext state, IDataElement data) {
		
		CapMessage capMsg = new CapMessage();
		Boolean alert=(Boolean)state.getContextParameter("alert");
		String identifier;
		String certainty;
		String formattedData = null;
		
		SimpleDateFormat sdfId = new SimpleDateFormat(CapConstants.CAP_DATE_FORMAT_IDENTIFIER);
		
		capMsg.setSender((String)state.getContextParameter("sender"));			
		capMsg.setScope(CapConstants.DEFAULT_SCOPE);	
		
		// i.e timestamp
		SimpleDateFormat sdfSent = new SimpleDateFormat(CapConstants.CAP_DATE_FORMAT_SENT);
		capMsg.setSent(sdfSent.format(data.getTimestamp()));
		capMsg.setStatus(CapConstants.DEFAULT_STATUS);
		capMsg.setSource(CapConstants.DEFAULT_SOURCE);
		capMsg.setAddresses((String)state.getContextParameter("addresses"));
		capMsg.setCategory((String)state.getContextParameter("category"));
		capMsg.setEvent((String)state.getContextParameter("event"));
		capMsg.setHeadline((String)state.getContextParameter("headline"));
		capMsg.setDescription((String)state.getContextParameter("description"));
		capMsg.setLanguage(CapConstants.DEFAULT_LANGUAGE);
		capMsg.setSenderName(CapConstants.DEFAULT_SENDERNAME);
		
		capMsg.setSeverity((String)state.getContextParameter("severity"));
		capMsg.setUrgency((String)state.getContextParameter("urgency"));
		
		capMsg.setAreaDesc((String)state.getContextParameter("areaDesc"));
		
		//Check if we have to create a circle or a polygon
		StringTokenizer loc = new StringTokenizer((String)state.getContextParameter("location"), " ");
		
		if(loc.countTokens() > 2) //We have a polygon as location 
			capMsg.setPolygon((String)state.getContextParameter("location"));
		else //We have a circle as location 
			capMsg.setCircle((String)state.getContextParameter("location"));
			
		try {
			certainty = this.mapCertainty((Double)data.getValue());
		} catch (FormatterException e) {
			//System.out.println("WARNING---->CAP FORMATTER: "+e.getMessage());
			return null;
		}
		
		//This is what varies in every message
		capMsg.setCertainty(certainty);
		
		if(!alert) {
			capMsg.setMsgType("Alert");
		
			identifier = state.getContextParameter("sender") + "" +
						 state.getContextParameter("applicationID") + "-" + 
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
				String refSender = (String)state.getContextParameter("sender");
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
		
		formattedData= capMsg.toXML();
		
		return formattedData;
	}
	
	private String mapCertainty (Double data) throws FormatterException {
		
		//System.out.println("-------------------->Value to map="+data);
		String result;
		
		if((data > 0.0) && (data<= 0.1))
			result="Unlikely";
		else if((data > 0.1) && (data <= 0.5))
			result="Possible";
		else if((data > 0.5) && (data <= 1.0))
			result="Likely";
		else  
			throw new FormatterException("No need to format a CAP message");

		return result;
	}
	
	@Override
	public void initialize(IContext state, HashMap<String, InputParameter> iparams) throws FormatterInitializationException {
		
		super.initialize(state, iparams);
		
		state.setContextParameter("alert", false);		//Boolean
		state.setContextParameter("oldValue", null); 	//String (certainty)
		state.setContextParameter("identifier", null); 	//String
		state.setContextParameter("sentTime", null); 	//String
		state.setContextParameter("first-time", new Boolean(true));
	}
	
	//Location of the area (1 or 2)
	public String[] getRequiredParameters() {
		String[] params={"area-location","sender", "addresses", "event", "headline", "description", "areaDesc", "location", "urgency", "severity", "applicationID", "category"};
		return params;
	}
	
	@Override
	public String getType() {
		return "emailMISO";
	}
	
	@Override
	public boolean allowsMultipleInputs() {	
		return true;
	}	
}
