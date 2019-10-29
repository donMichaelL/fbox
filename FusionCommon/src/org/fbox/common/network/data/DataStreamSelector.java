package org.fbox.common.network.data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.fbox.common.xml.data.SelectDAO;
import org.fbox.common.xml.data.StreamerSelectorDAO;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class DataStreamSelector implements Serializable{
	
	String phenomenon;
	Set<String> sensorIds;
	Geometry spatial; //WKTReader
	boolean dynamic;

	public DataStreamSelector(String phenomenon, Set<String> sensorIds, Geometry spatial, boolean dynamic) {
		this.spatial = spatial;
		this.phenomenon = phenomenon;
		this.sensorIds = sensorIds;
		this.dynamic=dynamic;
	}
	
	public DataStreamSelector(StreamerSelectorDAO ss) {
		Set<SelectDAO> selectConstraints=ss.getSelectConstraints();
		for (SelectDAO select : selectConstraints) {
			switch (select.getType()) {
				case "spatial": 
					try {
						this.spatial = new WKTReader().read(select.getValue());
					} catch (ParseException e) {
						e.printStackTrace();
					}
					break;
				case "phenomenon": 
					this.phenomenon=select.getValue();
					break;
				case "identity":
					String[] sensors=select.getValue().split(";");
					if (sensors!=null) {
						sensorIds=new HashSet<String>();
						for (String sensorId : sensors) {
							sensorIds.add(sensorId);
						}
					}
					break;
			}
		}
		this.dynamic=ss.isDynamic();
	}


	public Geometry getSpatial() {
		return spatial;
	}

	public void setSpatial(Geometry spatial) {
		this.spatial = spatial;
	}

	public String getPhenomenon() {
		return phenomenon;
	}

	public void setPhenomenon(String phenomenon) {
		this.phenomenon = phenomenon;
	}

	public Set<String> getSensorIds() {
		return sensorIds;
	}

	public void setSensorIds(Set<String> sensorIds) {
		this.sensorIds = sensorIds;
	}

	public boolean isDynamic() {
		return dynamic;
	}

	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}
}
