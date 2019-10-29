package org.fbox.common.registry;

import java.util.HashMap;

public interface IRegistry<T> {

	public void addEntry(String id, T entry, boolean replaceIfExists) throws RegistryInsertionError;
	public T getEntry(String id);
	public T removeEntry(String id);
	public HashMap<String, T> getRegistryEntries();
	public void printRegistry();
	public void clear();
}
