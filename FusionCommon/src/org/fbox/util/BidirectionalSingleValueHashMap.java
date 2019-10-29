package org.fbox.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class BidirectionalSingleValueHashMap<KeyType, ValueType> {

	private Map<KeyType, ValueType> keyToValueMap = new LinkedHashMap<KeyType,ValueType>();
	private Map<ValueType,  KeyType> valueToKeyMap = new LinkedHashMap<ValueType, KeyType>();

        synchronized public void put(KeyType key, ValueType value){
            keyToValueMap.put(key, value);
            valueToKeyMap.put(value, key);
        }

        synchronized public ValueType removeByKey(KeyType key){
            ValueType removedValue = keyToValueMap.remove(key);
            valueToKeyMap.remove(removedValue);
            return removedValue;
        }

        synchronized public KeyType removeByValue(ValueType value){
            KeyType removedKey = valueToKeyMap.remove(value);
            keyToValueMap.remove(removedKey);
            return removedKey;
        }

        public boolean containsKey(KeyType key){
            return keyToValueMap.containsKey(key);
        }

        public boolean containsValue(ValueType value){
            return keyToValueMap.containsValue(value);
        }

        public KeyType getKey(ValueType value){
            return valueToKeyMap.get(value);
        }

        public ValueType get(KeyType key){
            return keyToValueMap.get(key);
        }
	
	public void printKeyToValueMap() {
		Set<Entry<KeyType, ValueType>> cons = keyToValueMap.entrySet();
		for (Entry<KeyType, ValueType> con : cons) {
			System.out.println(con.getKey() + "-->" + con.getValue());
		}	
	}

	public void printValueTokeyMap() {
		Set<Entry<ValueType, KeyType>> cons = valueToKeyMap.entrySet();
		for (Entry<ValueType, KeyType> con : cons) {
			System.out.println(con.getKey() + "-->" + con.getValue());
		}	
	}
	
}
