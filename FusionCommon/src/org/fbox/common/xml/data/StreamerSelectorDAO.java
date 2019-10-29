package org.fbox.common.xml.data;

import java.util.HashSet;
import java.util.Set;

public class StreamerSelectorDAO extends GenericDAO {
	
	boolean dynamic;
	Set<SelectDAO> selectConstraints = new HashSet<SelectDAO>();
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
	
	public boolean isDynamic() {
		return dynamic;
	}

	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

	public Set<SelectDAO> getSelectConstraints() {
		return selectConstraints;
	}

	public void setSelectConstraints(Set<SelectDAO> selectConstraints) {
		this.selectConstraints = selectConstraints;
	}

	public void addSelectConstraint(SelectDAO sc) {
		this.selectConstraints.add(sc);
	}
	
	@Override
	public String toString() {
		String sb="id='"+ this.id +"', isDynamic="+dynamic + ", constraints={";
		int i=0;
		int size=selectConstraints.size();
		for (SelectDAO s : selectConstraints) {
			sb+="("+(++i)+ ")-->["+s+ "]";
			if (i<size)
				sb+=", ";
		}
		sb+="}";
			
		return sb;
	}
	
}
