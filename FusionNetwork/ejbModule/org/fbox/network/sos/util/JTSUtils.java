package org.fbox.network.sos.util;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

public class JTSUtils {
	
	public static Geometry WKTToGeometry(String srid) {
		Geometry geom = null;
		
		try {
			 geom = new WKTReader().read(srid);
		} catch(ParseException e) {
			e.printStackTrace();
		}
		
		return geom;
	}
	

	public static String GeometryToWKT(Geometry geom) {
		String strWKT = null;
		
		strWKT = new WKTWriter().write(geom);
		
		return strWKT;
	}
	
	// ATTENTION: (longitude, latitude) 
	public static String LatLonToPointWKT(double longitude, double latitude) {
		Coordinate pCoords = new Coordinate(longitude, latitude); 
		Geometry geom = new GeometryFactory().createPoint(pCoords);
		
		return GeometryToWKT(geom);
	}
	
	public static Geometry LatLonToPointGeometry(double longitude, double latitude) {
		Coordinate pCoords = new Coordinate(longitude, latitude); 
		Geometry geom = new GeometryFactory().createPoint(pCoords);
		
		return geom;
	}
	
	public static void main(String[] args) {
		
		//
		String s = "POINT(23.732529 37.969762)";
		Geometry g1 = JTSUtils.WKTToGeometry(s);
		System.out.println(g1.toString());
		
		//
		String wkt1 = JTSUtils.GeometryToWKT(g1);
		System.out.println(wkt1);
		
		double longitude = 23.732529;
		double latitude = 37.969762;
		
		// 
		String wkt2 = JTSUtils.LatLonToPointWKT(longitude, latitude);
		System.out.println(wkt2);
		
		//
		Geometry g2 = JTSUtils.LatLonToPointGeometry(longitude, latitude);
		System.out.println(g2.toString());
		
		
		String s00 = "SRID=4326;POINT(13.054161 47.81523)";
		Geometry g00 = JTSUtils.WKTToGeometry(s00);
		System.out.println(g00.toString());
		
	}

}
