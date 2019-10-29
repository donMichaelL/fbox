package org.fbox.configurator.ejb.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import net.java.truevfs.access.TFile;
import net.java.truevfs.access.TVFS;
import net.java.truevfs.kernel.spec.FsSyncException;

import org.fbox.common.IFusionConstants;
import org.fbox.common.application.configuration.ApplicationInfo;
import org.fbox.common.application.configuration.ApplicationStatus;
import org.fbox.common.db.ConfigurationParameter;
import org.fbox.configurator.exceptions.ApplicationBuilderException;
import org.fbox.configurator.exceptions.ApplicationDeployerException;
import org.fbox.configurator.exceptions.DMRManagerException;
import org.fbox.persistence.impl.DBManager;
import org.jboss.dmr.ModelNode;

/**
 * Session Bean implementation class ApplicationDeployerBean
 */
@Stateless
@LocalBean
public class ApplicationDeployerBean {

	@EJB
	DMRManagerBean dmrManager;
	@EJB
	DBManager fDbManager;
	

	private static String deploymentPath;
	//private static String deploymentPath = "c:/development/IDIRA/jboss-as-7.1.0.Final/standalone/fusion";
	//private static String deploymentPath = "C:/jboss-as-7.1.0.Final/standalone/fusion";
	private long deployTimeOutPeriod=60000; //60 secs

    /**
     * Default constructor. 
     */
    public ApplicationDeployerBean() {
    }
    
    @PostConstruct
    private void init() throws ApplicationBuilderException {
    	
    	ConfigurationParameter pathParam=fDbManager.getConfigParameter(IFusionConstants.DEPLOYMENT_PATH);
    	if (pathParam==null) {
    		throw new ApplicationBuilderException("DEPLOYMENT_PATH has not been set in DB");
    	} 
    	deploymentPath=pathParam.getValue();
		
    	ConfigurationParameter periodParam=fDbManager.getConfigParameter(IFusionConstants.DEPLOY_TIMEOUT_PERIOD);
    	if (periodParam==null) {
    		System.out.println("DEPLOY_TIMEOUT_PERIOD has not been set in DB. Using DEFAULT value ("+ periodParam +") secs");
    	} 
    	deployTimeOutPeriod=Long.parseLong(periodParam.getValue());
    	
    }

    public boolean clearAllDeploymentMetafiles(String filename) {
    	boolean result=true;
    	try {
	    	TFile deployFile=new TFile(new File(filename+".dodeploy"));    	
			if (deployFile.exists())
				deployFile.rm();
	    	TFile undeployFile=new TFile(new File(filename+".undeploy"));
			if (undeployFile.exists())
				undeployFile.rm();
	    	TFile deployFileStatus=new TFile(new File(filename+".deployed"));
			if (deployFileStatus.exists())
				deployFileStatus.rm();    	
	    	TFile undeployFileStatus=new TFile(new File(filename+".undeployed"));
			if (undeployFileStatus.exists())
				undeployFileStatus.rm();      	
		} catch (IOException e) {
			result=false;
		}   	
    	
    	return result;
    }
    
    public void deployApplication(ApplicationInfo appInfo) throws ApplicationDeployerException {
    	appInfo.setStatus(ApplicationStatus.DEPLOYING);
    	String filename=appInfo.getEarFilePath();
    	
     	TFile deployFile=new TFile(new File(filename+".dodeploy"));
    	try {
    		if (clearAllDeploymentMetafiles(filename))    			
    			deployFile.createNewFile();
    		else
    			throw new ApplicationDeployerException("Error while clearing deployment metafiles for :"+filename);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				TVFS.umount();
			} catch (FsSyncException e) {
				e.printStackTrace();
			}
		}
    }

    public void undeployApplication(ApplicationInfo appInfo) throws ApplicationDeployerException {
    	appInfo.setStatus(ApplicationStatus.UNDEPLOYING);
    	String filename=appInfo.getEarFilePath();
    	TFile undeployFile=new TFile(new File(filename+".undeploy"));
    	try {
    		if (clearAllDeploymentMetafiles(filename)) {
    			System.out.println(undeployFile.getAbsolutePath());
    			undeployFile.createNewFile();
    		} else
    			throw new ApplicationDeployerException("Error while clearing deployment metafiles for :"+filename);    		
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				TVFS.umount();
			} catch (FsSyncException e) {
				e.printStackTrace();
			}
		} 		
    }    
    
    public String getDeploymentPath() throws ApplicationBuilderException {
    	return deploymentPath;
    }
    
    public void setDeploymentPath(String newDeploymentPath) {
    	deploymentPath = newDeploymentPath;
    }
    
    public synchronized boolean checkIfDeployed(String filename) throws ApplicationDeployerException, IOException {
    	
    	String deployCheckJsonFormattedOperationString="{\"address\" : [{ \"deployment\" : \"" +
    			filename +"\" }], \"operation\" : \"read-resource\"}";
    	
    	boolean isDeployed=false;
    	
    	//System.out.println(ModelNode.fromJSONString(deployCheckJsonFormattedOperationString));
    	try {
			dmrManager.connect();
	    	ModelNode resultOfCheck=dmrManager.runOperation(deployCheckJsonFormattedOperationString);
	    	System.out.println(resultOfCheck);
	    	if (resultOfCheck.get("outcome").asString().equals("success")) 
	    		isDeployed= true;
		} catch (DMRManagerException e) {
			e.printStackTrace();
			throw new ApplicationDeployerException(e.getMessage(),e);
		} finally {
			try {
				dmrManager.disconnect();
			} catch (DMRManagerException e) {
				e.printStackTrace();
			}
		}
		
    	
    	return isDeployed;
    }

    public synchronized boolean waitForUndeployment(String filename) throws ApplicationDeployerException, IOException {
    	
    	String deployCheckJsonFormattedOperationString="{\"address\" : [{ \"deployment\" : \"" +
    			filename +"\" }], \"operation\" : \"read-resource\"}";
    	
    	boolean isUnDeployed=false;
    	
    	long  stopCheckingTime= new Date().getTime()+ deployTimeOutPeriod;
    	while (!isUnDeployed & (stopCheckingTime-new Date().getTime())>0) {
	    	try {
				dmrManager.connect();
		    	ModelNode resultOfCheck=dmrManager.runOperation(deployCheckJsonFormattedOperationString);
		    	//System.out.println("##"+resultOfCheck);
		    	if (resultOfCheck.get("outcome").asString().equals("failed")) 
		    		isUnDeployed= true;
			} catch (DMRManagerException e) {				
				e.printStackTrace();
				throw new ApplicationDeployerException(e.getMessage(),e);
			} finally {
				try {
					dmrManager.disconnect();
				} catch (DMRManagerException e) {
					e.printStackTrace();
				}
			}
    	}
    	
    	return isUnDeployed;
    }     
    
    public synchronized boolean waitForDeployment(String filename) throws ApplicationDeployerException, IOException {
    	
    	String deployCheckJsonFormattedOperationString="{\"address\" : [{ \"deployment\" : \"" +
    			filename +"\" }], \"operation\" : \"read-resource\"}";
    	
    	boolean isDeployed=false;
    	
    	long  stopCheckingTime= new Date().getTime()+ deployTimeOutPeriod;
    	while (!isDeployed & (stopCheckingTime-new Date().getTime())>0) {
	    	try {
				dmrManager.connect();
		    	ModelNode resultOfCheck=dmrManager.runOperation(deployCheckJsonFormattedOperationString);
//		    	System.out.println(resultOfCheck);
		    	if (resultOfCheck.get("outcome").asString().equals("success")) 
		    		isDeployed= true;
			} catch (DMRManagerException e) {
				e.printStackTrace();
				throw new ApplicationDeployerException(e.getMessage(),e);
			} finally {
				try {
					dmrManager.disconnect();
				} catch (DMRManagerException e) {
					e.printStackTrace();
				}
			}
    	}
    	
    	return isDeployed;
    }    
}
