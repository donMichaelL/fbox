package org.fbox.simulator;

import java.util.Date;
import java.util.StringTokenizer;

import org.apache.http.client.HttpClient;
import org.apache.log4j.PropertyConfigurator;
import org.fbox.xmlParser.XMLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sos.adapter.SimpleSOSHttpAdapter;
import org.sos.exception.SOSException;
import org.sos.message.SOSInsertMeasurement;
import org.sos.message.SOSInsertMeasurementGPS;
import org.sos.message.SOSInsertMeasurement_v350;
import org.sos.message.SOSInsertMobileSinglePhenomenon;
import org.sos.message.SOSInsertObservation4;
import org.sos.message.SOSInsertObservationDavis;
import org.sos.message.SOSInsertObservationSunSPOT;
import org.sos.message.SOSInsertObservationWeatherStation;

public class TaskPostObservationToSOS implements Runnable {

	private String recToSend;
	private SimpleSOSHttpAdapter sosClient;
	private String insertMeasTemplate;
	private String insertMeasGPSTemplate;
	private String insertObsDavisTemplate;
	private String insertObsWeatherTemplate;
	private String insertObsFlexitTemplate;
	private String insertObsSunSPOTTemplate;
	private Date startTime;
	private LogfileManipulator fileName;
	
	private MQpublisher publisher;
	
	private XMLParser parser;
	
	private final Logger log;
	
	public TaskPostObservationToSOS(String record, 
						 			String server,
						 			Date start,
						 			LogfileManipulator file,
						 			String dateForm,
						 			String insertMeasTemplate,
						 			String insertMeasGPSTemplate,
						 			String insertObsDavisTemplate,
						 			String insertObsWeatherTemplate,
						 			String insertObsFlexitTemplate,
						 			String insertObsSunSPOTTemplate,
						 			HttpClient httpClient,
						 			MQpublisher mqPub) { 
		
		log = LoggerFactory.getLogger(TaskPostObservationToSOS.class);
		PropertyConfigurator.configure("config/log4j.properties");
		
		this.recToSend = record;
		this.sosClient = new SimpleSOSHttpAdapter(httpClient, server);
		this.startTime = start;
		this.fileName = file;
		this.insertMeasTemplate = insertMeasTemplate;
		this.insertMeasGPSTemplate = insertMeasGPSTemplate;
		this.insertObsDavisTemplate = insertObsDavisTemplate;
		this.insertObsWeatherTemplate = insertObsWeatherTemplate;
		this.insertObsFlexitTemplate = insertObsFlexitTemplate;
		this.insertObsSunSPOTTemplate = insertObsSunSPOTTemplate;
		this.publisher = mqPub;
	}
	
	public void sendMeasurementToSOS (String record, LogfileManipulator currentFile) {
		
		long Timestamp, Interval;

		StringTokenizer st = new StringTokenizer(record, " ");
		//SOSInsertMeasurement insMeas = new SOSInsertMeasurement(this.insertMeasTemplate);
		SOSInsertMeasurement insMeas = new SOSInsertMeasurement();

		insMeas.setUniqueID(st.nextToken()); //The first token of the record(from a measurement logfile) is the Sensor ID
		
		Interval = Integer.parseInt(st.nextToken()); //The second token is the time interval
		Timestamp = this.startTime.getTime() + Interval;
		Date now = new Date(Timestamp);
		insMeas.setTimestamp(now);
		
		//Third token is Longitude, Fourth token in Latitude
		insMeas.setPosition(Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()));
		insMeas.setValue(Double.parseDouble(st.nextToken())); //Fifth token is the value for the measured phenomenon
		
		insMeas.setOwner(currentFile.OWNER);
		insMeas.setPhenomenon(currentFile.PHENOMENON[0]);
		
		String xml = insMeas.createXML();
		//System.out.println(xml);
		//this.parser = new XMLParser(xml);
		//System.out.println(this.parser.parseElement());
		
		if(xml != null)
		{
			if(Simulator.activeMQ == false)
			{
				try{
					log.info(this.sosClient.postXML(xml));
					
					//In order to know who is executing what!
			        //System.out.println("Thread ["+Thread.currentThread().getName()+"]: ");
					
			        //System.out.println("[DEBUG] Send record from file "+currentFile.FILENAME+" with interval from start time = "+Interval+" and timestamp = "+now.toString());
					now = new Date();
					log.info("{DEBUG} Send record from file \"{}\" with File_Interval = {} and Excecution_Interval = {}",currentFile.FILENAME,Interval,(now.getTime()-this.startTime.getTime()));
				} catch (SOSException e) {
					//In order to know who is executing what!
			        //System.out.println("Thread ["+Thread.currentThread().getName()+"]: ");
					log.warn("{SOS ERROR} {}",e.getMessage());
				}
			}
			else { //Send the SOS measurement message through activeMQ
				try {
					this.publisher.publishString(xml); //SOS message
				} catch (Exception e) {
					log.warn("{MQ ERROR} "+e.getMessage());
				}
			}
		}
	}
	
	public void sendMeasurementToSOS_v350 (String record, LogfileManipulator currentFile) {
		
		long Timestamp, Interval;

		StringTokenizer st = new StringTokenizer(record, " ");
		//SOSInsertMeasurement insMeas = new SOSInsertMeasurement(this.insertMeasTemplate);
		SOSInsertMeasurement_v350 insMeas = new SOSInsertMeasurement_v350();

		insMeas.setUniqueID(st.nextToken()); //The first token of the record(from a measurement logfile) is the Sensor ID
		
		Interval = Integer.parseInt(st.nextToken()); //The second token is the time interval
		Timestamp = this.startTime.getTime() + Interval;
		Date now = new Date(Timestamp);
		insMeas.setTimestamp(now);
		
		//Third token is Longitude, Fourth token in Latitude
		insMeas.setPosition(Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()));
		insMeas.setValue(Double.parseDouble(st.nextToken())); //Fifth token is the value for the measured phenomenon
		
		insMeas.setOwner(currentFile.OWNER);
		insMeas.setPhenomenon(currentFile.PHENOMENON[0]);
		insMeas.setUnitOfMeasurement(currentFile.PHENOMENON_UNIT[0]);
		
		String xml = insMeas.createXML();
		//System.out.println(xml);
		//this.parser = new XMLParser(xml);
		//System.out.println(this.parser.parseElement());
		
		if(xml != null)
		{
			if(Simulator.activeMQ == false)
			{
				try{
					log.info(this.sosClient.postXML(xml));
					
					//In order to know who is executing what!
			        //System.out.println("Thread ["+Thread.currentThread().getName()+"]: ");
					
			        //System.out.println("[DEBUG] Send record from file "+currentFile.FILENAME+" with interval from start time = "+Interval+" and timestamp = "+now.toString());
					now = new Date();
					log.info("{DEBUG} Send record from file \"{}\" with File_Interval = {} and Excecution_Interval = {}",currentFile.FILENAME,Interval,(now.getTime()-this.startTime.getTime()));
				} catch (SOSException e) {
					//In order to know who is executing what!
			        //System.out.println("Thread ["+Thread.currentThread().getName()+"]: ");
					log.warn("{SOS ERROR} {}",e.getMessage());
				}
			}
			else { //Send the SOS measurement message through activeMQ
				try {
					this.publisher.publishString(xml); //SOS message
				} catch (Exception e) {
					log.warn("{MQ ERROR} "+e.getMessage());
				}
			}
		}
	}
	
	public void sendMeasurementGPSToSOS (String record, LogfileManipulator currentFile) {
		
		long Timestamp, Interval;

		StringTokenizer st1 = new StringTokenizer(record, " ");
		SOSInsertMeasurementGPS insMeasGPS = new SOSInsertMeasurementGPS(this.insertMeasGPSTemplate);

		insMeasGPS.setUniqueID(st1.nextToken()); //The first token of the record(from a measurement logfile) is the Sensor ID
		
		Interval = Integer.parseInt(st1.nextToken()); //The second token is the time interval
		Timestamp = this.startTime.getTime() + Interval;
		Date now = new Date(Timestamp);
		insMeasGPS.setTimestamp(now);
		
		//Third token is Longitude, Fourth token in Latitude (Home Position of GPS sensor)
		insMeasGPS.setPosition(Double.parseDouble(st1.nextToken()), Double.parseDouble(st1.nextToken()));
		
		//In GPS sensor's case the 5th token of the line is the new position
		String position = st1.nextToken(); 
		
		//We have to tokenize this String with "," as delimiter
		StringTokenizer st2 = new StringTokenizer(position, ",");

		//First token is longNew and second is latNew
		insMeasGPS.setNewLocation(Double.parseDouble(st2.nextToken()), Double.parseDouble(st2.nextToken()));
		insMeasGPS.setOwner(currentFile.OWNER);
		
		String xml = insMeasGPS.createXML();

		if(xml != null)
		{
			if(Simulator.activeMQ == false) {
				try{
					log.info(this.sosClient.postXML(xml));
					
					//In order to know who is executing what!
			        //System.out.println("Thread ["+Thread.currentThread().getName()+"]: ");
					
			        //System.out.println("[DEBUG] Send record from file "+currentFile.FILENAME+" with interval from start time = "+Interval+" and timestamp = "+now.toString());
					now = new Date();
					log.info("{DEBUG} Send record from file \"{}\" with File_Interval = {} and Excecution_Interval = {}",currentFile.FILENAME,Interval,(now.getTime()-this.startTime.getTime()));
				} catch (SOSException e) {
					//In order to know who is executing what!
			        //System.out.println("Thread ["+Thread.currentThread().getName()+"]: ");
					log.warn("{SOS ERROR} {}",e.getMessage());
				}
			}
			else { //Send the SOS measurement message through activeMQ
				try {
					this.publisher.publishString(xml); //SOS message
				} catch (Exception e) {
					log.warn("{MQ ERROR} "+e.getMessage());
				}
			}
		}
	}
	
	public void sendMeasurementMobileToSOS (String record, LogfileManipulator currentFile) {
		
		SOSInsertMobileSinglePhenomenon insSimpleMobile = new SOSInsertMobileSinglePhenomenon();
		long Timestamp, Interval;
		StringTokenizer st1 = new StringTokenizer(record, " ");
		
		insSimpleMobile.setOwner(currentFile.OWNER);
		insSimpleMobile.setPhenomenon(currentFile.PHENOMENON[0]);
		insSimpleMobile.setUnitOfPhenomenon1(currentFile.PHENOMENON_UNIT[0]);
		
		insSimpleMobile.setUniqueID(st1.nextToken()); //The first token of the record(from a measurement logfile) is the Sensor ID
		
		Interval = Integer.parseInt(st1.nextToken()); //The second token is the time interval
		Timestamp = this.startTime.getTime() + Interval;
		Date now = new Date(Timestamp);
		insSimpleMobile.setTimestamp(now);
		
		//Third token is Longitude, Fourth token in Latitude (Home Position of Mobile simple sensor)
		insSimpleMobile.setPosition(Double.parseDouble(st1.nextToken()), Double.parseDouble(st1.nextToken()));

		//In Mobile simple sensor's case the 5th token of the line is the new position
		String position = st1.nextToken(); 

		//We have to tokenize this String with "," as delimiter
		StringTokenizer st2 = new StringTokenizer(position, ",");

		//First token is longNew and second is latNew
		insSimpleMobile.setNewLocation(Double.parseDouble(st2.nextToken()), Double.parseDouble(st2.nextToken()));
		
		//The 6th token is the value of the measured phenomenon from the simple sensor
		insSimpleMobile.setPhenomenonValue(Double.parseDouble(st1.nextToken())); //Fifth token is the value for the measured phenomenon

		String xml = insSimpleMobile.createXML();
		
		if(xml != null)
		{
			if(Simulator.activeMQ == false) {
				try{
					log.info(this.sosClient.postXML(xml));
					
					//In order to know who is executing what!
			        //System.out.println("Thread ["+Thread.currentThread().getName()+"]: ");
					
			        //System.out.println("[DEBUG] Send record from file "+currentFile.FILENAME+" with interval from start time = "+Interval+" and timestamp = "+now.toString());
					now = new Date();
					log.info("{DEBUG} Send record from file \"{}\" with File_Interval = {} and Excecution_Interval = {}",currentFile.FILENAME,Interval,(now.getTime()-this.startTime.getTime()));
				} catch (SOSException e) {
					//In order to know who is executing what!
			        //System.out.println("Thread ["+Thread.currentThread().getName()+"]: ");
					log.warn("{SOS ERROR} {}",e.getMessage());
				}
			}
			else { //Send the SOS registration message through activeMQ
				try {
					this.publisher.publishString(xml); //SOS message
				} catch (Exception e) {
					log.warn("{MQ ERROR} "+e.getMessage());
				}
			}
		}
	}
	
	public void sendDavisObservationToSOS (String record, LogfileManipulator currentFile) {
		
		long Timestamp,Interval;
		
		SOSInsertObservationDavis insDAVIS = new SOSInsertObservationDavis(this.insertObsDavisTemplate);
		StringTokenizer st = new StringTokenizer(record, " ");

		insDAVIS.setUniqueID(st.nextToken()); //The first token of the record(from a measurement logfile) is the Sensor ID
		
		Interval = Integer.parseInt(st.nextToken()); //The second token is the time interval
		Timestamp = this.startTime.getTime() + Interval;
		Date now = new Date(Timestamp);
		insDAVIS.setTimestamp(now);
		
		//Third token is Longitude, Fourth token in Latitude
		insDAVIS.setPosition(Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()));
		insDAVIS.setOwner(currentFile.OWNER);
		
		insDAVIS.setTemperatureValue(Double.parseDouble(st.nextToken()));
		insDAVIS.setHumidityValue(Double.parseDouble(st.nextToken()));
		insDAVIS.setWindSpeedValue(Double.parseDouble(st.nextToken()));
		insDAVIS.setWindDirectionValue(Double.parseDouble(st.nextToken()));
		insDAVIS.setRainRateValue(Double.parseDouble(st.nextToken()));

		String xml = insDAVIS.createXML();

		if(xml != null)
		{
			if(Simulator.activeMQ == false) {
				try {
					log.info(this.sosClient.postXML(xml));

					//In order to know who is executing what!
					//System.out.println("Thread ["+Thread.currentThread().getName()+"]: ");

					//System.out.println("[DEBUG] Send record from file "+currentFile.FILENAME+" with interval from start time = "+Interval+" and timestamp = "+now.toString());
					now = new Date();
					log.info("{DEBUG} Send record from file \"{}\" with File_Interval = {} and Excecution_Interval = {}",currentFile.FILENAME,Interval,(now.getTime()-this.startTime.getTime()));
				} catch (SOSException e) {
					//In order to know who is executing what!
					//System.out.println("Thread ["+Thread.currentThread().getName()+"]: ");
					log.warn("{SOS ERROR} {}",e.getMessage());
				} 
			}
			else { //Send the SOS observation message through activeMQ
				try {
					this.publisher.publishString(xml); //SOS message
				} catch (Exception e) {
					log.warn("{MQ ERROR} "+e.getMessage());
				}
			}
		}
	}
	
	public void sendWeatherObservationToSOS (String record, LogfileManipulator currentFile) {
		
		long Timestamp,Interval;
		
		SOSInsertObservationWeatherStation insWeather = new SOSInsertObservationWeatherStation(this.insertObsWeatherTemplate);
		
		StringTokenizer st = new StringTokenizer(record, " ");

		insWeather.setUniqueID(st.nextToken()); //The first token of the record(from a measurement logfile) is the Sensor ID
		
		Interval = Integer.parseInt(st.nextToken()); //The second token is the time interval
		Timestamp = this.startTime.getTime() + Interval;
		Date now = new Date(Timestamp);
		insWeather.setTimestamp(now);
		
		//Third token is Longitude, Fourth token in Latitude
		insWeather.setPosition(Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()));
		insWeather.setOwner(currentFile.OWNER);
		
		insWeather.setHumidityPhenomenon(currentFile.PHENOMENON[0]);
		insWeather.setPrecipitationPhenomenon(currentFile.PHENOMENON[1]);
		insWeather.setWindDirectionPhenomenon(currentFile.PHENOMENON[2]);
		insWeather.setTemperaturePhenomenon(currentFile.PHENOMENON[3]);
		insWeather.setPressurePhenomenon(currentFile.PHENOMENON[4]);
		insWeather.setWindSpeedPhenomenon(currentFile.PHENOMENON[5]);
		
		insWeather.setHumidityValue(Double.parseDouble(st.nextToken()));
		insWeather.setPrecipitationValue(Double.parseDouble(st.nextToken()));
		insWeather.setWindDirectionValue(Double.parseDouble(st.nextToken()));
		insWeather.setTemperatureValue(Double.parseDouble(st.nextToken()));
		insWeather.setPressureValue(Double.parseDouble(st.nextToken()));
		insWeather.setWindSpeedValue(Double.parseDouble(st.nextToken()));
		
		String xml = insWeather.createXML();

		if(xml != null)
		{
			if(Simulator.activeMQ == false) {
				try {
					log.info(this.sosClient.postXML(xml));
	
					//In order to know who is executing what!
			        //System.out.println("Thread ["+Thread.currentThread().getName()+"]: ");
			        
					//System.out.println("[DEBUG] Send record from file "+currentFile.FILENAME+" with interval from start time = "+Interval+" and timestamp = "+now.toString());
					now = new Date();
					log.info("{DEBUG} Send record from file \"{}\" with File_Interval = {} and Excecution_Interval = {}",currentFile.FILENAME,Interval,(now.getTime()-this.startTime.getTime()));
				} catch (SOSException e) {
					//In order to know who is executing what!
			        //System.out.println("Thread ["+Thread.currentThread().getName()+"]: ");
					log.warn("{SOS ERROR} {}",e.getMessage());
				} 
			}
			else { //Send the SOS observation message through activeMQ
				try {
					this.publisher.publishString(xml); //SOS message
				} catch (Exception e) {
					log.warn("{MQ ERROR} "+e.getMessage());
				}
			}
		}
	}
	
	public void sendFlexitObservationToSOS (String record, LogfileManipulator currentFile) {
		
		long Timestamp,Interval;
		
		SOSInsertObservation4 insFlexit = new SOSInsertObservation4(this.insertObsFlexitTemplate);
		
		StringTokenizer st = new StringTokenizer(record, " ");

		insFlexit.setUniqueID(st.nextToken()); //The first token of the record(from a measurement logfile) is the Sensor ID
		
		Interval = Integer.parseInt(st.nextToken()); //The second token is the time interval
		Timestamp = this.startTime.getTime() + Interval;
		Date now = new Date(Timestamp);
		insFlexit.setTimestamp(now);
		
		//Third token is Longitude, Fourth token in Latitude
		insFlexit.setPosition(Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()));
		insFlexit.setOwner(currentFile.OWNER);
		
		insFlexit.setPhenomenon1(currentFile.PHENOMENON[0]);
		insFlexit.setPhenomenon2(currentFile.PHENOMENON[1]);
		insFlexit.setPhenomenon3(currentFile.PHENOMENON[2]);
		insFlexit.setPhenomenon4(currentFile.PHENOMENON[3]);
		
		insFlexit.setPhenomenon1Value(Double.parseDouble(st.nextToken()));
		insFlexit.setPhenomenon2Value(Double.parseDouble(st.nextToken()));
		insFlexit.setPhenomenon3Value(Double.parseDouble(st.nextToken()));
		insFlexit.setPhenomenon4Value(Double.parseDouble(st.nextToken()));
		
		String xml = insFlexit.createXML();
		//System.out.println(xml);
		
		if(xml != null)
		{
			if(Simulator.activeMQ == false) {
				try {
					log.info(this.sosClient.postXML(xml));
	
					//In order to know who is executing what!
			        //System.out.println("Thread ["+Thread.currentThread().getName()+"]: ");
			        
					//System.out.println("[DEBUG] Send record from file "+currentFile.FILENAME+" with interval from start time = "+Interval+" and timestamp = "+now.toString());
					now = new Date();
					log.info("{DEBUG} Send record from file \"{}\" with File_Interval = {} and Excecution_Interval = {}",currentFile.FILENAME,Interval,(now.getTime()-this.startTime.getTime()));
				} catch (SOSException e) {
					//In order to know who is executing what!
			        //System.out.println("Thread ["+Thread.currentThread().getName()+"]: ");
					log.warn("{SOS ERROR} {}",e.getMessage());
				} 
			}
			else { //Send the SOS observation message through activeMQ
				try {
					this.publisher.publishString(xml); //SOS message
				} catch (Exception e) {
					log.warn("{MQ ERROR} "+e.getMessage());
				}
			}
		}
	}
	
	public void sendSunSPOTObservationToSOS (String record, LogfileManipulator currentFile) {
		
		long Timestamp,Interval;
		
		SOSInsertObservationSunSPOT insSunSPOT = new SOSInsertObservationSunSPOT(this.insertObsSunSPOTTemplate);
		
		StringTokenizer st = new StringTokenizer(record, " ");

		insSunSPOT.setUniqueID(st.nextToken()); //The first token of the record(from a measurement logfile) is the Sensor ID
		
		Interval = Integer.parseInt(st.nextToken()); //The second token is the time interval
		Timestamp = this.startTime.getTime() + Interval;
		Date now = new Date(Timestamp);
		insSunSPOT.setTimestamp(now);
		
		//Third token is Longitude, Fourth token in Latitude
		insSunSPOT.setPosition(Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()));
		insSunSPOT.setOwner(currentFile.OWNER);
		
		insSunSPOT.setTemperaturePhenomenon(currentFile.PHENOMENON[0]);
		insSunSPOT.setLightPhenomenon(currentFile.PHENOMENON[1]);
		
		insSunSPOT.setTemperatureValue(Double.parseDouble(st.nextToken()));
		insSunSPOT.setLightValue(Double.parseDouble(st.nextToken()));
		
		String xml = insSunSPOT.createXML();
		//System.out.println(xml);
		
		if(xml != null)
		{
			if(Simulator.activeMQ == false) {
				try {
					log.info(this.sosClient.postXML(xml));
	
					//In order to know who is executing what!
			        //System.out.println("Thread ["+Thread.currentThread().getName()+"]: ");
			        
					//System.out.println("[DEBUG] Send record from file "+currentFile.FILENAME+" with interval from start time = "+Interval+" and timestamp = "+now.toString());
					now = new Date();
					log.info("{DEBUG} Send record from file \"{}\" with File_Interval = {} and Excecution_Interval = {}",currentFile.FILENAME,Interval,(now.getTime()-this.startTime.getTime()));
				} catch (SOSException e) {
					//In order to know who is executing what!
			        //System.out.println("Thread ["+Thread.currentThread().getName()+"]: ");
					log.warn("{SOS ERROR} {}",e.getMessage());
				} 
			}
			else { //Send the SOS observation message through activeMQ
				try {
					this.publisher.publishString(xml); //SOS message
				} catch (Exception e) {
					log.warn("{MQ ERROR} "+e.getMessage());
				}
			}
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(this.fileName.FILE_TYPE.equalsIgnoreCase("measurement")) {
			
			if(this.fileName.MOBILE.equalsIgnoreCase("true")) {
				this.sendMeasurementMobileToSOS(this.recToSend,this.fileName);
			}
			else {
				if(this.fileName.PHENOMENON[0].equalsIgnoreCase("position")) {
					this.sendMeasurementGPSToSOS(this.recToSend,this.fileName);
				} else {
					if(Simulator.SOSv350)
						this.sendMeasurementToSOS_v350(this.recToSend,this.fileName);
					else
						this.sendMeasurementToSOS(this.recToSend,this.fileName);
				}
			}
		} else {
			//System.out.println("Filename: "+this.fileName.FILENAME + " Phenomena: "+this.fileName.PHENOMENON.length);
			switch (this.fileName.PHENOMENON.length) {
				case 6: 
					this.sendWeatherObservationToSOS(this.recToSend,this.fileName);
					break;
				case 5: 
					this.sendDavisObservationToSOS(this.recToSend,this.fileName);
					break;
				case 4: 
					this.sendFlexitObservationToSOS(this.recToSend,this.fileName);
					break;
				case 2: 
					this.sendSunSPOTObservationToSOS(this.recToSend,this.fileName);
					break;
			}
		}
		
		//this.sosClient.closeSOSHttpAdapter();
		this.sosClient = null; //For the garbage collector...
	}
}
