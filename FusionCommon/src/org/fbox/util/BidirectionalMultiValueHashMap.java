package org.fbox.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class BidirectionalMultiValueHashMap<KeyType, ValueType> {

	private Map<KeyType, ArrayList<ValueType>> keyToValueMap = new LinkedHashMap<KeyType, ArrayList<ValueType>>();
	private Map<ValueType,  ArrayList<KeyType>> valueToKeyMap = new LinkedHashMap<ValueType, ArrayList<KeyType>>();

	synchronized public void put(KeyType key, ValueType value) {
		
		ArrayList<ValueType> existingValuesList;
		ArrayList<KeyType> existingKeysList;
		
		existingValuesList= keyToValueMap.get(key);
		if (existingValuesList==null) {
			existingValuesList=new ArrayList<ValueType>();
			keyToValueMap.put(key, existingValuesList);
		}			
		
		existingValuesList.add(value);
		
		existingKeysList= valueToKeyMap.get(value);
		if (existingKeysList==null) {
			existingKeysList=new ArrayList<KeyType>();
			valueToKeyMap.put(value,existingKeysList);
		}
		existingKeysList.add(key);
	}
	

	synchronized public ArrayList<ValueType> removeByKey(KeyType key) {
		
		
		ArrayList<ValueType> removed = keyToValueMap.remove(key);
		if (removed!=null) {
			for (ValueType value : removed) {
				valueToKeyMap.get(value).remove(key);
			}
		}		
		return removed;
	}

	synchronized public ArrayList<KeyType> removeByValue(ValueType value) {
		
		ArrayList<KeyType> removed = valueToKeyMap.remove(value);
		if (removed!=null) {
			for (KeyType key : removed) {
				keyToValueMap.get(key).remove(value);
			}
		}		
		return removed;
	}

	public boolean containsKey(KeyType key) {
		return keyToValueMap.containsKey(key);
	}

	public boolean containsValue(ValueType value) {
		return keyToValueMap.containsValue(value);
	}

	public ArrayList<KeyType> getKey(ValueType value) {
		return valueToKeyMap.get(value);
	}

	public ArrayList<ValueType> get(KeyType key) {
		return keyToValueMap.get(key);
	}
	
	public void printKeyToValueMap() {
		Set<Entry<KeyType, ArrayList<ValueType>>> cons = keyToValueMap.entrySet();
		for (Entry<KeyType, ArrayList<ValueType>> con : cons) {
			System.out.println(con.getKey() + "-->" + con.getValue());
		}	
	}

	public void printValueTokeyMap() {
		Set<Entry<ValueType, ArrayList<KeyType>>> cons = valueToKeyMap.entrySet();
		for (Entry<ValueType, ArrayList<KeyType>> con : cons) {
			System.out.println(con.getKey() + "-->" + con.getValue());
		}	
	}
	
	public Set<KeyType> keySet() {
		return keyToValueMap.keySet();
	}
	
	public synchronized void clear() {
		keyToValueMap.clear();
		valueToKeyMap.clear();
	}
}
