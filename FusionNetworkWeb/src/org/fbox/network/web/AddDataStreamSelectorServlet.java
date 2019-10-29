package org.fbox.network.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fbox.common.network.INetworkFusion;
import org.fbox.common.network.data.DataStreamSelector;
import org.fbox.network.impl.NetworkModule;

import com.vividsolutions.jts.geom.Geometry;

@WebServlet("/AddDataStreamSelector")
public class AddDataStreamSelectorServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	@EJB
	INetworkFusion networkModule;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.write("Called!");
		
		int dssNum = -1;
		DataStreamSelector dsSelector = null;
		String dataStreamSourceId = null;
		
		if(request.getParameter("dssNum") != null) {
			dssNum = Integer.parseInt(request.getParameter("dssNum"));	
		}
		
		switch(dssNum) {
			case 1:
				dsSelector = loadDataStreamSelector_1();
				dataStreamSourceId = "dss-" + dssNum;
				break;
			case 2:
				break;
			default:
				dsSelector = loadDataStreamSelector_Default();
				dataStreamSourceId = "dss-" + "default";
			}
		
		networkModule.registerDataStreamSource(dataStreamSourceId, dsSelector);
		
	}	
	
	private DataStreamSelector loadDataStreamSelector_Default () {
		// DataStreamSelector
		String phenomenon = "urn:ogc:def:phenomenon:OGC:1.0.30:waterlevel";
		
		String sid1 = "urn:ogc:object:feature:sensor:Idira:GAUGING_STATION_550620";
		String sid2 = "urn:ogc:object:feature:sensor:Idira:GAUGING_STATION_550710";
		
		Set<String> sensorIds = new HashSet<String>();
		sensorIds.add(sid1);
		sensorIds.add(sid2);
		
		Geometry spatial = null;
		boolean dynamic = false;
		
		DataStreamSelector dsSelector = 
				new DataStreamSelector(phenomenon, sensorIds, spatial, dynamic);  
		
		return dsSelector;
	}
	
	private DataStreamSelector loadDataStreamSelector_1 () {

		String phenomenon = "urn:ogc:def:phenomenon:OGC:1.0.30:humidity";
		
		String sid1 = "urn:ogc:object:feature:sensor:Wedaal:WEATHER_STATION_131";
		
		Set<String> sensorIds = new HashSet<String>();
		sensorIds.add(sid1);
		
		Geometry spatial = null;
		boolean dynamic = false;
		
		DataStreamSelector dsSelector = 
				new DataStreamSelector(phenomenon, sensorIds, spatial, dynamic);  
		
		return dsSelector;
	}
}
