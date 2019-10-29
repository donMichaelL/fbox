package org.fbox.fusion.output.formatter.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.sos.message.SOSInsertMeasurement;
import org.sos.message.SOSRegisterMeasurement;

@Stateless (name="sos")
@Remote ({IFormatter.class})
public class SOSFormatter extends AbstractFormatter<String> {
	
	private static SimpleDateFormat dateFormatter=new SimpleDateFormat("yyyy.MM.dd HH:mm:ss:SSS");
	
	// SOS message - User defined parameters
	private static final String PATH = "/resources/";
	private static final String REGISTER_TEMPLATE = "Template_RegisterSensor_Measurement.xml";
	private static final String INSERT_TEMPLATE = "Template_InsertObservation_Measurement.xml"; 
	private static final String SOS_PARAM_OWNER = "owner";
	private static final String SOS_PARAM_UNIQUE_ID = "unique-id";
	private static final String SOS_PARAM_PHENOMENON_URN = "phenomenon-urn";
	private static final String SOS_PARAM_LATITUDE = "latitude";
	private static final String SOS_PARAM_LONGITUDE = "longitude";
	
	// SOS message default values
	private static final String SOS_DEFAULT_OWNER = "SFE";
	public static final boolean SOS_DEFAULT_STATUS = true;
	public static final String SOS_DEFAULT_ALTITUDE = "2.0";
	public static final String SOS_DEFAULT_OFFERING_ID = "SFE-Output";
	public static final String SOS_DEFAULT_UOM = "%";
	
	@Override
	public String format(IContext state, IDataElement... data) throws FormatterException {
	
		String formattedMessage=null;
		try {
			Boolean isRegistered=(Boolean)state.getContextParameter("isRegistered");
			
			if (!isRegistered) {
				formattedMessage = formatSosRegisterMeasurementMessage(state, data[0]);
				state.setContextParameter("isRegistered", true);
			} else {
				formattedMessage= formatSosInsertMeasurementMessage(state, data[0]);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new FormatterException(e.getMessage());
		}
			
		return formattedMessage;				
	}
	

	@Override
	public String[] getRequiredParameters() {
		String[] params={SOS_PARAM_OWNER, SOS_PARAM_UNIQUE_ID, SOS_PARAM_PHENOMENON_URN, SOS_PARAM_LATITUDE, SOS_PARAM_LONGITUDE};
		return params;
	}


	@Override
	public String getType() {
		return "sos";
	}
	
	public String formatSosRegisterMeasurementMessage(IContext state, IDataElement data) {

		SOSRegisterMeasurement registerMeasurement = new SOSRegisterMeasurement();
		registerMeasurement.setOwner((String)state.getContextParameter(SOS_PARAM_OWNER));
		registerMeasurement.setUniqueID((String)state.getContextParameter(SOS_PARAM_UNIQUE_ID));
		registerMeasurement.setStatus(SOS_DEFAULT_STATUS);
		
		Double latitude = Double.parseDouble((String)state.getContextParameter(SOS_PARAM_LATITUDE));
		Double longitude = Double.parseDouble((String)state.getContextParameter(SOS_PARAM_LONGITUDE));
		Double altitude = Double.parseDouble(SOS_DEFAULT_ALTITUDE);
		registerMeasurement.setPosition(longitude, latitude, altitude);
		
		registerMeasurement.setPhenomenon((String)state.getContextParameter(SOS_PARAM_PHENOMENON_URN));
		registerMeasurement.setOfferingID(SOS_DEFAULT_OFFERING_ID);
		registerMeasurement.setUnitOfMeasurement(SOS_DEFAULT_UOM);
		
		return registerMeasurement.createXML();
	}
	
	public String formatSosInsertMeasurementMessage(IContext state, IDataElement data) {

		if (data!=null && data.getValue()!=null) {
			SOSInsertMeasurement insertMeasurement = new SOSInsertMeasurement();
			insertMeasurement.setOwner((String)state.getContextParameter(SOS_PARAM_OWNER));
			insertMeasurement.setUniqueID((String)state.getContextParameter(SOS_PARAM_UNIQUE_ID));
			//System.out.println("TIMESTAMP--------->"+dateFormatter.format(data.getTimestamp()));			
			insertMeasurement.setTimestamp(data.getTimestamp());
			//Date timestampOfData=new Date();
			//System.out.println("TIMESTAMP--------->"+dateFormatter.format(timestampOfData));
			//insertMeasurement.setTimestamp(timestampOfData);

			insertMeasurement.setPhenomenon((String)state.getContextParameter(SOS_PARAM_PHENOMENON_URN));
			
			Double latitude = Double.parseDouble((String)state.getContextParameter(SOS_PARAM_LATITUDE));
			Double longitude = Double.parseDouble((String)state.getContextParameter(SOS_PARAM_LONGITUDE));
			insertMeasurement.setPosition(longitude, latitude);
		
			insertMeasurement.setValue((Double)data.getValue());
	
			return insertMeasurement.createXML();
		}else
				return null;
	}	
	/*
	private String getInsertionXML (String filePosition) {
		
		String xmltoString = null;
		boolean first = true;

		try {
			InputStream xmlInput = this.getClass().getResourceAsStream(filePosition);

			InputStreamReader xmlStreamReader = new InputStreamReader(xmlInput);
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(xmlStreamReader);
			String read = br.readLine();

			while(read != null) {
				if (first) {
					sb.append(read);
					first = false;
				}
				else
					sb.append("\n" + read);

				read = br.readLine();
			}

			xmltoString = sb.toString();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return xmltoString;
	}
	*/
	@Override
	public void initialize(IContext state, HashMap<String, InputParameter> iparams) throws FormatterInitializationException {
		super.initialize(state, iparams);
		state.setContextParameter("isRegistered", false);
	}

	@Override
	public boolean allowsMultipleInputs() {	
		return false;
	}	
}


