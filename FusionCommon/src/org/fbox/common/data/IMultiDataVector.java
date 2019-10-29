package org.fbox.common.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public interface IMultiDataVector extends Serializable {
	
	public void setDataElement(Comparable value, int index);
	public int size();
	public boolean hasMetadata();
	public Comparable[] getData();
	public void setData(Comparable[] data);
	public Entry<String, Comparable<?>> getEntry(int index);
	public ArrayList<Entry<String, Comparable<?>>> getAllEntries();
	public HashMap<String, Comparable<?>> getAllEntriesAsMap();
	public Comparable<?> getDataElement(int index);
	public Comparable<?> getDataElement(String metaDataLabel);
	public void setDataElement(String metaDataLabel, Comparable<?> value);
	public Entry<Integer, Comparable> getMaxValueElement();
}
