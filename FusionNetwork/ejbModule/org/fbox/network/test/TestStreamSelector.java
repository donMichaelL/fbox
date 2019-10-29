package org.fbox.network.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.fbox.common.network.data.DataStreamSelector;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

public class TestStreamSelector {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		

	}
	
	public static DataStreamSelector loadStreamSelector_1() {
		
		boolean PHENOMENON_CONSTRAINT = true;
		boolean SENSORIDS_CONSTRAINT = false;
		boolean SPATIAL_CONSTRAINT = false;
		
		String phenomenon = null;
		Set<String> sensorIdsSet = null;
		Geometry geom = null;
				
		// Phenomenon constraint
		if(PHENOMENON_CONSTRAINT)
			phenomenon = "urn:ogc:def:phenomenon:IDIRA:RSSI";
		
		// Set of sensor ids
		if(SENSORIDS_CONSTRAINT) {
			sensorIdsSet = new HashSet<String>();
			sensorIdsSet.add("urn:ogc:object:feature:Sensor:IDIRA:tablet1-gps");
			sensorIdsSet.add("urn:ogc:object:feature:Sensor:IDIRA:tablet1-RSSI");
		}
		
		if(SPATIAL_CONSTRAINT) {
			// Spatial constraint - Coordinates are defined in format (Longitude, Latitude)
			Map<String, Polygon> areaMap = new HashMap<String, Polygon>();
			GeometryFactory geomFac = new GeometryFactory();
			
			Coordinate[] coordinates = {
					new Coordinate(23.716736, 38.008607),
	        		new Coordinate(23.676224, 37.971267),
	        		new Coordinate(23.740425, 37.941219),
	        		new Coordinate(23.788147, 37.990481),
	        		new Coordinate(23.716736, 38.008607)};
			
			LinearRing linearRing = geomFac.createLinearRing(coordinates);
			Polygon polygon = geomFac.createPolygon(linearRing, null);
			areaMap.put("Athens", polygon);
			
			// System.out.println("---" + polygon.toString());
			
			geom = (Geometry) areaMap.get("Athens");
		}

		DataStreamSelector ss = new DataStreamSelector(phenomenon, sensorIdsSet, geom, false);
		
		return ss;
	}
	
	public static DataStreamSelector loadStreamSelector_2() {
		boolean PHENOMENON_CONSTRAINT = true;
		boolean SENSORIDS_CONSTRAINT = false;
		boolean SPATIAL_CONSTRAINT = false;
		
		String phenomenon = null;
		Set<String> sensorIdsSet = null;
		Geometry geom = null;
				
		// Phenomenon constraint
		if(PHENOMENON_CONSTRAINT)
			phenomenon = "urn:ogc:def:phenomenon:OGC:1.0.30:position";
		
		// Set of sensor ids
		if(SENSORIDS_CONSTRAINT) {
			sensorIdsSet = new HashSet<String>();
			sensorIdsSet.add("urn:ogc:object:feature:Sensor:IDIRA:tablet1-gps");
			sensorIdsSet.add("urn:ogc:object:feature:Sensor:IDIRA:tablet1-RSSI");
		}
		
		if(SPATIAL_CONSTRAINT) {
			// Spatial constraint - Coordinates are defined in format (Longitude, Latitude)
			Map<String, Polygon> areaMap = new HashMap<String, Polygon>();
			GeometryFactory geomFac = new GeometryFactory();
			
			Coordinate[] coordinates = {
					new Coordinate(23.716736, 38.008607),
	        		new Coordinate(23.676224, 37.971267),
	        		new Coordinate(23.740425, 37.941219),
	        		new Coordinate(23.788147, 37.990481),
	        		new Coordinate(23.716736, 38.008607)};
			
			LinearRing linearRing = geomFac.createLinearRing(coordinates);
			Polygon polygon = geomFac.createPolygon(linearRing, null);
			areaMap.put("Athens", polygon);
			
			// System.out.println("---" + polygon.toString());
			
			geom = (Geometry) areaMap.get("Athens");
		}

		DataStreamSelector ss = new DataStreamSelector(phenomenon, sensorIdsSet, geom, false);
		
		return ss;
	}

}
