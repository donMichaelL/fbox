package org.sos.message;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

public class SOSRegisterObservationDavis {

	private String owner;
	private String unigueID;
	private String status;
	private String longitude;
	private String latitude;
	private String altitude;
	private String offeringID;
	
	private String xml; //In order to load xml only once
	
	public SOSRegisterObservationDavis (String filePosition) {
		this.owner = null;
		this.unigueID = null;
		this.status = null;
		this.longitude = null;
		this.latitude = null;
		this.altitude = null;
		this.offeringID = null;
		
		this.xml = this.getRegistrationXML(filePosition);
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
		String systemType = null; //"http" or "C" expected
		StringTokenizer st = new StringTokenizer(filePosition,":");
		
		systemType = st.nextToken();
		
		try {
			if(systemType.equalsIgnoreCase("http")) //Retrieve file from a server
			{
				URL url = new URL(filePosition);
				URLConnection urlConnection = url.openConnection();
				InputStream is = urlConnection.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
	
				int numCharsRead;
				char[] charArray = new char[1024];
				StringBuffer sb = new StringBuffer();
				while ((numCharsRead = isr.read(charArray)) > 0) {
					sb.append(charArray, 0, numCharsRead);
				}
	
				xmltoString = sb.toString();
			}
			else //Retrieve file from the file system
			{
				byte[] buffer = new byte[(int) new File(filePosition).length()];
			    BufferedInputStream f = null;
			    try {
			        f = new BufferedInputStream(new FileInputStream(filePosition));
			        f.read(buffer);
			    } finally {
			        if (f != null) try { f.close(); } catch (IOException ignored) { }
			    }
			    
			    xmltoString = new String(buffer);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
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
		
		if(this.offeringID == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"OFFERING_ID\" hasn't been set!");
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
			
			//System.out.println(this.xml.toString());
			//System.out.println(xmlToChangeString.toString());
			
			return xmlToChangeString;
		}
	}
}
