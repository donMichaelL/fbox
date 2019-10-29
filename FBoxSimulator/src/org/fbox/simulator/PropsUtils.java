package org.fbox.simulator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropsUtils {
	
	public Properties properties = new Properties();
	public String filename;
	 
	public void loadProperties(String filename){
		this.filename = filename;
		try {
			this.properties.load(new FileInputStream(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//load the file
	
	public void getParameter(){
		Simulator.POST_SERVER = this.properties.getProperty("simulator.postserver");
		Simulator.path = this.properties.getProperty("simulator.path");
		Simulator.usage = Integer.parseInt(this.properties.getProperty("simulator.usage"));
		Simulator.INSERT_DATE_FORMAT = this.properties.getProperty("simulator.dateformat");
		Simulator.poolSize = Integer.parseInt(this.properties.getProperty("simulator.poolsize"));
		Simulator.timeout = Integer.parseInt(this.properties.getProperty("simulator.timeout"));
		Simulator.resultFile = Boolean.parseBoolean(this.properties.getProperty("simulator.resultfile"));
		Simulator.registerMeasurementTemplate = this.properties.getProperty("simulator.registerMeasurementTemplate");
		Simulator.registerMeasurementGPSTemplate = this.properties.getProperty("simulator.registerMeasurementGPSTemplate");
		Simulator.registerObservationDavisTemplate = this.properties.getProperty("simulator.registerObservationDavisTemplate");
		Simulator.registerObservationWeatherTemplate = this.properties.getProperty("simulator.registerObservationWeatherTemplate");
		Simulator.registerObservationFlexitTemplate = this.properties.getProperty("simulator.registerObservationFlexitTemplate");
		Simulator.registerObservationSunSPOTTemplate = this.properties.getProperty("simulator.registerObservationSunSPOTTemplate");
		Simulator.insertMeasurementTemplate = this.properties.getProperty("simulator.insertMeasurementTemplate");
		Simulator.insertMeasurementGPSTemplate = this.properties.getProperty("simulator.insertMeasurementGPSTemplate");
		Simulator.insertObservationDavisTemplate = this.properties.getProperty("simulator.insertObservationDavisTemplate");
		Simulator.insertObservationFlexitTemplate = this.properties.getProperty("simulator.insertObservationFlexitTemplate");
		Simulator.insertObservationWeatherTemplate = this.properties.getProperty("simulator.insertObservationWeatherTemplate");
		Simulator.insertObservationSunSPOTTemplate = this.properties.getProperty("simulator.insertObservationSunSPOTTemplate");
		Simulator.activeMQ = Boolean.parseBoolean(this.properties.getProperty("simulator.activeMQ"));
		Simulator.SOSv350 = Boolean.parseBoolean(this.properties.getProperty("simulator.SOSv350"));
		Simulator.queueURL = this.properties.getProperty("mqueue.url");
		Simulator.subject = this.properties.getProperty("mqueue.dest");
		Simulator.queueUser = this.properties.getProperty("mqueue.username");
		Simulator.queuePass = this.properties.getProperty("mqueue.password");
	}//get the parameters

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PropsUtils propFile = new PropsUtils();
		propFile.loadProperties("simulator.properties");
		propFile.getParameter();
		
		Logger log = LoggerFactory.getLogger(PropsUtils.class);
		PropertyConfigurator.configure("config/log4j.properties");
		
		log.info("Server: "+Simulator.POST_SERVER);
		log.info("Logfiles' path: "+Simulator.path);
		log.info("Usage: "+Simulator.usage);
		log.info("Date Format: "+Simulator.INSERT_DATE_FORMAT);
		log.info("Threadpool Size: "+Simulator.poolSize);
		log.info("Timeout in sec: "+Simulator.timeout);
		log.info("Write Log to a File: "+String.valueOf(Simulator.resultFile));
		log.info("Use SOS v3.5.0: "+Simulator.SOSv350);
		log.info(Simulator.registerMeasurementTemplate);
		log.info(Simulator.registerMeasurementGPSTemplate);
		log.info(Simulator.registerObservationDavisTemplate);
		log.info(Simulator.registerObservationWeatherTemplate);
		log.info(Simulator.registerObservationFlexitTemplate);
		log.info(Simulator.registerObservationSunSPOTTemplate);
		log.info(Simulator.insertMeasurementTemplate);
		log.info(Simulator.insertMeasurementGPSTemplate);
		log.info(Simulator.insertObservationDavisTemplate);
		log.info(Simulator.insertObservationFlexitTemplate);
		log.info(Simulator.insertObservationWeatherTemplate);
		log.info(Simulator.insertObservationSunSPOTTemplate);
	}

}
