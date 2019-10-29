package org.fbox.common.xml.data;

import java.util.ArrayList;

public class ContextorDAO extends GenericDAO implements Cloneable {

	ContextorType type;
	AlgorithmDAO algorithm;
	AlgorithmDAO missingValueAlgorithm;
	CombineType sourceCombineType;
	ArrayList<SourceDAO> sources;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
	
	public ContextorType getType() {
		return type;
	}

	public void setType(ContextorType type) {
		this.type = type;
	}

	public AlgorithmDAO getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(AlgorithmDAO algorithm) {
		this.algorithm = algorithm;
	}

	public AlgorithmDAO getMissingValueAlgorithm() {
		return missingValueAlgorithm;
	}

	public void setMissingValueAlgorithm(AlgorithmDAO missingValueAlgorithm) {
		this.missingValueAlgorithm = missingValueAlgorithm;
	}
	
	public CombineType getSourceCombineType() {
		return sourceCombineType;
	}

	public void setSourceCombineType(CombineType sourceCombineType) {
		this.sourceCombineType = sourceCombineType;
	}

	public ArrayList<SourceDAO> getSources() {
		return sources;
	}

	public void setSources(ArrayList<SourceDAO> sources) {
		this.sources = sources;
	}

	public SourceDAO getSource(String sourceId) {
		for (SourceDAO source : sources) {
			if (source.getId().equals(sourceId))
				return source;
		}
		
		System.out.println("here!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+sourceId);
		return null;
	}
	
	@Override
	public String toString() {
		String sb="id='"+ this.id +"', type="+this.type + ", algorithm="+ this.algorithm + ", missingValueAlgorithm=" + this.missingValueAlgorithm + ", sources={combineType="+ this.sourceCombineType +", "+ this.sources +"}";
		return sb;
	}

	@Override
	public  ContextorDAO clone() throws CloneNotSupportedException {
		return (ContextorDAO)super.clone();
	}
}
