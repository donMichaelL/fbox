package org.fbox.common.registry;

import java.util.HashMap;
import java.util.LinkedHashMap;
import org.fbox.util.HashMapHelper;

public abstract class AbstractRegistry<T> implements IRegistry<T>{
	

	private HashMap<String, T> registry=new LinkedHashMap<String, T>();

    public synchronized void addEntry(String id, T entry, boolean replaceIfExists) throws RegistryInsertionError {
    	
    	T oldEntry=null;
    	if (registry.get(id)==null | replaceIfExists) {
    		oldEntry=registry.put(id, entry);
    		System.out.println("New Entry with id="+id +" added to Registry");
    	}
    	
    	if (oldEntry!=null) 
    		System.out.println("WARNING ---> Mapping already exists! Mapping Replaced. Old Mapping="+oldEntry);
    }
    
    public T getEntry(String id) {
    	return registry.get(id);
    }

    public T removeEntry(String id) {
    	return registry.remove(id);
    }
    
    public HashMap<String, T> getRegistryEntries() {
    	return registry;
    }
    
	public void printRegistry() {
		new HashMapHelper().printSingleValueMap(registry);
	}
	
    public void clear() {
    	registry.clear();
    }	

}
