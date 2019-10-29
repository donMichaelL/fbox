package org.fbox.common.application.data;

import java.util.Date;

import org.fbox.common.data.IDataElement;
import org.fbox.util.DataTransformer;
import org.fbox.util.DateHelper;

import com.vividsolutions.jts.geom.Geometry;

public class DataElement implements IDataElement {

	private static final long serialVersionUID = -3597438635127939500L;
	
	protected String id;
	protected long sequenceNumber;
	protected Comparable<?> value;
	protected Date timestamp;
	protected Geometry location;
	
	public DataElement(String id) {
		this.id=id;
		timestamp=new Date();
	}
		
	@Override
	public String getId() {
		return id;
	}

	@Override	
	public long getSequenceNumber() {
		return sequenceNumber;
	}	
	
	@Override
	public void setValue(Comparable<?> value) {
		this.value=value;	
		this.timestamp=new Date();
		sequenceNumber++;
	}

	@Override
	public Comparable<?> getValue() {
		return value;
	}		

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	@Override
	public void setTimestamp(Date timestamp) {
		this.timestamp=timestamp;
	}
	
	@Override
	public void setLocation(Geometry location) {
		this.location=location;
	}

	@Override
	public Geometry getLocation() {
		return location;
	}
	
	@Override
	public String toString() {
		return "ID: " +this.id + ", Sequence:"+ sequenceNumber +", Value:"+this.value +", Timestamp: " + DateHelper.getTimestampAsUTCString(this.timestamp) +", location: " + this.location;
	}
	
	public String toXml() {
		return DataTransformer.transformDataElementToXml(this);
	}		
}
