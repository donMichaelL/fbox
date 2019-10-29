package org.fbox.fusion.output.formatter.data;

public class AreaPoint {

	private Integer row;
	private Integer column;
	
	public AreaPoint (Integer r, Integer c) {
		this.row = r;
		this.column = c;
	}
	
	public Integer getRow() {
		return row;
	}

	public void setRow(Integer row) {
		this.row = row;
	}

	public Integer getColumn() {
		return column;
	}

	public void setColumn(Integer column) {
		this.column = column;
	}
}
