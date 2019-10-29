package org.fbox.fusion.algorithms.siso.ejb.impl.threshold;
public enum Operator {
	GREATER_THAN,
	LESS_THAN,
	EQUAL,
	NOT_EQUAL,
	NONE;
	
	public static Operator select(String operator) {
		switch (operator) {
		case "gt":
			return Operator.GREATER_THAN;
		case "lt":
			return Operator.LESS_THAN;
		case "eq":
			return Operator.EQUAL;
		case "neq":
			return Operator.NOT_EQUAL;
		default:
			return Operator.NONE;
		}
	}
}