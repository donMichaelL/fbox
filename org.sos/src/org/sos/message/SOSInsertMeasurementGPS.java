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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

public class SOSInsertMeasurementGPS {

	private String owner;
	private String unigueID;
	private String longitude;
	private String latitude;
	private String timestamp;
	private String longitudeNEW;
	private String latitudeNEW;
	
	private String xml; //In order to load xml only once
	
	private final String dateFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
	
	public SOSInsertMeasurementGPS (String filePosition) {
		this.owner = null;
		this.unigueID = null;
		this.longitude = null;
		this.latitude = null;
		this.timestamp = null;
		this.longitudeNEW = null;
		this.latitudeNEW = null;
		
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
	 * {LONGITUDE_NEW}, {LATITUDE_NEW}
	 */
	//Method for setting the values of the {LONGITUDE_NEW} and {LATITUDE_NEW} tags
	public void setNewLocation (double longitude, double latitude) {
		this.longitudeNEW = Double.toString(longitude);
		this.latitudeNEW = Double.toString(latitude);
	}
	
	//Method for getting the value of the "longitudeNew" field
	public double getNewLongitude () {
		return Double.parseDouble(this.longitudeNEW);
	}
	
	//Method for getting the value of the "latitudeNew" field
	public double getNewLatitude () {
		return Double.parseDouble(this.latitudeNEW);
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
		
		if(this.longitude == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"LONGITUDE\" hasn't been set!");
		}
		
		if(this.latitude == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"LATITUDE\" hasn't been set!");
		}
		
		if(this.longitudeNEW == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"LONGITUDE_NEW\" hasn't been set!");
		}
		
		if(this.latitudeNEW == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"LATITUDE_NEW\" hasn't been set!");
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
			xmlToChangeString = xmlToChangeString.replaceAll("\\{TIMESTAMP\\}",this.timestamp);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{LONGITUDE_NEW\\}",this.longitudeNEW);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{LATITUDE_NEW\\}",this.latitudeNEW);
			
			//System.out.println(this.xml.toString());
			//System.out.println(xmlToChangeString.toString());
			
			return xmlToChangeString;
		}
	}
}
