package fbox.fusion.core.http.datatables;

import java.util.LinkedList;
import java.util.List;

import org.fbox.common.application.configuration.ApplicationInfo;

public class DataTablesViewApplicationRegistryModel {
	private int sEcho;
	private int iTotalRecords;
	private int iTotalDisplayRecords;
	private List<ApplicationInfo> data;
	
	public DataTablesViewApplicationRegistryModel() {
		this.data = new LinkedList<ApplicationInfo>();
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

	public List<ApplicationInfo> getData() {
		return data;
	}

	public void setaaData(List<ApplicationInfo> data) {
		this.data = data;
	}
	
	
	
}
