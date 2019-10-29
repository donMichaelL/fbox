package org.fbox.common.network.data;

import java.util.Date;

import org.fbox.common.application.data.DataElement;
import org.fbox.common.data.IMeasuredDataElement;
import org.fbox.util.UUIDGenerator;

public class MeasuredDataElement extends DataElement implements IMeasuredDataElement {

	static final String ID_PREFIX="MD";
	
	public MeasuredDataElement(String id) {
		super(id);
	}
			
	@Override
	public void setTimestamp(Date timestamp) {
		this.timestamp=timestamp;
	}
	
}
