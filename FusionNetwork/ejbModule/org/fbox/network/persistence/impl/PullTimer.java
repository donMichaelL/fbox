package org.fbox.network.persistence.impl;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table (name="timer")
public class PullTimer implements Serializable {
	private String timerId;
	private Date timestamp;
	private String description;
	
	@Id
	@Column(name="timer_id")
	public String getTimerId() {
		return timerId;
	}
	
	public void setTimerId(String timerId) {
		this.timerId = timerId;
	}
	
	@Column(name="timestamp")
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	@Column(name="description")
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return "FBoxTimer [timerId=" + timerId + ", timestamp=" + timestamp
				+ ", description=" + description + "]";
	}
}
