package org.fbox.common.application.configuration;

import java.util.Date;

import org.fbox.common.xml.data.ApplicationDAO;

public class ApplicationInfo {

	String id;
	ApplicationDAO model;
	ApplicationStatus status;
	String earFilePath;
	String deployedModuleName;
	Date deploymentTime;
	String sourceFileName;
	
	/** NEW **/
	Date lastReportTime;
	long reportedValues;
	
	public ApplicationInfo() {
		deploymentTime = new Date();
		
		/** NEW **/
		this.lastReportTime = this.deploymentTime;
		this.reportedValues = 0;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ApplicationDAO getModel() {
		return model;
	}

	public void setModel(ApplicationDAO model) {
		this.model = model;
	}

	public ApplicationStatus getStatus() {
		return status;
	}

	public void setStatus(ApplicationStatus status) {
		this.status = status;
	}

	public String getEarFilePath() {
		return earFilePath;
	}

	public void setEarFilePath(String earFilePath) {
		this.earFilePath = earFilePath;
	}

	public String getDeployedModuleName() {
		return deployedModuleName;
	}

	public void setDeployedModuleName(String deployedModuleName) {
		this.deployedModuleName = deployedModuleName;
	}

	public Date getDeploymentTime() {
		return deploymentTime;
	}

	public void setDeploymentTime(Date deploymentTime) {
		this.deploymentTime = deploymentTime;
	}
	
	/** NEW **/
	public Date getLastReportTime() {
		return this.lastReportTime;
	}

	/** NEW **/
	private void setLastReportTime(Date lastReportTime) {
		this.lastReportTime = lastReportTime;
	}
	
	/** NEW **/
	public void updateStats(Date lastReportTime) {
		this.setLastReportTime(lastReportTime);
		this.reportedValues++;
	}
	
	/** NEW **/
	public long getReportedValues() {
		return this.reportedValues;
	}
	
	/** NEW **/
	public String getSourceFileName() {
		return sourceFileName;
	}

	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}
}
