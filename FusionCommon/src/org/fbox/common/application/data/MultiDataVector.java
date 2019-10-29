package org.fbox.common.application.data;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.fbox.common.data.IMultiDataVector;

public class MultiDataVector implements Comparable<MultiDataVector>, IMultiDataVector  {

	private static final long serialVersionUID = 134234525223424L;
	
	String[] metadata;
	Comparable[] data; 
	
	public MultiDataVector(int size, String[] metadata) {
		data=new Double[size];
		if (metadata!=null && metadata.length!=size)
			throw new RuntimeException("Number of items in Metadata does not match size of data!");
		else if (metadata==null) {
			this.metadata=new String[size];
			for (int i=0;i<size;i++) {
				this.metadata[i]=Integer.toString(i);
			}
		} else
			this.metadata=metadata;
	}
	
	public void setDataElement(Comparable value, int index) {
		if (index<data.length) {
			data[index]=value;
		}
	}

	public int size() {
		return data.length;
	}
	
	public boolean hasMetadata() {
		return (this.metadata!=null);
	}
	
	public Comparable[] getData() {
		return data;
	}

	public void setData(Comparable[] data) {
		this.data=data;
	}
	
	public Entry<String, Comparable<?>> getEntry(int index) {
		
		Entry<String, Comparable<?>> entryToReturn=null;
		
		Comparable<?> data;
		if (this.data!=null && index<this.data.length)
			data=this.data[index];
		else
			data=null;		
		
		if (data!=null) {
			String metadata=this.metadata[index];			
			entryToReturn=new AbstractMap.SimpleEntry<String, Comparable<?>>(metadata,data);
		}
		
		return entryToReturn;
	}
	
	public ArrayList<Entry<String, Comparable<?>>> getAllEntries() {
		 ArrayList<Entry<String, Comparable<?>>> entriesToReturn=new ArrayList<Entry<String, Comparable<?>>>();
		 
		 for (int i=0;i<this.data.length;i++) {
			 entriesToReturn.add(new AbstractMap.SimpleEntry<String, Comparable<?>>(this.metadata[i],this.data[i]));	
		 }
		 
		 return entriesToReturn;
	}

	public HashMap<String, Comparable<?>> getAllEntriesAsMap() {
		 HashMap<String, Comparable<?>> mapToReturn=new HashMap<String, Comparable<?>>();
		 
		 for (int i=0;i<this.data.length;i++) {
			 mapToReturn.put(this.metadata[i],this.data[i]);	
		 }
		 
		 return mapToReturn;
	}

	
	public Comparable<?> getDataElement(int index) {
		return data[index];
	}
	
	public Comparable<?> getDataElement(String metaDataLabel) {
		Comparable<?> valueToReturn=null;
		if (this.metadata!=null) {
			for (int i=0;i<this.metadata.length;i++) {
				if (this.metadata[i].equals(metaDataLabel)) {
					 valueToReturn=data[i];
					break;
				}
			}
		}
		return valueToReturn;
	}
	
	public void setDataElement(String metaDataLabel, Comparable<?> value) {
		if (this.metadata!=null) {
			for (int i=0;i<this.metadata.length;i++) {
				if (this.metadata[i].equals(metaDataLabel)) {
					 data[i]=value;
					break;
				}
			}
		}
	}	

	public Entry<Integer, Comparable> getMaxValueElement() {
		Comparable maxValue=null;
		if (data.length>0) {
			Integer index=0;
			maxValue=data[0];
			for (int i=1;i<data.length;i++) {
				if (data[i].compareTo(maxValue)>0) {
					index=i;
					maxValue=data[i];
				} 
			}			
			return new AbstractMap.SimpleEntry<Integer, Comparable>(index, maxValue);
			
		} else {
			return null;
		}
	}
	
	@Override
	public int compareTo(MultiDataVector obj) {
	
		int result=0;
		
		if (obj!=null) {
			if (obj.data.length!=this.data.length) {
				throw new RuntimeException("ERROR--->Objects have different length! Unable to compare!!!");
			} else {					
				Entry<Integer, Comparable> thisMaxElement = this.getMaxValueElement();
				Entry<Integer, Comparable> objMaxElement = obj.getMaxValueElement();
				
				if (thisMaxElement!=null && objMaxElement!=null) {
					result=thisMaxElement.getKey()-objMaxElement.getKey();
					if (result==0) {
						result=thisMaxElement.getValue().compareTo(objMaxElement.getValue());
					} 
				} else if (thisMaxElement==null) {
					if (objMaxElement!=null) {
						result=-1; //this<obj
					}						
				} else {
					if (thisMaxElement!=null) {
						result=1; //this>obj
					}					
				}
			}
		} else {
			result= 1;
		}
		
		return result;
	}

	
	@Override
	public String toString() {
		String result=null;
		if (data!=null) {
			result="{";
			for (int i=0;i<data.length;i++) {
				result+=(i!=0?", ":"")+"("+metadata[i]+":"+data[i]+")";
			}
			result+="}";
		}
		
		return result;
	}
	
}
