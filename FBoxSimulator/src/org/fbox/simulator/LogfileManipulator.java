package org.fbox.simulator;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogfileManipulator {

	public String FILENAME;
	public String FILE_TYPE;
	public String MOBILE;
	public String OWNER;
	public String [] PHENOMENON;
	public String [] PHENOMENON_UNIT;
	public int SENSOR_NO;
	public boolean ACTIVE;
	public Sensor [] SENSORS;
	
	public BufferedReader reader;
	
	private final Logger log;
	
	/**
	 * Constructor of the class LogfileManipulator
	 * Reads the metadata part from the "filename" file and initializes the variables
	 * and also the BufferedReader for this file
	 */
	public LogfileManipulator (String fileName) {
		
		log = LoggerFactory.getLogger(LogfileManipulator.class);
		PropertyConfigurator.configure("config/log4j.properties");
		
		try {
			FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fstream);
			reader = new BufferedReader(new InputStreamReader(in));
			this.ACTIVE = true;
			this.loadMetadata(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method which is used to define a file as closed. That means to
	 * close the fileReader when we have EOF from the getRecord method
	 * and change the value of ACTIVE field for this class
	 */
	public void deActivate() {
		try{
			this.reader.close();
			this.ACTIVE = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getRecord() {
		String strLine = null;
		
		try {
			strLine = this.reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if( strLine != null)
			return strLine;
		else
			return "EOF";
	}
	
	/**
	 * With this method we retrieve useful information
	 * about the file that the simulator has to handle
	 */
	public void loadMetadata(String fileName) {
		
		StringTokenizer name = new StringTokenizer(fileName, "/");
		name.nextToken();
		this.FILENAME = name.nextToken();
		
		try {
			String strLine;
			
			while ((strLine = this.reader.readLine()) != null && (strLine.contains("#")) )   {
				//Parse those lines and get the description of the file
				StringTokenizer st = new StringTokenizer(strLine, " =#");
				String key = st.nextToken();
					
				if(key.equalsIgnoreCase("type"))
					this.FILE_TYPE = st.nextToken();
				else
					if(key.equalsIgnoreCase("mobile"))
						this.MOBILE = st.nextToken();
					else
						if(key.equalsIgnoreCase("owner"))
							this.OWNER = st.nextToken();
						else
							if(key.equalsIgnoreCase("phenomenon"))
							{
								int phenomenon_no = 0;
								StringTokenizer observ = new StringTokenizer(strLine, " =#[,]");
								observ.nextToken(); //In order to pass the "phenomenon" token
								
								phenomenon_no = observ.countTokens();
								
								this.PHENOMENON = new String[phenomenon_no];
								
								for(int i=0 ; i<phenomenon_no ; i++)
									this.PHENOMENON[i] = observ.nextToken();
							}
							else
								if(key.equalsIgnoreCase("phenomenon_unit"))
								{
									int phenomenon_no = 0;
									StringTokenizer observ = new StringTokenizer(strLine, " =#[,]");
									observ.nextToken(); //In order to pass the "phenomenon_unit" token
								
									phenomenon_no = observ.countTokens();
									
									this.PHENOMENON_UNIT = new String[phenomenon_no];
									
									for(int i=0 ; i<phenomenon_no ; i++)
										this.PHENOMENON_UNIT[i] = observ.nextToken();
								}
								else
									if(key.equalsIgnoreCase("sensor_no"))
									{
										this.SENSOR_NO = Integer.parseInt(st.nextToken());

										this.SENSORS = new Sensor[this.SENSOR_NO];
										strLine = this.reader.readLine();
										strLine = this.reader.readLine();
										for(int i=0 ; i < this.SENSOR_NO ; i++)
											this.SENSORS[i] = new Sensor(strLine = this.reader.readLine());
									}
							
				st = null;
			}
			strLine = this.reader.readLine();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void printInfos () {
		log.info("FILENAME: "+this.FILENAME);
		log.info("FILE_TYPE: "+this.FILE_TYPE);
		log.info("MOBILE: "+this.MOBILE);
		log.info("OWNER: "+this.OWNER);
		
		if(this.FILE_TYPE.equalsIgnoreCase("measurement"))
			log.info("PHENOMENON: "+this.PHENOMENON[0]+" , PHENOMENON_UNIT: "+this.PHENOMENON_UNIT[0]);
		else
			for(int i=0 ; i<this.PHENOMENON.length ; i++)
				log.info("PHENOMENON["+i+"]: "+this.PHENOMENON[i]+" , PHENOMENON_UNIT["+i+"]: "+this.PHENOMENON_UNIT[i]);
	
		//Now print the coordinates of each sensor
		for(int j=0 ; j<this.SENSOR_NO ; j++)
			log.info("Sensor with id: "+this.SENSORS[j].SensorID+" has longitude="+this.SENSORS[j].Longitude+" , latitude="+this.SENSORS[j].Latitude+" , status="+this.SENSORS[j].Status);
	}
	
	public static void main(String[] args) {
		
		//Retrieve the files from the "logfiles" directory
		File folder = new File("logfiles");
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			LogfileManipulator logFile = new LogfileManipulator("logfiles/"+listOfFiles[i].getName());
			logFile.printInfos();
		}
	}
}
