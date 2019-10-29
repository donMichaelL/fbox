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

public class SOSInsertObservationSunSPOT {

	private String owner;
	private String unigueID;
	private String longitude;
	private String latitude;
	private String timestamp;
	
	private String phenomenon1; //temperature
	private String phenomenonURN1;
	private String temperatureValue;
	private String phenomenon2; //light
	private String phenomenonURN2;
	private String lightValue;
	
	private String xml; //In order to load xml only once
	
	private final String dateFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
	
	public SOSInsertObservationSunSPOT (String filePosition) {
		this.owner = null;
		this.unigueID = null;
		this.longitude = null;
		this.latitude = null;
		this.timestamp = null;
		this.phenomenon1 = null;
		this.phenomenonURN1 = null;
		this.temperatureValue = null;
		this.phenomenon2 = null;
		this.phenomenonURN2 = null;
		this.lightValue = null;		
		
		this.xml = this.getInsertionXML(filePosition);
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
	 * {PHENOMENON_1},{PHENOMENON_URN_1} (For "temperature")
	 */
	//Method for setting the value of the {PHENOMENON_1} and {PHENOMENON_URN_1} tag (equals "temperature")
	public void setTemperaturePhenomenon (String phen) {
		this.phenomenon1 = phen;
		this.phenomenonURN1 = "urn:ogc:def:phenomenon:OGC:1.0.30:"+phen;
	}
	
	//Method for getting the value of the "phenomenon1" field
	public String getTemperaturePhenomenon () {
		return this.phenomenon1;
	}
	
	/**
	 * {PHENOMENON_2},{PHENOMENON_URN_2} (For "light")
	 */
	//Method for setting the value of the {PHENOMENON_2} and {PHENOMENON_URN_2} tag (equals "light")
	public void setLightPhenomenon (String phen) {
		this.phenomenon2 = phen;
		this.phenomenonURN2 = "urn:ogc:def:phenomenon:OGC:1.0.30:"+phen;
	}
	
	//Method for getting the value of the "phenomenon2" field
	public String getLightPhenomenon () {
		return this.phenomenon2;
	}
	
	/**
	 * {VALUE_1} (For "temperature" PHENOMENON)
	 */
	//Method for setting the value of the {VALUE_1} tag (for "temperature" the value can only be double)
	public void setTemperatureValue (double value) {
		this.temperatureValue = Double.toString(value);
	}
	
	//Method for getting the value of the {VALUE_1} tag (for "temperature" the value can only be double)
	public double getTemperatureValue () {
		return Double.parseDouble(this.temperatureValue);
	}
	
	/**
	 * {VALUE_2} (For "light" PHENOMENON)
	 */
	//Method for setting the value of the {VALUE_2} tag (for "light" the value can only be double)
	public void setLightValue (double value) {
		this.lightValue = Double.toString(value);
	}
	
	//Method for getting the value of the {VALUE_2} tag (for "light" the value can only be double)
	public double getLightValue () {
		return Double.parseDouble(this.lightValue);
	}
	
	private String getInsertionXML (String filePosition) {
		
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
		
		if(this.temperatureValue == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"VALUE_1\" (for humidity PHENOMENON) hasn't been set!");
		}
		
		if(this.lightValue == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"VALUE_2\" (for precipitation PHENOMENON) hasn't been set!");
		}
		
		if(this.timestamp == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"TIMESTAMP\" hasn't been set!");
		}
		
		if(this.phenomenon1 == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"PHENOMENON_1\" hasn't been set!");
		}
		
		if(this.phenomenonURN1 == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"PHENOMENON_URN_1\" hasn't been set!");
		}
		
		if(this.phenomenon2 == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"PHENOMENON_2\" hasn't been set!");
		}
		
		if(this.phenomenonURN2 == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"PHENOMENON_URN_2\" hasn't been set!");
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
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON_1\\}",this.phenomenon1);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON_URN_1\\}",this.phenomenonURN1);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{VALUE_1\\}",this.temperatureValue);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON_2\\}",this.phenomenon2);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON_URN_2\\}",this.phenomenonURN2);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{VALUE_2\\}",this.lightValue);
			
			//System.out.println(this.xml.toString());
			//System.out.println(xmlToChangeString.toString());
			
			return xmlToChangeString;
		}
	}
	
}
