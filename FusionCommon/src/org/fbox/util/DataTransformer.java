package org.fbox.util;

import org.fbox.common.data.IDataElement;

public class DataTransformer {
	
	public static String transformDataElementToXml(IDataElement data) {

		return "<DataElement>" +
				"<Id>" +data.getId() + "</Id>" +
				"<Sequence>" +data.getSequenceNumber()+ "</Sequence>" +				
				"<Value>"+ data.getValue() +"</Value>" +
				"<Time>" + DateHelper.getTimestampAsUTCString(data.getTimestamp()) +"</Time>" +
				"<Location>" + data.getLocation() +"</Location>" +
			"</DataElement>";
	}

}

