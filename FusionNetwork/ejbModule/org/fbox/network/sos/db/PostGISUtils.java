package org.fbox.network.sos.db;

import org.fbox.network.sos.util.JTSUtils;
import org.postgis.PGgeometry;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class PostGISUtils {

	public static Geometry EwktToGeometry(String strInEwkt) {
		
		String[] tokens = strInEwkt.split(";");
		String[] sridTokens = tokens[0].split("=");
		
		int srid = Integer.parseInt(sridTokens[1]);
		
		Geometry geom = null;
		
		try {
			 geom = new WKTReader().read(tokens[1]);
			 geom.setSRID(srid);
		} catch(ParseException e) {
			e.printStackTrace();
		}
		
		return geom;
	}
	
	public static Geometry convertToGeometry(PGgeometry pgGeom) {
		
		String pgGeomStr = pgGeom.toString();
		
		String WKTGeometry = pgGeomStr.substring(pgGeomStr.indexOf(";") + 1);
		
		return JTSUtils.WKTToGeometry(WKTGeometry);
	}
	
	

}
