package org.fbox.simulator;

import java.io.File;
import java.io.FileFilter;
import java.util.Date;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.log4j.PropertyConfigurator;
import org.fbox.xmlParser.XMLParser;
import org.sos.adapter.SimpleSOSHttpAdapter;
import org.sos.exception.SOSException;
import org.sos.message.SOSRegisterMeasurement;
import org.sos.message.SOSRegisterMeasurementGPS;
import org.sos.message.SOSRegisterMobileSinglePhenomenon;
import org.sos.message.SOSRegisterObservation4;
import org.sos.message.SOSRegisterObservationDavis;
import org.sos.message.SOSRegisterObservationSunSPOT;
import org.sos.message.SOSRegisterObservationWeatherStation;
import org.sos.message.SOSRegisterObservationQuax2;
import org.sos.message.SOSRegisterObservationQuax4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Simulator extends Thread implements Runnable {
	
	public static String POST_SERVER;
	public static String INSERT_DATE_FORMAT;
	public static int poolSize;
	public static int timeout;
	public static String path;
	public static int usage;
	public static boolean resultFile;
	public static boolean SOSv350;
	public static String registerMeasurementTemplate;
	public static String registerMeasurementGPSTemplate;
	public static String registerObservationDavisTemplate;
	public static String registerObservationWeatherTemplate;
	public static String registerObservationFlexitTemplate;
	public static String registerObservationSunSPOTTemplate;
	public static String insertMeasurementTemplate;
	public static String insertMeasurementGPSTemplate;
	public static String insertObservationDavisTemplate;
	public static String insertObservationWeatherTemplate;
	public static String insertObservationFlexitTemplate;
	public static String insertObservationSunSPOTTemplate;
	
	private static SchemeRegistry schemeRegistry;
	private static PoolingClientConnectionManager cm;
	private static HttpClient httpClient;
	private static SimpleSOSHttpAdapter sosClient;
	
	private static LinkedList<LogfileManipulator> fileList;
	private static int totalFiles;
	private static int TimeIntervals []; //Keep the interval of current record from every file
	private static Date START_TIME;
	
	public static boolean activeMQ;
	public static String queueURL;
	public static String subject;
	public static String queueUser;
	public static String queuePass;
	
	private static MQpublisher sos;
	
	private static XMLParser parser;
	
	private final Logger log;
	
	public Simulator() {
		
		log = LoggerFactory.getLogger(Simulator.class);
		PropertyConfigurator.configure("config/log4j.properties");
		
		//Get infos from properties file
		PropsUtils propFile = new PropsUtils();
		propFile.loadProperties("simulator.properties");
		propFile.getParameter();
		
		//Inform for the usage mode of simulator
		switch (Simulator.usage)
		{
			case 0:
				log.info("Simulator Mode ---> \"Only register available sensors\"");
				break;
			case 1:
				log.info("Simulator Mode ---> \"Only insert observation from the available logfiles\"");
				break;
			case 2:
				log.info("Simulator Mode ---> \"Register available sensors and start inserting observations for them\"");
				break;
		}
		
		//Retrieve only the ".txt" files from the "traces" directory
		//Create a FileFilter and override its accept-method
	   FileFilter fileFilter = new FileFilter() {

	        public boolean accept(File file) {
	            //if the file extension is .txt return true, else false
	            if (file.getName().endsWith(".txt") || file.getName().endsWith(".TXT")) {
	                return true;
	            }
	            return false;
	        }
	    };
	    
		File folder = new File(Simulator.path);
		File[] listOfFiles = folder.listFiles(fileFilter);
		
		Simulator.totalFiles = listOfFiles.length;
		Simulator.TimeIntervals = new int[Simulator.totalFiles];
		
		Simulator.schemeRegistry = new SchemeRegistry();
		Simulator.schemeRegistry.register(new Scheme("http", 10, PlainSocketFactory.getSocketFactory()));
		Simulator.cm = new PoolingClientConnectionManager(Simulator.schemeRegistry);
		
		Simulator.httpClient = new DefaultHttpClient(Simulator.cm);
		Simulator.sosClient = new SimpleSOSHttpAdapter(Simulator.httpClient, Simulator.POST_SERVER);
		
		//Initialize ActiveMQ publisher object
		try {
			if(Simulator.activeMQ)
				Simulator.sos = new MQpublisher(Simulator.queueUser, Simulator.queuePass, Simulator.queueURL, Simulator.subject);
			else
				Simulator.sos = null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("{MQ INIT ERROR} "+e.getMessage());
			System.exit(1);
		}
		
		//Initialize the array of files and register their sensors in SOS
		Simulator.fileList = new LinkedList<LogfileManipulator>();
		for (int i = 0; i < Simulator.totalFiles; i++) 
		{
			Simulator.fileList.add(new LogfileManipulator(Simulator.path+"/"+listOfFiles[i].getName()));
			
			if(Simulator.usage != 1) //If we are not in the case where simulator only posts observations
				this.registerSensors(Simulator.fileList.get(i)); //Registration of sensors from this logfile	
		}
		
		//Clear client without timeout policy
		Simulator.sosClient = null;
		
		//Timeout policy for the client that will be used for insertion of observations in SOS
		Simulator.httpClient.getParams().setParameter("http.socket.timeout", new Integer(Simulator.timeout*1000));
		Simulator.httpClient.getParams().setParameter("http.connection.timeout", new Integer(Simulator.timeout*1000));
		
		//Client with timeout policy
		Simulator.sosClient = new SimpleSOSHttpAdapter(Simulator.httpClient, Simulator.POST_SERVER);
		
		Simulator.START_TIME = new Date();
	}
	
	/**
	 * Method which is used to initialize the arrays that are going to be used
	 * by the simulator for the synchronization task of posting in SOS
	 */
	public String [] initArrays() {
		
		String [] startRecords = new String[Simulator.totalFiles];
		
		for(int i=0; i<Simulator.totalFiles ; i++)
		{
			startRecords[i] = Simulator.fileList.get(i).getRecord();
			Simulator.TimeIntervals[i] = this.getTimeInterval(startRecords[i]);
			
			if( Simulator.TimeIntervals[i] < 0 ) //File without measurements (close it!)
				Simulator.fileList.get(i).deActivate();
		}
		
		return startRecords;
	}
	
	/**
	 * Method which is used to define the time that the record
	 * should be sent
	 */
	public int getTimeInterval(String record) {
		
		if((record != null) && (!record.equalsIgnoreCase("EOF"))) { 
			StringTokenizer st = new StringTokenizer(record, " ");
			st.nextToken(); //To pass the Sensor ID token
			String TimeInterval = st.nextToken();
			
			return Integer.parseInt(TimeInterval);
		}
		else //For files without measurement records
			return -1;
	}
	
	/**
	 * Method which defines the file that should send the 
	 * record to SOS by taking account of the smallest interval
	 */
	public int fileToUse () {
		int filePosition = -1;
		int minInterval = -1;
		boolean initialized = false;
		
		for(int i=0 ; i<Simulator.totalFiles ; i++)
			if(Simulator.fileList.get(i).ACTIVE)
			{
				if(initialized == false)
				{
					initialized = true;
					minInterval = Simulator.TimeIntervals[i];
					filePosition = i;
					break;
				}
			}
		
		for(int i=0 ; i<Simulator.totalFiles ; i++) //Check only the "ACTIVE" files
		{
			if( (initialized == true) && (Simulator.fileList.get(i).ACTIVE == true) )
				if(Simulator.TimeIntervals[i] <= minInterval) 
				{
					minInterval = Simulator.TimeIntervals[i];
					filePosition = i;
				}
		}
		
		return filePosition;
	}
	
	/**
	 * Define the number of active files(files that are still posting records to SOS)
	 */
	public int activeFiles() {
		int counter=0,i;
		
		for(i=0; i < Simulator.totalFiles ; i++)
			if(Simulator.fileList.get(i).ACTIVE == true)
				counter++;
		
		if(counter == 0)
			log.info("{DEBUG} There are no ACTIVE files!!!");
		
		return counter;
	}
	
	public void registerSensor_Measurement(LogfileManipulator currentFile, Sensor currentSensor) {

		//SOSRegisterMeasurement regMeas = new SOSRegisterMeasurement(Simulator.registerMeasurementTemplate);
		SOSRegisterMeasurement regMeas = new SOSRegisterMeasurement();

		regMeas.setStatus(currentSensor.Status);
		regMeas.setOfferingID(currentSensor.SensorID);
		regMeas.setUniqueID(currentSensor.SensorID);
		regMeas.setOwner(currentFile.OWNER);
		regMeas.setPhenomenon(currentFile.PHENOMENON[0]);
		regMeas.setUnitOfMeasurement(currentFile.PHENOMENON_UNIT[0]);
		regMeas.setPosition(currentSensor.Longitude, currentSensor.Latitude, 0.0);

		String sosMessage = regMeas.createXML();
		
		//Send it via activeMQ
		
		//Simulator.parser = new XMLParser(sosMessage);
		//System.out.println(Simulator.parser.parseElement());
		
		if(sosMessage != null) //Successfully registered sensor to SOS
		{
			if(Simulator.activeMQ == false){
				try {
					log.info(Simulator.sosClient.postXML(sosMessage));
				} catch (SOSException e) {
					log.warn("{SOS ERROR} "+e.getMessage());
				}
			}
			else { //Send the SOS registration message through activeMQ
				try {
					Simulator.sos.publishString(sosMessage); //SOS message
				} catch (Exception e) {
					log.warn("{MQ ERROR} "+e.getMessage());
				}
			}
		}
	}
	
	public void registerSensor_Measurement_GPS(LogfileManipulator currentFile, Sensor currentSensor) {

		SOSRegisterMeasurementGPS regMeasGPS = new SOSRegisterMeasurementGPS(Simulator.registerMeasurementGPSTemplate);

		regMeasGPS.setStatus(currentSensor.Status);
		regMeasGPS.setOfferingID(currentSensor.SensorID);
		regMeasGPS.setUniqueID(currentSensor.SensorID);
		regMeasGPS.setOwner(currentFile.OWNER);
		regMeasGPS.setPosition(currentSensor.Longitude, currentSensor.Latitude, 0.0);

		String sosMessage = regMeasGPS.createXML();

		if(sosMessage != null) //Successfully registered sensor to SOS
		{
			if(Simulator.activeMQ == false) {
				try {
					log.info(Simulator.sosClient.postXML(sosMessage));
				} catch (SOSException e) {
					log.warn("{SOS ERROR} "+e.getMessage());
				}
			}
			else { //Send the SOS registration message through activeMQ
				try {
					Simulator.sos.publishString(sosMessage); //SOS message
				} catch (Exception e) {
					log.warn("{MQ ERROR} "+e.getMessage());
				}
			}
		}
	}
	
	public void registerSensorMobile_Measurement (LogfileManipulator currentFile, Sensor currentSensor) {
		
		SOSRegisterMobileSinglePhenomenon regSimpleMobile = new SOSRegisterMobileSinglePhenomenon();
		
		regSimpleMobile.setOwner(currentFile.OWNER);
		regSimpleMobile.setOfferingID(currentSensor.SensorID);
		regSimpleMobile.setUniqueID(currentSensor.SensorID);
		regSimpleMobile.setPosition(currentSensor.Longitude, currentSensor.Latitude, 0.0);		
		regSimpleMobile.setPhenomenon1(currentFile.PHENOMENON[0]);
		regSimpleMobile.setUnitOfPhenomenon1(currentFile.PHENOMENON_UNIT[0]);
		
		String sosMessage = regSimpleMobile.createXML();
		
		if(sosMessage != null) //Successfully registered sensor to SOS
		{
			if(Simulator.activeMQ == false) {
				try {
					log.info(Simulator.sosClient.postXML(sosMessage));
				} catch (SOSException e) {
					log.warn("{SOS ERROR} "+e.getMessage());
				}
			}
			else { //Send the SOS registration message through activeMQ
				try {
					Simulator.sos.publishString(sosMessage); //SOS message
				} catch (Exception e) {
					log.warn("{MQ ERROR} "+e.getMessage());
				}
			}
		}
	}
	
	public void registerDavis_Observation(LogfileManipulator currentFile, Sensor currentSensor) {
		
		SOSRegisterObservationDavis regDAVIS = new SOSRegisterObservationDavis(Simulator.registerObservationDavisTemplate);
		
		regDAVIS.setStatus(currentSensor.Status);
		regDAVIS.setOfferingID(currentSensor.SensorID);
		regDAVIS.setUniqueID(currentSensor.SensorID);
		regDAVIS.setOwner(currentFile.OWNER);
		regDAVIS.setPosition(currentSensor.Longitude, currentSensor.Latitude, 0.0);
		
		String sosMessage = regDAVIS.createXML();
		
		if(sosMessage != null) //Successfully registered sensor to SOS
		{
			if(Simulator.activeMQ == false) {
				try {
					log.info(Simulator.sosClient.postXML(sosMessage));
				} catch (SOSException e) {
					log.warn("{SOS ERROR} "+e.getMessage());
				}
			}
			else { //Send the SOS registration message through activeMQ
				try {
					Simulator.sos.publishString(sosMessage); //SOS message
				} catch (Exception e) {
					log.warn("{MQ ERROR} "+e.getMessage());
				}
			}
		}
	}
	
	public void registerWeatherStation_Observation(LogfileManipulator currentFile, Sensor currentSensor) {
		
		SOSRegisterObservationWeatherStation regWeather = new SOSRegisterObservationWeatherStation(Simulator.registerObservationWeatherTemplate);
		
		regWeather.setStatus(currentSensor.Status);
		regWeather.setOfferingID(currentSensor.SensorID);
		regWeather.setUniqueID(currentSensor.SensorID);
		regWeather.setOwner(currentFile.OWNER);
		regWeather.setPosition(currentSensor.Longitude, currentSensor.Latitude, 0.0);
		
		regWeather.setHumidityPhenomenon(currentFile.PHENOMENON[0]);
		regWeather.setUnitOfHumidity(currentFile.PHENOMENON_UNIT[0]);
		regWeather.setPrecipitationPhenomenon(currentFile.PHENOMENON[1]);
		regWeather.setUnitOfPrecipitation(currentFile.PHENOMENON_UNIT[1]);
		regWeather.setWindDirectionPhenomenon(currentFile.PHENOMENON[2]);
		regWeather.setUnitOfWindDirection(currentFile.PHENOMENON_UNIT[2]);
		regWeather.setTemperaturePhenomenon(currentFile.PHENOMENON[3]);
		regWeather.setUnitOfTemperature(currentFile.PHENOMENON_UNIT[3]);
		regWeather.setPressurePhenomenon(currentFile.PHENOMENON[4]);
		regWeather.setUnitOfPressure(currentFile.PHENOMENON_UNIT[4]);
		regWeather.setWindSpeedPhenomenon(currentFile.PHENOMENON[5]);
		regWeather.setUnitOfWindSpeed(currentFile.PHENOMENON_UNIT[5]);
		
		String sosMessage = regWeather.createXML();
		/*String encoded = "";
		byte ptext[];
		try {
			ptext = sosMessage.getBytes("UTF-8");
			encoded = new String(ptext, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println(encoded);*/
		if(sosMessage != null) //Successfully registered sensor to SOS
		{
			if(Simulator.activeMQ == false) {
				try {
					log.info(Simulator.sosClient.postXML(sosMessage));
				} catch (SOSException e) {
					log.warn("{SOS ERROR} "+e.getMessage());
				}
			}
			else { //Send the SOS registration message through activeMQ
				try {
					Simulator.sos.publishString(sosMessage); //SOS message
				} catch (Exception e) {
					log.warn("{MQ ERROR} "+e.getMessage());
				}
			}
		}
	}
	
	public void registerFlexit_Observation(LogfileManipulator currentFile, Sensor currentSensor) {
		
		SOSRegisterObservation4 regObs4 = new SOSRegisterObservation4(Simulator.registerObservationFlexitTemplate);
		
		regObs4.setStatus(currentSensor.Status);
		regObs4.setOfferingID(currentSensor.SensorID);
		regObs4.setUniqueID(currentSensor.SensorID);
		regObs4.setOwner(currentFile.OWNER);
		regObs4.setPosition(currentSensor.Longitude, currentSensor.Latitude, 0.0);
		
		regObs4.setPhenomenon1(currentFile.PHENOMENON[0]);
		regObs4.setUnitOfPhenomenon1(currentFile.PHENOMENON_UNIT[0]);
		regObs4.setPhenomenon2(currentFile.PHENOMENON[1]);
		regObs4.setUnitOfPhenomenon2(currentFile.PHENOMENON_UNIT[1]);
		regObs4.setPhenomenon3(currentFile.PHENOMENON[2]);
		regObs4.setUnitOfPhenomenon3(currentFile.PHENOMENON_UNIT[2]);
		regObs4.setPhenomenon4(currentFile.PHENOMENON[3]);
		regObs4.setUnitOfPhenomenon4(currentFile.PHENOMENON_UNIT[3]);
		
		String sosMessage = regObs4.createXML();
		
		if(sosMessage != null) //Successfully registered sensor to SOS
		{
			if(Simulator.activeMQ == false) {
				try {
					log.info(Simulator.sosClient.postXML(sosMessage));
				} catch (SOSException e) {
					log.warn("{SOS ERROR} "+e.getMessage());
				}
			}
			else { //Send the SOS registration message through activeMQ
				try {
					Simulator.sos.publishString(sosMessage); //SOS message
				} catch (Exception e) {
					log.warn("{MQ ERROR} "+e.getMessage());
				}
			}
		}
	}
	
	public void registerSunSPOT_Observation(LogfileManipulator currentFile, Sensor currentSensor) {
		
		SOSRegisterObservationSunSPOT regObsSunSPOT = new SOSRegisterObservationSunSPOT(Simulator.registerObservationSunSPOTTemplate);
		
		regObsSunSPOT.setStatus(currentSensor.Status);
		regObsSunSPOT.setOfferingID(currentSensor.SensorID);
		regObsSunSPOT.setUniqueID(currentSensor.SensorID);
		regObsSunSPOT.setOwner(currentFile.OWNER);
		regObsSunSPOT.setPosition(currentSensor.Longitude, currentSensor.Latitude, 0.0);
		
		regObsSunSPOT.setTemperaturePhenomenon(currentFile.PHENOMENON[0]);
		regObsSunSPOT.setUnitOfTemperature(currentFile.PHENOMENON_UNIT[0]);
		regObsSunSPOT.setLightPhenomenon(currentFile.PHENOMENON[1]);
		regObsSunSPOT.setUnitOfLight(currentFile.PHENOMENON_UNIT[1]);
		
		String sosMessage = regObsSunSPOT.createXML();
		
		if(sosMessage != null) //Successfully registered sensor to SOS
		{
			if(Simulator.activeMQ == false) {
				try {
					log.info(Simulator.sosClient.postXML(sosMessage));
				} catch (SOSException e) {
					log.warn("{SOS ERROR} "+e.getMessage());
				}
			}
			else { //Send the SOS registration message through activeMQ
				try {
					Simulator.sos.publishString(sosMessage); //SOS message
				} catch (Exception e) {
					log.warn("{MQ ERROR} "+e.getMessage());
				}
			}
		}
	}
	
	public void registerQuax2_Observation(LogfileManipulator currentFile, Sensor currentSensor) {

		//SOSRegisterObservationSunSPOT regObsSunSPOT = new SOSRegisterObservationSunSPOT(Simulator.registerObservationSunSPOTTemplate);
		SOSRegisterObservationQuax2 regObsQuax2 = new SOSRegisterObservationQuax2();
		
		
		regObsQuax2.setStatus(currentSensor.Status);
		regObsQuax2.setOfferingID(currentSensor.SensorID);
		regObsQuax2.setUniqueID(currentSensor.SensorID);
		regObsQuax2.setOwner(currentFile.OWNER);
		regObsQuax2.setPosition(currentSensor.Longitude, currentSensor.Latitude, 0.0);
		
		regObsQuax2.setBatteryVoltagePhenomenon(currentFile.PHENOMENON[0]);
		regObsQuax2.setUnitOfBatteryVoltage(currentFile.PHENOMENON_UNIT[0]);
		regObsQuax2.set_CO_CO2_Phenomenon(currentFile.PHENOMENON[1]);
		regObsQuax2.setUnitOf_CO_CO2(currentFile.PHENOMENON_UNIT[1]);

		String sosMessage = regObsQuax2.createXML();

		if(sosMessage != null) //Successfully registered sensor to SOS
		{
			if(Simulator.activeMQ == false) {
				try {
					log.info(Simulator.sosClient.postXML(sosMessage));
				} catch (SOSException e) {
					log.warn("{SOS ERROR} "+e.getMessage());
				}
			}
			else { //Send the SOS registration message through activeMQ
				try {
					Simulator.sos.publishString(sosMessage); //SOS message
				} catch (Exception e) {
					log.warn("{MQ ERROR} "+e.getMessage());
				}
			}
		}
	}
	
	public void registerQuax4_Observation(LogfileManipulator currentFile, Sensor currentSensor) {

		//SOSRegisterObservationSunSPOT regObsSunSPOT = new SOSRegisterObservationSunSPOT(Simulator.registerObservationSunSPOTTemplate);
		SOSRegisterObservationQuax4 regObsQuax4 = new SOSRegisterObservationQuax4();
		
		
		regObsQuax4.setStatus(currentSensor.Status);
		regObsQuax4.setOfferingID(currentSensor.SensorID);
		regObsQuax4.setUniqueID(currentSensor.SensorID);
		regObsQuax4.setOwner(currentFile.OWNER);
		regObsQuax4.setPosition(currentSensor.Longitude, currentSensor.Latitude, 0.0);
		
		regObsQuax4.setBatteryVoltagePhenomenon(currentFile.PHENOMENON[0]);
		regObsQuax4.setUnitOfBatteryVoltage(currentFile.PHENOMENON_UNIT[0]);
		regObsQuax4.set_CO_CO2_Phenomenon(currentFile.PHENOMENON[1]);
		regObsQuax4.setUnitOf_CO_CO2(currentFile.PHENOMENON_UNIT[1]);
		regObsQuax4.setTemperaturePhenomenon(currentFile.PHENOMENON[2]);
		regObsQuax4.setUnitOfTemperature(currentFile.PHENOMENON_UNIT[2]);
		regObsQuax4.setHumidityPhenomenon(currentFile.PHENOMENON[3]);
		regObsQuax4.setUnitOfHumidity(currentFile.PHENOMENON_UNIT[3]);

		String sosMessage = regObsQuax4.createXML();

		if(sosMessage != null) //Successfully registered sensor to SOS
		{
			if(Simulator.activeMQ == false) {
				try {
					log.info(Simulator.sosClient.postXML(sosMessage));
				} catch (SOSException e) {
					log.warn("{SOS ERROR} "+e.getMessage());
				}
			}
			else { //Send the SOS registration message through activeMQ
				try {
					Simulator.sos.publishString(sosMessage); //SOS message
				} catch (Exception e) {
					log.warn("{MQ ERROR} "+e.getMessage());
				}
			}
		}
	}
	
	public void registerSensors(LogfileManipulator currentFile) {
		
		if(currentFile.FILE_TYPE.equalsIgnoreCase("measurement") && currentFile.MOBILE.equalsIgnoreCase("false"))
			for(int i=0 ; i<currentFile.SENSOR_NO ; i++)
			{
				log.info("---> Registering sensor \""+currentFile.SENSORS[i].SensorID+"\" in SOS...");
				
				//We have to check if we are in the special case of a GPS sensor (this can be defined from the PHENOMENON type)
				if(currentFile.PHENOMENON[0].equalsIgnoreCase("position")) {
					this.registerSensor_Measurement_GPS(currentFile,currentFile.SENSORS[i]);
				} else
					this.registerSensor_Measurement(currentFile,currentFile.SENSORS[i]);
			}
		
		if(currentFile.FILE_TYPE.equalsIgnoreCase("observation")) //&& currentFile.MOBILE.equalsIgnoreCase("false"))	
			for(int i=0 ; i<currentFile.SENSOR_NO ; i++)
			{
				log.info("---> Registering sensor \""+currentFile.SENSORS[i].SensorID+"\" in SOS...");
				
				switch (currentFile.PHENOMENON.length) { 
					case 6: 
						this.registerWeatherStation_Observation(currentFile, currentFile.SENSORS[i]);
						break;
					case 5: 
						this.registerDavis_Observation(currentFile,currentFile.SENSORS[i]);
						break;
					case 4: //Discriminate Flexit sensors to Quaxes with 4 phenomena
						if(currentFile.PHENOMENON[0].equalsIgnoreCase("BatteryVoltage")) {
							this.registerQuax4_Observation(currentFile,currentFile.SENSORS[i]);
						} else {
							this.registerFlexit_Observation(currentFile,currentFile.SENSORS[i]);
						}
						break;
					case 2:  //Discriminate Sun SPOTs to Quaxes with 2 phenomena
						if(currentFile.PHENOMENON[0].equalsIgnoreCase("BatteryVoltage")) {
							this.registerQuax2_Observation(currentFile,currentFile.SENSORS[i]);
						} else {
 							this.registerSunSPOT_Observation(currentFile,currentFile.SENSORS[i]);
						}
						break;
				}
			}
	
		//Two other methods should be implemented also, in order to cover the case of mobile sensors
		
		//Case of a mobile sensor that monitors only one phenomenon
		if(currentFile.FILE_TYPE.equalsIgnoreCase("measurement") && currentFile.MOBILE.equalsIgnoreCase("true")) {
			for(int i=0 ; i<currentFile.SENSOR_NO ; i++) {
				log.info("---> Registering mobile sensor \""+currentFile.SENSORS[i].SensorID+"\" in SOS...");
				this.registerSensorMobile_Measurement(currentFile, currentFile.SENSORS[i]);
			}
		}
	}
	
	public void run() {
		
		if(Simulator.usage != 0) //If simulator isn't in "only register available sensors" mode
		{
			String [] recordArray = new String[Simulator.totalFiles];
			int fileNo;
			int lastSent = 0;
			
			ExecutorService threadPool = Executors.newFixedThreadPool(Simulator.poolSize);
			
			//Get the first record from all files
			recordArray = this.initArrays().clone();
			
			//As long as there are active files to retrieve records
			while (this.activeFiles() != 0) 
			{
				fileNo = this.fileToUse();
				
				try {
					//Now that we have the file we can estimate the sleep time
					Thread.sleep(Simulator.TimeIntervals[fileNo] - lastSent);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
				
				//And after that we can send the record to SOS
				//Use thread into threadpool for sending the messages to SOS
				//1. Create a new task with the available data
				TaskPostObservationToSOS task = new TaskPostObservationToSOS(recordArray[fileNo], Simulator.POST_SERVER, Simulator.START_TIME, Simulator.fileList.get(fileNo),
																			  Simulator.INSERT_DATE_FORMAT, Simulator.insertMeasurementTemplate, Simulator.insertMeasurementGPSTemplate,  
																			  Simulator.insertObservationDavisTemplate, Simulator.insertObservationWeatherTemplate, 
																			  Simulator.insertObservationFlexitTemplate, Simulator.insertObservationSunSPOTTemplate, 
																			  Simulator.httpClient, Simulator.sos);
																			
				//2. Send this task to be executed from a thread into threadpool
				try {
					threadPool.execute(task);
				} catch (RejectedExecutionException e) {
					log.error("{THREADPOOL ERROR} Task couldn't be executed. "+e.getMessage());
				}
				//3. Clear this task
				task = null;
				
				lastSent = Simulator.TimeIntervals[fileNo];
				//System.out.println("[DEBUG] Send record from file "+this.fileList.get(fileNo).FILENAME+ " with interval from start time = "+lastSent);
				
				//Finally, we have to update some of the arrays
				//Bring a record from this file
				recordArray[fileNo] = Simulator.fileList.get(fileNo).getRecord();
				
				if(recordArray[fileNo].equalsIgnoreCase("EOF"))
				{
					log.info("{DEBUG} File "+Simulator.fileList.get(fileNo).FILENAME+" Returned EOF");
					Simulator.fileList.get(fileNo).deActivate();
					Simulator.TimeIntervals[fileNo] = -1;	
				}
				else
					Simulator.TimeIntervals[fileNo] = this.getTimeInterval(recordArray[fileNo]);
			}
			
			log.info("ThreadPool is shutdown: {}",threadPool.isShutdown());
			log.info("ThreadPool is shutdown: {}",threadPool.isTerminated());
			//Since the work is done, we must clear the threadPool
			this.shutdownAndAwaitTermination(threadPool);
			log.info("ThreadPool is shutdown: {}",threadPool.isShutdown());
			log.info("ThreadPool is shutdown: {}",threadPool.isTerminated());
			
			if(Simulator.activeMQ) {
				try {
					Simulator.sos.closeMQpublisher();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else {
			log.info("Registration process has been completed!");
			//System.out.println("Registration process has been completed!");
		}
			
		Simulator.httpClient.getConnectionManager().shutdown();
	}
	
	public void shutdownAndAwaitTermination(ExecutorService threadPool) {
		threadPool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) 
			{
				threadPool.shutdownNow(); // Cancel currently executing tasks
				
				// Wait a while for tasks to respond to being canceled
				if (!threadPool.awaitTermination(10, TimeUnit.SECONDS))
					log.error("Pool did not terminate");
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			threadPool.shutdownNow();
			
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}

	public static void main(String[] args) {
		
		Simulator simulator;
		
		simulator = new Simulator();
		simulator.start();
	}//main	
}
