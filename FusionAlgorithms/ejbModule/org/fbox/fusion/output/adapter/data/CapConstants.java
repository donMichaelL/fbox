package org.fbox.fusion.output.adapter.data;

public class CapConstants {
	public static final String DEFAULT_IDENTIFIER = "SFE-20120911-1-0"; //e.g. SFE-20120911-1-0
	public static final String DEFAULT_MSGTYPE = "Alert";	// [Alert, Update, Cancel]
	public static final String DEFAULT_SENDER = "SFE";
	public static final String DEFAULT_SCOPE = "Private";	// [Private]
	public static final String DEFAULT_SENT = "2012-10-25T16:35:10+00:00";
	public static final String DEFAULT_STATUS = "Test";
	public static final String DEFAULT_SOURCE = "Sensor Fusion Engine";
	public static final String DEFAULT_ADDREESSES = "NKUA";
	 
	public static final String DEFAULT_CATEGORY = "Safety"; 
	public static final String DEFAULT_CERTAINTY = "Likely";
	public static final String DEFAULT_DESCRIPTION = "CAP message from SFE";
	public static final String DEFAULT_EVENT = "Flooding Event"; // e.g. Flooding Event 
	public static final String DEFAULT_HEADLINE = "Flood event due to increase in water level"; // e.g. Flooding Event
	public static final String DEFAULT_LANGUAGE = "en-UK";
	public static final String DEFAULT_SENDERNAME = "SFE"; // 
	public static final String DEFAULT_SEVERITY = "Severe";
	public static final String DEFAULT_URGENCY = "Expected";
	
	public static final String DEFAULT_AREADESC = "Pirna"; // e.g. Dresden
	public static final String DEFAULT_CIRCLE = "51.02221,14.25354 20.505"; // Near Prima
	
	public static final String URN_CAP_VERSION_1_1 = "urn:oasis:names:tc:emergency:cap:1.1";
	public static final String URN_CAP_VERSION_1_2 = "urn:oasis:names:tc:emergency:cap:1.2";
	
	public static final String CAP_DATE_FORMAT_IDENTIFIER = "yyyyMMdd";
	public static final String CAP_DATE_FORMAT_SENT = "yyyy-MM-dd'T'HH:mm:ssXXX";
	
}
