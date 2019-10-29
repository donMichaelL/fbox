package org.fbox.network.sos.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import org.fbox.common.data.DataStream;
import org.fbox.common.network.data.DataStreamSelector;
import org.fbox.network.sos.db.dao.ObservationDAO;

public interface ISosDBManager {
	public Set<DataStream> getDataStreams(DataStreamSelector dsSelector) throws SQLException;
	public ArrayList<ObservationDAO> getObservations(Set<DataStream> dataStreams, Date minTimestamp, Date maxTimestamp) throws SQLException; 
}
