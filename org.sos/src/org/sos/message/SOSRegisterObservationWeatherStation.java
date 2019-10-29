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

public class SOSRegisterObservationWeatherStation {

	private String owner;
	private String unigueID;
	private String longitude;
	private String latitude;
	private String altitude;
	private String status;
	private String offeringID;
	
	private String phenomenon1; //humidity
	private String phenomenonURN1;
	private String unitHumidity;
	private String phenomenon2; //precipitation
	private String phenomenonURN2;
	private String unitPrecipitation;
	private String phenomenon3; //winddirection
	private String phenomenonURN3;
	private String unitWindDirection;
	private String phenomenon4; //temperature
	private String phenomenonURN4;
	private String unitTemperature;
	private String phenomenon5; //pressure
	private String phenomenonURN5;
	private String unitPressure;
	private String phenomenon6; //windspeed
	private String phenomenonURN6;
	private String unitWindSpeed;
	
	private String xml; //In order to load xml only once
	
	public SOSRegisterObservationWeatherStation (String filePosition) {
		this.owner = null;
		this.unigueID = null;
		this.longitude = null;
		this.latitude = null;
		this.altitude = null;
		this.status = null;
		this.offeringID = null;
		this.phenomenon1 = null;
		this.phenomenonURN1 = null;
		this.unitHumidity = null;
		this.phenomenon2 = null;
		this.phenomenonURN2 = null;
		this.unitPrecipitation = null;
		this.phenomenon3 = null;
		this.phenomenonURN3 = null;
		this.unitWindDirection = null;
		this.phenomenon4 = null;
		this.phenomenonURN4 = null;
		this.unitTemperature = null;
		this.phenomenon5 = null;
		this.phenomenonURN5 = null;
		this.unitPressure = null;
		this.phenomenon6 = null;
		this.phenomenonURN6 = null;
		this.unitWindSpeed = null;

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
	 * {UNIT_OF_MEASUREMENT_1} (For "humidity")
	 */
	//Method for setting the value of the {UNIT_OF_MEASUREMENT_1} tag
	public void setUnitOfHumidity (String unit) {
		this.unitHumidity = unit;
	}
	
	//Method for getting the value of the "unitHumidity" field
	public String getUnitOfHumidity () {
		return this.unitHumidity;
	}
	
	/**
	 * {UNIT_OF_MEASUREMENT_2} (For "precipitation")
	 */
	//Method for setting the value of the {UNIT_OF_MEASUREMENT_2} tag
	public void setUnitOfPrecipitation (String unit) {
		this.unitPrecipitation = unit;
	}
	
	//Method for getting the value of the "unitPrecipitation" field
	public String getUnitOfPrecipitation () {
		return this.unitPrecipitation;
	}
	
	/**
	 * {UNIT_OF_MEASUREMENT_3} (For "winddirection")
	 */
	//Method for setting the value of the {UNIT_OF_MEASUREMENT_3} tag
	public void setUnitOfWindDirection (String unit) {
		this.unitWindDirection = unit;
	}
	
	//Method for getting the value of the "unitWindDirection" field
	public String getUnitOfWindDirection () {
		return this.unitWindDirection;
	}
	
	/**
	 * {UNIT_OF_MEASUREMENT_4} (For "temperature")
	 */
	//Method for setting the value of the {UNIT_OF_MEASUREMENT_4} tag
	public void setUnitOfTemperature (String unit) {
		this.unitTemperature = unit;
	}
	
	//Method for getting the value of the "unitTemperature" field
	public String getUnitOfTemperature () {
		return this.unitTemperature;
	}
	
	/**
	 * {UNIT_OF_MEASUREMENT_5} (For "pressure")
	 */
	//Method for setting the value of the {UNIT_OF_MEASUREMENT_5} tag
	public void setUnitOfPressure (String unit) {
		this.unitPressure = unit;
	}
	
	//Method for getting the value of the "unitPressure" field
	public String getUnitOfPressure () {
		return this.unitPressure;
	}
	
	/**
	 * {UNIT_OF_MEASUREMENT_6} (For "windspeed")
	 */
	//Method for setting the value of the {UNIT_OF_MEASUREMENT_6} tag
	public void setUnitOfWindSpeed (String unit) {
		this.unitWindSpeed = unit;
	}
	
	//Method for getting the value of the "unitWindSpeed" field
	public String getUnitOfWindSpeed () {
		return this.unitWindSpeed;
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
		
		if(this.phenomenon1 == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"PHENOMENON_1\" hasn't been set!");
		}
		
		if(this.phenomenonURN1 == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"PHENOMENON_URN_1\" hasn't been set!");
		}
		
		if(this.unitHumidity == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"UNIT_OF_MEASUREMENT_1\" hasn't been set!");
		}
		
		if(this.phenomenon2 == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"PHENOMENON_2\" hasn't been set!");
		}
		
		if(this.phenomenonURN2 == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"PHENOMENON_URN_2\" hasn't been set!");
		}
		
		if(this.unitPrecipitation == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"UNIT_OF_MEASUREMENT_2\" hasn't been set!");
		}
		
		if(this.phenomenon3 == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"PHENOMENON_3\" hasn't been set!");
		}
		
		if(this.phenomenonURN3 == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"PHENOMENON_URN_3\" hasn't been set!");
		}
		
		if(this.unitWindDirection == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"UNIT_OF_MEASUREMENT_3\" hasn't been set!");
		}
		
		if(this.phenomenon4 == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"PHENOMENON_4\" hasn't been set!");
		}
		
		if(this.phenomenonURN4 == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"PHENOMENON_URN_4\" hasn't been set!");
		}
		
		if(this.unitTemperature == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"UNIT_OF_MEASUREMENT_4\" hasn't been set!");
		}
		
		if(this.phenomenon5 == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"PHENOMENON_5\" hasn't been set!");
		}
		
		if(this.phenomenonURN5 == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"PHENOMENON_URN_5\" hasn't been set!");
		}
		
		if(this.unitPressure == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"UNIT_OF_MEASUREMENT_5\" hasn't been set!");
		}
		
		if(this.phenomenon6 == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"PHENOMENON_6\" hasn't been set!");
		}
		
		if(this.phenomenonURN6 == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"PHENOMENON_URN_6\" hasn't been set!");
		}
		
		if(this.unitWindSpeed == null) {
			validation = false;
			System.err.println("[ERROR] Value for the field \"UNIT_OF_MEASUREMENT_6\" hasn't been set!");
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
			xmlToChangeString = xmlToChangeString.replaceAll("\\{ALTITUDE\\}",this.altitude);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{STATUS\\}",this.status);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{OFFERING_ID\\}",this.offeringID);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON_1\\}",this.phenomenon1);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON_URN_1\\}",this.phenomenonURN1);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{UNIT_OF_MEASUREMENT_1\\}",this.unitHumidity);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON_2\\}",this.phenomenon2);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON_URN_2\\}",this.phenomenonURN2);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{UNIT_OF_MEASUREMENT_2\\}",this.unitPrecipitation);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON_3\\}",this.phenomenon3);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON_URN_3\\}",this.phenomenonURN3);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{UNIT_OF_MEASUREMENT_3\\}",this.unitWindDirection);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON_4\\}",this.phenomenon4);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON_URN_4\\}",this.phenomenonURN4);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{UNIT_OF_MEASUREMENT_4\\}",this.unitTemperature);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON_5\\}",this.phenomenon5);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON_URN_5\\}",this.phenomenonURN5);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{UNIT_OF_MEASUREMENT_5\\}",this.unitPressure);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON_6\\}",this.phenomenon6);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{PHENOMENON_URN_6\\}",this.phenomenonURN6);
			xmlToChangeString = xmlToChangeString.replaceAll("\\{UNIT_OF_MEASUREMENT_6\\}",this.unitWindSpeed);
			
			//System.out.println(this.xml.toString());
			//System.out.println(xmlToChangeString.toString());
			
			return xmlToChangeString;
		}
	}
	
}
