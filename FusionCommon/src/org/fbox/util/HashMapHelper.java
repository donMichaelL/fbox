package org.fbox.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

public class HashMapHelper {

	public <K,V extends Object> void printMultiValueMap(HashMap<K, ArrayList<V>> dataMap) {
		// print map of Contextors
		Set<Entry<K, ArrayList<V>>> cons = dataMap.entrySet();
		for (Entry<K, ArrayList<V>> con : cons) {
			System.out.println(con.getKey() + "-->" + con.getValue());
		}		
	}
	
	public <K,V extends Object> void printSingleValueMap(HashMap<K, V> dataMap) {
		// print map of Contextors
		Set<Entry<K, V>> cons = dataMap.entrySet();
		for (Entry<K, V> con : cons) {
			System.out.println(con.getKey() + "-->" + con.getValue());
		}		
	}	
}
