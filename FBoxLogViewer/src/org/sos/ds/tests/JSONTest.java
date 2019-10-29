package org.sos.ds.tests;

import java.io.IOException;

import org.sos.ds.model.LastObservation;
import org.sos.ds.ui.table.model.DataTablesLastObservationModel;
import org.sos.ds.ui.table.model.DataTablesLastPositionModel;
import org.sos.ds.unsorted.LastPosition;


import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONTest {

	
	public static void main(String[] args) {
	
		LastPosition lpDao1 = new LastPosition();
		lpDao1.setProcedureID("urn:ogc:IDIRA:GaugingStation-24");
		lpDao1.setTimeStamp("2012-09-16 12:42:14+03");
		lpDao1.setLatitude("14.93247932");
		lpDao1.setLongtitude("43.45213981");
		
		LastPosition lpDao2 = new LastPosition();
		lpDao2.setProcedureID("urn:ogc:IDIRA:GaugingStation-20");
		lpDao2.setTimeStamp("2012-09-16 13:25:14+03");
		lpDao2.setLatitude("13.87934");
		lpDao2.setLongtitude("44.9324209");
		
		if(lpDao1.getProcedureID().toLowerCase().contains("i"))
			System.out.println("YES!");
		else
			System.out.println("NO!");
		/*
		ObservationDAO obsDao1 = new ObservationDAO();
		obsDao1.setTimeStamp("2012-09-16 12:42:14+03");
		obsDao1.setSensorId("urn:ogc:IDIRA:GaugingStation-24");
		obsDao1.setIsMobile("N");
		obsDao1.setLatitude("14.93247932");
		obsDao1.setLongtitude("43.45213981");
		obsDao1.setValue("178");
		
		ObservationDAO obsDao2 = new ObservationDAO();
		obsDao2.setTimeStamp("2012-09-16 13:25:14+03");
		obsDao2.setSensorId("urn:ogc:IDIRA:GaugingStation-20");
		obsDao2.setIsMobile("Y");
		obsDao2.setLatitude("13.87934");
		obsDao2.setLongtitude("44.9324209");
		obsDao2.setValue("100");
	
		ArrayList<ObservationDAO> obsDaoList = new ArrayList<ObservationDAO>();
		obsDaoList.add(obsDao1);
		obsDaoList.add(obsDao2);
		
		DataTablesObservationModel dtObsModel = new DataTablesObservationModel();
		dtObsModel.getObsDaoList().add(obsDao1);
		dtObsModel.getObsDaoList().add(obsDao2);
		*/
		
		DataTablesLastPositionModel dtPosModel = new DataTablesLastPositionModel();
		dtPosModel.setsEcho(1);
		dtPosModel.setiTotalRecords(57);
		dtPosModel.setiTotalDisplayRecords(57);
		dtPosModel.getdata().add(lpDao1);
		dtPosModel.getdata().add(lpDao2);
		
		System.out.println(dtPosModel.getdata().toArray(new LastPosition[dtPosModel.getdata().size()]));
		
		
		LastObservation lObs1 = new LastObservation();
		lObs1.setTimeStamp("2012-09-16 12:42:14+03");
		lObs1.setProcedureId("urn:ogc:IDIRA:GaugingStation-24");
		lObs1.setPhenomenonId("urn:pgc:temperature");
		lObs1.setValue("10");
		
		DataTablesLastObservationModel dtObsModel = new DataTablesLastObservationModel();
		dtObsModel.setsEcho(1);
		dtObsModel.setiTotalRecords(23);
		dtObsModel.setiTotalDisplayRecords(23);
		dtObsModel.getData().add(lObs1);
		
		
		// LastPositionDAO[] lpArray;
		
		// 1. Convert Java object to JSON format
		ObjectMapper mapper = new ObjectMapper(); 
		try {
			// mapper.writeValue(new File("observations.json"), obsDao);
			// System.out.println(mapper.writeValueAsString(obsDao1));
			// System.out.println(mapper.writeValueAsString(obsDao2));
			
			//System.out.println(mapper.writeValueAsString(obsDaoList));
			System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dtObsModel));
			
			
			
			// 2. Convert JSON to Java object
			// ObservationDAO obsDao2 = mapper.readValue(new File("observations.json"), ObservationDAO.class);
			// System.out.println(obsDao2.toString());
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
