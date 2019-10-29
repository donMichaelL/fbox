package org.fbox.common.xml.data;

public class SourceDAO extends GenericDAO {

	long timeLimit;
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
	
	public Long getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(Long timeLimit) {
		this.timeLimit = timeLimit;
	}
	
	@Override
	public String toString() {
		return "(id="+this.id +", timeLimit="+this.timeLimit+")";
	}	
}
