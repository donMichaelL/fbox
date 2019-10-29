package org.fbox.simulator;

import java.util.StringTokenizer;

public class Sensor {

	public String SensorID;
	public double Longitude;
	public double Latitude;
	public boolean Status;
	
	public Sensor(String recordToTokenize) {
		
		StringTokenizer infos = new StringTokenizer(recordToTokenize, " =#");
		
		this.SensorID = infos.nextToken();
		this.Longitude = Double.parseDouble(infos.nextToken());
		this.Latitude = Double.parseDouble(infos.nextToken());
		this.Status = Boolean.parseBoolean(infos.nextToken());
	}
}
