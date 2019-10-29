package org.fbox.fusion.application.configuration.util;

import java.util.Comparator;
import java.util.LinkedList;

public class ListComparator implements Comparator<LinkedList<String>> {

	@Override
	public int compare(LinkedList<String> o1, LinkedList<String> o2) {
		int length1=o1.size();
		int length2=o2.size();
		
		int comparison=length1-length2;
		if (comparison==0) {
			for (int i=length1-1;i>=0;i--) {
				comparison=o1.get(i).compareTo(o2.get(i));
				//System.out.println(o1.get(i)+ " vs " +o2.get(i)+" ---> "+ comparison);
				if (comparison!=0)
					break;
			}
		}		
	
		return comparison;
	}
}