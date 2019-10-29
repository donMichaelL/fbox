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

public class SOSInsertObservationDavis {

	private String owner;
	private String unigueID;
	private String longitude;
	private String latitude;
	private String timestamp;
	private String temperatureValue;
	private String humidityValue;
	private String rainRateValue;
	private String windDirectionValue;
	private String windSpeedValue;
	
	private String xml; //In order to load xml only once
	
	private final String dateFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
	
	public SOSInsertObservationDavis (String filePosition) {
		this.owner = null;
		this.unigueID = null;
		this.longitude = null;
		this.latitude = null;
		this.timestamp = null;
		this.temperatureValue = null;
		this.humidityValue = null;
		this.rainRateValue = null;
		this.windDirectionValue = null;
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
	 * {VALUE_2} (For "humidity" PHENOMENON)
	 */
	//Method for setting the value of the {VALUE_2} tag (for "humidity" the value can only be double)
	public void setHumidityValue (double value) {
		this.humidityValue = Double.toString(value);
	}
	
	//Method for getting the value of the {VALUE_2} tag (for "humidity" the value can only be double)
	public double getHumidityValue () {
		return Double.parseDouble(this.humidityValue);
	}
	
	/**
	 * {VALUE_3} (For "windSpeed" PHENOMENON)
	 */
	//Method for setting the value of the {VALUE_3} tag (for "windSpeed" the value can only be double)
	public void setWindSpeedValue (double value) {
		this.windSpeedValue = Double.toString(value);
	}
	
	//Method for getting the value of the {VALUE_3} tag (for "windSpeed" the value can only be double)
	public double getWindSpeedValue () {
		return Double.parseDouble(this.windSpeedValue);
	}
	
	/**
	 * {VALUE_4} (For "windDirection" PHENOMENON)
	 */
	//Method for setting the value of the {VALUE_4} tag (for "windDirection" the value can only be double)
	public void setWindDirectionValue (double value) {
		this.windDirectionValue = Double.toString(value);
	}
	
	//Method for getting the value of the {VALUE_4} tag (for "windDirection" the value can only be double)
	public double getWindDirectionValue () {
		return Double.parseDouble(this.windDirectionValue);
	}
	
	/**
	 * {VALUE_5} (For "rainRate" PHENOMENON)
	 */
	//Method for setting the value of the {VALUE_5} tag (for "rainRate" the value can only be double)
	public void setRainRateValue (double value) {
		this.rainRateValue = Double.toString(value);
	}
	
	//Method for getting the value of the {VALUE_5} tag (for "rainRate" the value can only be double)
	public double getRainRateValue () {
		return Double.parseDouble(this.rainRateValue);
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
		
		if(this.temperatureValue == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"VALUE_1\" (for temperature PHENOMENON) hasn't been set!");
		}
		
		if(this.humidityValue == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"VALUE_2\" (for humidity PHENOMENON) hasn't been set!");
		}
		
		if(this.windSpeedValue == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"VALUE_3\" (for windSpeed PHENOMENON) hasn't been set!");
		}
		
		if(this.windDirectionValue == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"VALUE_4\" (for windDirection PHENOMENON) hasn't been set!");
		}
		
		if(this.rainRateValue == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"VALUE_5\" (for rainRate PHENOMENON) hasn't been set!");
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
			xmlToChangeString = xmlToChangeString.replaceAll("\\{VALUE_1\\}",this.temperatureValue);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{VALUE_2\\}",this.humidityValue);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{VALUE_3\\}",this.windSpeedValue);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{VALUE_4\\}",this.windDirectionValue);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{VALUE_5\\}",this.rainRateValue);
			
			//System.out.println(this.xml.toString());
			//System.out.println(xmlToChangeString.toString());
			
			return xmlToChangeString;
		}
	}
	
}
