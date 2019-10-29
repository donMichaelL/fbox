package org.sos.message;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

public class SOSRegisterMeasurement {

	private String owner;
	private String unigueID;
	private String status;
	private String longitude;
	private String latitude;
	private String altitude;
	private String phenomenon;
	private String phenomenonURN;
	private String offeringID;
	private String unit;
	
	private String path;
	private String fileName;
	private String xml; //In order to load xml only once
	
	public SOSRegisterMeasurement () {
		this.owner = null;
		this.unigueID = null;
		this.status = null;
		this.longitude = null;
		this.latitude = null;
		this.altitude = null;
		this.phenomenon = null;
		this.phenomenonURN = null;
		this.offeringID = null;
		this.unit = null;
		
		this.path = "/xml_templates/";
		this.fileName = "Template_RegisterSensor_Measurement.xml";
		this.xml = this.getRegistrationXML(this.path + this.fileName);
	}
	
	/**
	 * {OWNER}
	 */
	//Method for setting the value of the {OWNER} tag
	public void setOwner (String owner) {
		this.owner = owner;
	}
	
	//Method for getting the value of the "owner" field
	public String getOwner () {
		return this.owner;
	}
	
	/**
	 * {UNIQUE_ID}
	 */
	//Method for setting the value of the {UNIQUE_ID} tag
	public void setUniqueID (String sensID) {
		this.unigueID = sensID;
	}
	
	//Method for getting the value of the "uniqueID" field
	public String getUnigueID () {
		return this.unigueID;
	}
	
	/**
	 * {LONGITUDE}, {LATITUDE}, {ALTITUDE}
	 */
	//Method for setting the values of the {LONGITUDE}, {LATITUDE} and {ALTITUDE} tags
	public void setPosition (double longitude, double latitude, double altitude) {
		
		this.longitude = Double.toString(longitude);
		this.latitude = Double.toString(latitude);
		this.altitude = Double.toString(altitude);
	}
	
	//Method for getting the value of the "longitude" field
	public double getLongitude () {
		return Double.parseDouble(this.longitude);
	}
	
	//Method for getting the value of the "latitude" field
	public double getLatitude () {
		return Double.parseDouble(this.latitude);
	}
	
	//Method for getting the value of the "altitude" field
	public double getAltitude () {
		return Double.parseDouble(this.altitude);
	}
	
	/**
	 * {PHENOMENON}, {PHENOMENON_URN}
	 */
	//Method for setting the values of the {PHENOMENON} and {PHENOMENON_URN} tags
	public void setPhenomenon (String phenomenon) {
		this.phenomenon = phenomenon;
		this.phenomenonURN = "urn:ogc:def:phenomenon:OGC:1.0.30:"+phenomenon;
	}
	
	//Method for getting the value of the "phenomenon" field
	public String getPhenomenon () {
		return this.phenomenon;
	}
	
	//Method for getting the value of the "phenomenonURN" field
	public String getPhenomenonURN () {
		return this.phenomenonURN;
	}
	
	/**
	 * {OFFERING_ID}
	 */
	//Method for setting the value of the {OFFERING_ID} tag
	public void setOfferingID (String offering) {
		this.offeringID = offering;
	}
	
	//Method for getting the value of the "offeringID" field
	public String getOfferingID () {
		return this.offeringID;
	}
	
	/**
	 * {UNIT_OF_MEASUREMENT}
	 */
	//Method for setting the value of the {UNIT_OF_MEASUREMENT} tag
	public void setUnitOfMeasurement (String unit) {
		this.unit = unit;
	}
	
	//Method for getting the value of the "unit" field
	public String getUnit () {
		return this.unit;
	}
	
	/**
	 * {STATUS}
	 */
	//Method for setting the value of the {STATUS} tag
	public void setStatus (boolean status) {
		if(status == true)
			this.status = "true";
		else
			if(status == false)
				this.status = "false";
	}

	//Method for getting the value of the "status" field
	public boolean getStatus () {
		boolean value = false;
		
		if(this.status.equalsIgnoreCase("true"))
			value = true;
		else
			if(this.status.equalsIgnoreCase("false"))
				value = false;
		
		return value;
	}
	
	private String getRegistrationXML (String filePosition) {
		
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
	
	private boolean validateFields () {
		boolean validation = true;
		
		if(this.owner == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"OWNER\" hasn't been set!");
		}
		
		if(this.unigueID == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"UNIQUE_ID\" hasn't been set!");
		}
		
		if(this.status == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"STATUS\" hasn't been set!");
		}
		
		if(this.status != null) {
			if((this.status.compareToIgnoreCase("true") != 0) && (this.status.compareToIgnoreCase("false") != 0)) {
				validation = false;
				System.err.println("[ERROR] Values for the field \"STATUS\" can be {true,false}!");
			}
		}
		
		if(this.longitude == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"LONGITUDE\" hasn't been set!");
		}
		
		if(this.latitude == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"LATITUDE\" hasn't been set!");
		}
		
		if(this.altitude == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"ALTITUDE\" hasn't been set!");
		}
		
		if(this.altitude != null) {
			if(Double.parseDouble(this.altitude) < 0) {
				validation = false;
				System.err.println("[ERROR] Value for the field \"ALTITUDE\" cannot be negative!");
			}
		}
		
		if(this.phenomenon == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"PHENOMENON\" hasn't been set!");
		}
		
		if(this.phenomenonURN == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"PHENOMENON_URN\" hasn't been set!");
		}
		
		if(this.offeringID == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"OFFERING_ID\" hasn't been set!");
		}
		
		if(this.unit == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"UNIT_OF_MEASUREMENT\" hasn't been set!");
		}
		
		return validation;
	}
	
	public String createXML () {
		
		//Check if all the mandatory fields have been initialized properly with values
		if(this.validateFields() == false)
			return null;
		else {
			String xmlToChangeString = this.xml.toString();
			
			xmlToChangeString = xmlToChangeString.replaceAll("\\{OWNER\\}",this.owner);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{UNIQUE_ID\\}",this.unigueID);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{STATUS\\}",this.status);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{LONGITUDE\\}",this.longitude);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{LATITUDE\\}",this.latitude);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{ALTITUDE\\}",this.altitude);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{OFFERING_ID\\}",this.offeringID);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON\\}",this.phenomenon);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON_URN\\}",this.phenomenonURN);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{UNIT_OF_MEASUREMENT\\}",this.unit);
			
			//System.out.println(this.xml.toString());
			//System.out.println(xmlToChangeString.toString());
			
			return xmlToChangeString;
		}
	}
}
