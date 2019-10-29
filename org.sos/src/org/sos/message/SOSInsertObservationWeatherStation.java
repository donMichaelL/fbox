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

public class SOSInsertObservationWeatherStation {

	private String owner;
	private String unigueID;
	private String longitude;
	private String latitude;
	private String timestamp;
	
	private String phenomenon1; //humidity
	private String phenomenonURN1;
	private String humidityValue;
	private String phenomenon2; //precipitation
	private String phenomenonURN2;
	private String precipitationValue;
	private String phenomenon3; //winddirection
	private String phenomenonURN3;
	private String windDirectionValue;
	private String phenomenon4; //temperature
	private String phenomenonURN4;
	private String temperatureValue;
	private String phenomenon5; //pressure
	private String phenomenonURN5;
	private String pressureValue;
	private String phenomenon6; //windspeed
	private String phenomenonURN6;
	private String windSpeedValue;
	
	private String xml; //In order to load xml only once
	
	private final String dateFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
	
	public SOSInsertObservationWeatherStation (String filePosition) {
		this.owner = null;
		this.unigueID = null;
		this.longitude = null;
		this.latitude = null;
		this.timestamp = null;
		this.phenomenon1 = null;
		this.phenomenonURN1 = null;
		this.humidityValue = null;
		this.phenomenon2 = null;
		this.phenomenonURN2 = null;
		this.precipitationValue = null;		
		this.phenomenon3 = null;
		this.phenomenonURN3 = null;
		this.windDirectionValue = null;
		this.phenomenon4 = null;
		this.phenomenonURN4 = null;
		this.temperatureValue = null;
		this.phenomenon5 = null;
		this.phenomenonURN5 = null;
		this.pressureValue = null;
		this.phenomenon6 = null;
		this.phenomenonURN6 = null;
		this.windSpeedValue = null;

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
	 * {PHENOMENON_1},{PHENOMENON_URN_1} (For "humidity")
	 */
	//Method for setting the value of the {PHENOMENON_1} and {PHENOMENON_URN_1} tag (equals "humidity")
	public void setHumidityPhenomenon (String phen) {
		this.phenomenon1 = phen;
		this.phenomenonURN1 = "urn:ogc:def:phenomenon:OGC:1.0.30:"+phen;
	}
	
	//Method for getting the value of the "phenomenon1" field
	public String getHumidityPhenomenon () {
		return this.phenomenon1;
	}
	
	/**
	 * {PHENOMENON_2},{PHENOMENON_URN_2} (For "precipitation")
	 */
	//Method for setting the value of the {PHENOMENON_2} and {PHENOMENON_URN_2} tag (equals "precipitation")
	public void setPrecipitationPhenomenon (String phen) {
		this.phenomenon2 = phen;
		this.phenomenonURN2 = "urn:ogc:def:phenomenon:OGC:1.0.30:"+phen;
	}
	
	//Method for getting the value of the "phenomenon2" field
	public String getPrecipitationPhenomenon () {
		return this.phenomenon2;
	}
	
	/**
	 * {PHENOMENON_3},{PHENOMENON_URN_3} (For "winddirection")
	 */
	//Method for setting the value of the {PHENOMENON_3} and {PHENOMENON_URN_3} tag (equals "winddirection")
	public void setWindDirectionPhenomenon (String phen) {
		this.phenomenon3 = phen;
		this.phenomenonURN3 = "urn:ogc:def:phenomenon:OGC:1.0.30:"+phen;
	}
	
	//Method for getting the value of the "phenomenon3" field
	public String getWindDirectionPhenomenon () {
		return this.phenomenon3;
	}
	
	/**
	 * {PHENOMENON_4},{PHENOMENON_URN_4} (For "temperature")
	 */
	//Method for setting the value of the {PHENOMENON_4} and {PHENOMENON_URN_4} tag (equals "temperature")
	public void setTemperaturePhenomenon (String phen) {
		this.phenomenon4 = phen;
		this.phenomenonURN4 = "urn:ogc:def:phenomenon:OGC:1.0.30:"+phen;
	}
	
	//Method for getting the value of the "phenomenon4" field
	public String getTemperaturePhenomenon () {
		return this.phenomenon4;
	}
	
	/**
	 * {PHENOMENON_5},{PHENOMENON_URN_5} (For "pressure")
	 */
	//Method for setting the value of the {PHENOMENON_5} and {PHENOMENON_URN_5} tag (equals "pressure")
	public void setPressurePhenomenon (String phen) {
		this.phenomenon5 = phen;
		this.phenomenonURN5 = "urn:ogc:def:phenomenon:OGC:1.0.30:"+phen;
	}
	
	//Method for getting the value of the "phenomenon5" field
	public String getPressurePhenomenon () {
		return this.phenomenon5;
	}
	
	/**
	 * {PHENOMENON_6},{PHENOMENON_URN_6} (For "windspeed")
	 */
	//Method for setting the value of the {PHENOMENON_6} and {PHENOMENON_URN_6} tag (equals "windspeed")
	public void setWindSpeedPhenomenon (String phen) {
		this.phenomenon6 = phen;
		this.phenomenonURN6 = "urn:ogc:def:phenomenon:OGC:1.0.30:"+phen;
	}
	
	//Method for getting the value of the "phenomenon6" field
	public String getWindSpeedPhenomenon () {
		return this.phenomenon6;
	}
	
	/**
	 * {VALUE_1} (For "humidity" PHENOMENON)
	 */
	//Method for setting the value of the {VALUE_1} tag (for "humidity" the value can only be double)
	public void setHumidityValue (double value) {
		this.humidityValue = Double.toString(value);
	}
	
	//Method for getting the value of the {VALUE_1} tag (for "humidity" the value can only be double)
	public double getHumidityValue () {
		return Double.parseDouble(this.humidityValue);
	}
	
	/**
	 * {VALUE_2} (For "precipitation" PHENOMENON)
	 */
	//Method for setting the value of the {VALUE_2} tag (for "precipitation" the value can only be double)
	public void setPrecipitationValue (double value) {
		this.precipitationValue = Double.toString(value);
	}
	
	//Method for getting the value of the {VALUE_2} tag (for "precipitation" the value can only be double)
	public double getPrecipitationValue () {
		return Double.parseDouble(this.precipitationValue);
	}
	
	/**
	 * {VALUE_3} (For "windDirection" PHENOMENON)
	 */
	//Method for setting the value of the {VALUE_3} tag (for "windDirection" the value can only be double)
	public void setWindDirectionValue (double value) {
		this.windDirectionValue = Double.toString(value);
	}
	
	//Method for getting the value of the {VALUE_3} tag (for "windDirection" the value can only be double)
	public double getWindDirectionValue () {
		return Double.parseDouble(this.windDirectionValue);
	}
	
	/**
	 * {VALUE_4} (For "temperature" PHENOMENON)
	 */
	//Method for setting the value of the {VALUE_4} tag (for "temperature" the value can only be double)
	public void setTemperatureValue (double value) {
		this.temperatureValue = Double.toString(value);
	}
	
	//Method for getting the value of the {VALUE_4} tag (for "temperature" the value can only be double)
	public double getTemperatureValue () {
		return Double.parseDouble(this.temperatureValue);
	}
	
	/**
	 * {VALUE_5} (For "pressure" PHENOMENON)
	 */
	//Method for setting the value of the {VALUE_5} tag (for "pressure" the value can only be double)
	public void setPressureValue (double value) {
		this.pressureValue = Double.toString(value);
	}
	
	//Method for getting the value of the {VALUE_5} tag (for "pressure" the value can only be double)
	public double getPressureValue () {
		return Double.parseDouble(this.pressureValue);
	}
	
	/**
	 * {VALUE_6} (For "windSpeed" PHENOMENON)
	 */
	//Method for setting the value of the {VALUE_6} tag (for "windSpeed" the value can only be double)
	public void setWindSpeedValue (double value) {
		this.windSpeedValue = Double.toString(value);
	}
	
	//Method for getting the value of the {VALUE_6} tag (for "windSpeed" the value can only be double)
	public double getWindSpeedValue () {
		return Double.parseDouble(this.windSpeedValue);
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
		
		if(this.humidityValue == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"VALUE_1\" (for humidity PHENOMENON) hasn't been set!");
		}
		
		if(this.precipitationValue == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"VALUE_2\" (for precipitation PHENOMENON) hasn't been set!");
		}
		
		if(this.windDirectionValue == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"VALUE_3\" (for winddirection PHENOMENON) hasn't been set!");
		}
		
		if(this.temperatureValue == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"VALUE_4\" (for temperature PHENOMENON) hasn't been set!");
		}
		
		if(this.pressureValue == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"VALUE_5\" (for pressure PHENOMENON) hasn't been set!");
		}
		
		if(this.windSpeedValue == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"VALUE_6\" (for windspeed PHENOMENON) hasn't been set!");
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
		
		if(this.phenomenon3 == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"PHENOMENON_3\" hasn't been set!");
		}
		
		if(this.phenomenonURN3 == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"PHENOMENON_URN_3\" hasn't been set!");
		}
		
		if(this.phenomenon4 == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"PHENOMENON_4\" hasn't been set!");
		}
		
		if(this.phenomenonURN4 == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"PHENOMENON_URN_4\" hasn't been set!");
		}
		
		if(this.phenomenon5 == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"PHENOMENON_5\" hasn't been set!");
		}
		
		if(this.phenomenonURN5 == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"PHENOMENON_URN_5\" hasn't been set!");
		}
		
		if(this.phenomenon6 == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"PHENOMENON_6\" hasn't been set!");
		}
		
		if(this.phenomenonURN6 == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"PHENOMENON_URN_6\" hasn't been set!");
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
			xmlToChangeString = xmlToChangeString.replaceAll("\\{VALUE_1\\}",this.humidityValue);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON_2\\}",this.phenomenon2);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON_URN_2\\}",this.phenomenonURN2);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{VALUE_2\\}",this.precipitationValue);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON_3\\}",this.phenomenon3);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON_URN_3\\}",this.phenomenonURN3);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{VALUE_3\\}",this.windDirectionValue);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON_4\\}",this.phenomenon4);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON_URN_4\\}",this.phenomenonURN4);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{VALUE_4\\}",this.temperatureValue);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON_5\\}",this.phenomenon5);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON_URN_5\\}",this.phenomenonURN5);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{VALUE_5\\}",this.pressureValue);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON_6\\}",this.phenomenon6);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON_URN_6\\}",this.phenomenonURN6);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{VALUE_6\\}",this.windSpeedValue);
			
			//System.out.println(this.xml.toString());
			//System.out.println(xmlToChangeString.toString());
			
			return xmlToChangeString;
		}
	}
	
}
