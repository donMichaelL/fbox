package org.fbox.common.data;

public class DataStream {

	String id;
	
	String sensorId;
	String phenomenon;
	DataType type;

	public DataStream(String id, String sensorId, String phenomenon, DataType type) {
		this.id=id;
		this.sensorId = sensorId;
		this.phenomenon = phenomenon;
		this.type = type;
	}
	
	public DataStream(String streamId) {
		this.id=streamId;
	}	
	
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}

	public void setPhenomenon(String phenomenon) {
		this.phenomenon = phenomenon;
	}

	public DataType getType() {
		return this.type;
	}
	
	public String getPhenomenon() {
		return this.phenomenon;
	}

	public void setType(DataType type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((phenomenon == null) ? 0 : phenomenon.hashCode());
		result = prime * result
				+ ((sensorId == null) ? 0 : sensorId.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataStream other = (DataStream) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (phenomenon == null) {
			if (other.phenomenon != null)
				return false;
		} else if (!phenomenon.equals(other.phenomenon))
			return false;
		if (sensorId == null) {
			if (other.sensorId != null)
				return false;
		} else if (!sensorId.equals(other.sensorId))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DataStream("+ this.id +") [sensorId=" + this.sensorId + ", phenomenon=" + this.phenomenon
				+ ", type=" + this.type + "]";
	}


}
