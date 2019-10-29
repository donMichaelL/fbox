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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

public class SOSInsertMeasurement {

	private String owner;
	private String unigueID;
	private String longitude;
	private String latitude;
	private String phenomenonURN;
	private String timestamp;
	private String value;
	
	private String path;
	private String fileName;
	private String xml; //In order to load xml only once
	
	private final String dateFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
	
	public SOSInsertMeasurement () {
		this.owner = null;
		this.unigueID = null;
		this.longitude = null;
		this.latitude = null;
		this.phenomenonURN = null;
		this.timestamp = null;
		this.value = null;
		
		this.path = "/xml_templates/";
		this.fileName = "Template_InsertObservation_Measurement.xml";
		this.xml = this.getInsertionXML(this.path + this.fileName);
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
	 * {LONGITUDE}, {LATITUDE}
	 */
	//Method for setting the values of the {LONGITUDE} and {LATITUDE} tags
	public void setPosition (double longitude, double latitude) {
		this.longitude = Double.toString(longitude);
		this.latitude = Double.toString(latitude);
	}
	
	//Method for getting the value of the "longitude" field
	public double getLongitude () {
		return Double.parseDouble(this.longitude);
	}
	
	//Method for getting the value of the "latitude" field
	public double getLatitude () {
		return Double.parseDouble(this.latitude);
	}
	
	/**
	 * {PHENOMENON_URN}
	 */
	//Method for setting the value of the {PHENOMENON} tags
	public void setPhenomenon (String phenomenon) {
		this.phenomenonURN = "urn:ogc:def:phenomenon:OGC:1.0.30:"+phenomenon;
	}
	
	//Method for getting the value of the "phenomenonURN" field
	public String getPhenomenon () {
		return this.phenomenonURN;
	}
	
	/**
	 * {TIMESTAMP}
	 */
	//Method for setting the value of the {TIMESTAMP} tags
	public void setTimestamp (Date currentTime) {
		SimpleDateFormat sdf = new SimpleDateFormat(this.dateFormat);
		this.timestamp = sdf.format(currentTime);
	}
	
	//Method for getting the value of the "timestamp" field
	public String getTimestamp () {
		return this.timestamp;
	}
	
	/**
	 * {VALUE}
	 */
	//Methods for setting the value of the {VALUE} tag (for measurement the value can only be numeric)
	public void setValue (Comparable<?> value) {
		this.value = value.toString();
	}
	
	
	//Methods for getting the value of the {VALUE} tag (for measurement the value can only be numeric)
	public int getIntValue () {
		return Integer.parseInt(this.value);
	}
	
	public double getDoubleValue () {
		return Double.parseDouble(this.value);
	}
	
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
		
		if(this.longitude == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"LONGITUDE\" hasn't been set!");
		}
		
		if(this.latitude == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"LATITUDE\" hasn't been set!");
		}
		
		if(this.phenomenonURN == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"PHENOMENON_URN\" hasn't been set!");
		}
		
		if(this.value == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"VALUE\" hasn't been set!");
		}
		
		if(this.timestamp == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"TIMESTAMP\" hasn't been set!");
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
			xmlToChangeString = xmlToChangeString.replaceAll("\\{LONGITUDE\\}",this.longitude);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{LATITUDE\\}",this.latitude);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON_URN\\}",this.phenomenonURN);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{TIMESTAMP\\}",this.timestamp);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{VALUE\\}",this.value);
			
			//System.out.println(this.xml.toString());
			//System.out.println(xmlToChangeString.toString());
			
			return xmlToChangeString;
		}
	}
}
