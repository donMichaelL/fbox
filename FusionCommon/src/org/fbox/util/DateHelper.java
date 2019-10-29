package org.fbox.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateHelper {

	private static SimpleDateFormat dtFormatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS z'('Z')'");

	public static String getTimestampAsString(Date timestamp, TimeZone tz) {
		if (timestamp!=null) {
			dtFormatter.setTimeZone(tz);
			return dtFormatter.format(timestamp);
		} else
			return null;	
	}

	public static String getTimestampAsUTCString(Date timestamp) {
		return getTimestampAsString(timestamp, TimeZone.getTimeZone("UTC"));	
	}
}
