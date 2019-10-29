package org.sos.ds.ui.table.model;

import java.util.LinkedList;
import java.util.List;

import org.sos.ds.unsorted.LastPosition;

public class DataTablesLastPositionModel {
	private int sEcho;
	private int iTotalRecords;
	private int iTotalDisplayRecords;
	// private String haha = "aaData";
	private List<LastPosition> data;
	
	
	public DataTablesLastPositionModel() {
		this.data = new LinkedList<LastPosition>();
	}
	
	
	
	public DataTablesLastPositionModel(int sEcho, int iTotalRecords,
			int iTotalDisplayRecords, String aaData,
			List<LastPosition> lpDaoList) {
		super();
		this.sEcho = sEcho;
		this.iTotalRecords = iTotalRecords;
		this.iTotalDisplayRecords = iTotalDisplayRecords;
		// this.haha = aaData;
		this.data = lpDaoList;
	}
/*
	public String getAaData() {
		return haha;
	}
	
	public void setAaData(String aaData) {
		this.haha = aaData;
	}
	*/
	public List<LastPosition> getdata() {
		return data;
	}
	
	public void setdata(List<LastPosition> lpDaoList) {
		this.data = lpDaoList;
	}

	public int getsEcho() {
		return sEcho;
	}

	public void setsEcho(int sEcho) {
		this.sEcho = sEcho;
	}

	public int getiTotalRecords() {
		return iTotalRecords;
	}

	public void setiTotalRecords(int iTotalRecords) {
		this.iTotalRecords = iTotalRecords;
	}

	public int getiTotalDisplayRecords() {
		return iTotalDisplayRecords;
	}

	public void setiTotalDisplayRecords(int iTotalDisplayRecords) {
		this.iTotalDisplayRecords = iTotalDisplayRecords;
	}
	
	
	
	
}
