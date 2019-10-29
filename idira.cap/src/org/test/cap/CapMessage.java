package org.test.cap;

import com.incident.cap.Alert;
import com.incident.cap.Area;
import com.incident.cap.Info;

public class CapMessage {
	
	// Alert fields
	private String identifier;
	private String msgType;
	private String sender;
	private String scope;
	private String sent;
	private String status;
	private String source;
	private String addresses;
	private String references;
	
	// Info fields 
	private String category;
	private String certainty;
	private String description;
	private String event;
	private String headline;
	private String language;
	private String senderName;
	private String severity;
	private String urgency;
	
	// Area fields
	private String areaDesc;
	private String circle;

	public CapMessage () {
		
	}
	
	
	public CapMessage(String identifier, String msgType, String sender,
			String scope, String sent, String status, String source,
			String addresses, String references, String category, String certainty,
			String description, String event, String headline, String language,
			String senderName, String severity, String urgency,
			String areaDesc, String circle) {
	
		this.identifier = identifier;
		this.msgType = msgType;
		this.sender = sender;
		this.scope = scope;
		this.sent = sent;
		this.status = status;
		this.source = source;
		this.addresses = addresses;
		this.references = references;
		this.category = category;
		this.certainty = certainty;
		this.description = description;
		this.event = event;
		this.headline = headline;
		this.language = language;
		this.senderName = senderName;
		this.severity = severity;
		this.urgency = urgency;
		this.areaDesc = areaDesc;
		this.circle = circle;
	}

	public String createXML() {

		Alert alert = new Alert();
		alert.setIdentifier(this.identifier);
		alert.setMsgType(this.msgType);
		alert.setSender(this.sender);
		alert.setScope(this.scope);
		alert.setSent(this.sent);
		alert.setStatus(this.status);
		alert.setSource(this.source);
		alert.setAddresses(this.addresses);
		
		if (references != null)
			alert.setReferences(this.references);
		
		Info info = new Info();
		info.addCategory(this.category);
		info.setCertainty(this.certainty);
		info.setDescription(this.description);
		info.setEvent(this.event);
		info.setHeadline(this.headline);
		info.setLanguage(this.language);
		info.setSenderName(this.senderName);
		info.setSeverity(this.severity);
		info.setUrgency(this.urgency);
		
		Area area = new Area();
		area.setAreaDesc(this.areaDesc);
		area.addCircle(this.circle);
		
		info.addArea(area);
		alert.addInfo(info);
		
		return alert.toString().replace(CapConstants.URN_CAP_VERSION_1_1, CapConstants.URN_CAP_VERSION_1_2);
	}

	
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getSent() {
		return sent;
	}

	public void setSent(String sent) {
		this.sent = sent;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getAddresses() {
		return addresses;
	}

	public void setAddresses(String addresses) {
		this.addresses = addresses;
	}
	
	public String getReferences() {
		return references;
	}

	public void setReferences(String references) {
		this.references = references;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCertainty() {
		return certainty;
	}

	public void setCertainty(String certainty) {
		this.certainty = certainty;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getHeadline() {
		return headline;
	}


	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public String getUrgency() {
		return urgency;
	}

	public void setUrgency(String urgency) {
		this.urgency = urgency;
	}

	public String getAreaDesc() {
		return areaDesc;
	}

	public void setAreaDesc(String areaDesc) {
		this.areaDesc = areaDesc;
	}

	public String getCircle() {
		return circle;
	}

	public void setCircle(String circle) {
		this.circle = circle;
	}
	
}
