package org.fbox.common.data;

import java.io.Serializable;
import java.util.Date;

import com.vividsolutions.jts.geom.Geometry;

public interface IDataElement extends Serializable{

	public String getId();
	public Comparable<?> getValue();
	public void setValue(Comparable<?> value);
	public Date getTimestamp();
	public void setTimestamp(Date timestamp);
	public Geometry getLocation();
	public void setLocation(Geometry location);
	public long getSequenceNumber();	
	
}
