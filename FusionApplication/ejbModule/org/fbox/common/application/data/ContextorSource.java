package org.fbox.common.application.data;

public class ContextorSource {
	
	protected String id;
	protected long timeLimit;
		
	public ContextorSource(String sourceId, long timeLimit) {
		this.id=sourceId;
		this.timeLimit=timeLimit;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public long getTimeLimit() {
		return timeLimit;
	}
	
	public void setTimeLimit(long timeLimit) {
		this.timeLimit = timeLimit;
	}
	
	@Override
	public String toString() {	
		return "(id:"+this.id +", timeLimit:"+this.timeLimit+")";
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean result=false;
		if (obj!=null && obj instanceof ContextorSource) {
			 result=this.id.equals(((ContextorSource)obj).getId());
		}		
		return result;
	}
}
