package org.sos.ds.ui.table.model;

import java.util.LinkedList;
import java.util.List;

import org.sos.ds.model.LastObservation;


public class DataTablesLastObservationModel {
	private int sEcho;
	private int iTotalRecords;
	private int iTotalDisplayRecords;
	private List<LastObservation> data;
	
	public DataTablesLastObservationModel() {
		this.data = new LinkedList<LastObservation>();
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

	public List<LastObservation> getData() {
		return data;
	}

	public void setaaData(List<LastObservation> data) {
		this.data = data;
	}
	
	
	
}
