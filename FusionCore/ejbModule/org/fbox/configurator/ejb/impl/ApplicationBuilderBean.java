package org.fbox.configurator.ejb.impl;

import java.io.File;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.xml.parsers.ParserConfigurationException;
import net.java.truevfs.access.TFile;
import net.java.truevfs.access.TFileReader;
import net.java.truevfs.access.TFileWriter;
import net.java.truevfs.access.TVFS;
import net.java.truevfs.kernel.spec.FsSyncException;

import org.fbox.common.IFusionConstants;
import org.fbox.common.application.configuration.ApplicationInfo;
import org.fbox.common.application.configuration.ApplicationStatus;
import org.fbox.common.application.configuration.IConfigure;
import org.fbox.common.application.util.IServiceLocator;
import org.fbox.common.db.ConfigurationParameter;
import org.fbox.common.exception.ApplicationAlreadyExistsException;
import org.fbox.common.exception.ApplicationConfigurationException;
import org.fbox.common.exception.EventSourceParserException;
import org.fbox.common.exception.ServiceLocatorException;
import org.fbox.common.registry.IRegistry;
import org.fbox.common.registry.RegistryInsertionError;
import org.fbox.common.xml.data.ApplicationDAO;
import org.fbox.common.xml.parsers.ApplicationSourceParser;
import org.fbox.configurator.exceptions.ApplicationBuilderException;
import org.fbox.configurator.exceptions.ApplicationDeployerException;
import org.fbox.persistence.impl.DBManager;


/**
 * Session Bean implementation class ApplicationBuilder
 */
@Stateless
@LocalBean
public class ApplicationBuilderBean {

	@EJB
	ApplicationDeployerBean deployer;
	@EJB
	IServiceLocator serviceLocator;
	@EJB
	DBManager fDbManager;
	@EJB
	IRegistry<ApplicationInfo> applicationRegistry;
	
	
	
	private String templateApplicationEar;    
	//private static String templateApplicationEar = "c:/development/IDIRA/jboss-as-7.1.0.Final/standalone/fusion/FBoxApplication.ear";
	private String applicationFileNamePrefix;
	
	//private static String applicationFileNamePrefix="FA_";	
	private static String fileToModifyEjbJar="FusionApplication.jar/META-INF/ejb-jar.xml";
	private static String fileToModifyApplicationXml="META-INF/application.xml";
	private static String fileToModifyDataJmsXml="FusionApplication.jar/META-INF/data-jms.xml";
	
	//configuration parameter
	private static boolean shouldValidate=false;
//	private static String jndiLookupEntry="/FusionApplication/Configurator!org.fbox.fusion.application.configuration.IConfigure";
	
	
    /**
     * Default constructor. 
     */
    public ApplicationBuilderBean() {
    }
    
    @PostConstruct
    private void init() throws ApplicationBuilderException {
    	
    	ConfigurationParameter templateParam=fDbManager.getConfigParameter(IFusionConstants.TEMPLATE_APPLICATION_NAME);
    	if (templateParam==null) {
    		throw new ApplicationBuilderException("TEMPLATE_APPLICATION_NAME has not been set in DB");
    	}     	   
    	
    	String deployPath=deployer.getDeploymentPath();
    	System.out.println("________________>"+deployPath);
    	templateApplicationEar=deployPath + (deployPath.endsWith(File.separator)?"":File.separator)  + templateParam.getValue();
    	
    	ConfigurationParameter filePrefix=fDbManager.getConfigParameter(IFusionConstants.APPLICATION_FILE_NAME_PREFIX);
    	if (filePrefix==null) {
    		throw new ApplicationBuilderException("APPLICATION_FILE_NAME_PREFIX has not been set in DB");
    	}  
    	applicationFileNamePrefix=filePrefix.getValue();
    }

    public ApplicationInfo buildApplication(StringReader source, boolean startApplication) throws ApplicationBuilderException, EventSourceParserException, ApplicationDeployerException, IOException, RegistryInsertionError, ParserConfigurationException, ApplicationAlreadyExistsException {
    	// startApplication = true
    	//build the Application DAO from the xml source
       	ApplicationDAO applicationSpecificationModel= new ApplicationSourceParser(source).parse(shouldValidate);
    	
    	//create the application info object to register
    	ApplicationInfo ai=new ApplicationInfo();
    	ai.setId(applicationSpecificationModel.getId());
    	ai.setModel(applicationSpecificationModel);	    
    	ai.setStatus(ApplicationStatus.DEPLOYING);
    	ai.setDeployedModuleName(determineDeployableModuleName(ai.getId()));
    	
    	ApplicationInfo appInfo=applicationRegistry.getEntry(ai.getId());
    	if (appInfo!=null) {
    		throw new ApplicationAlreadyExistsException("An application with the specified ID ("+ai.getId()+") is already registered. Please undeploy it and try again!");
    	} else {
    		applicationRegistry.addEntry(ai.getId(), ai , true);
    	}
    	
        try {        	
        	//deploy application creates the file structure (ear file) and deploys it to the appropriate folder 
	    	deploy(ai, startApplication);
	   		    	
	    	if (startApplication) {
	    		//configure application, creates the application specific memory structures inside the ear
	    		configureApplication(ai);
	    	}
	    	
	    	
    	} catch  (Exception e) {    		
    		if (deployer.checkIfDeployed(ai.getDeployedModuleName())) {
    			undeploy(ai, true);
    			
    			try {
    				TVFS.umount();
    				deleteFile(determineDeployableModuleFullPath(ai.getId()));  
    				applicationRegistry.removeEntry(ai.getId());
    			} catch (FsSyncException e1) {
    				e1.printStackTrace();
    			}     			
    		}
    		//rethrow exception
    		throw e;
    	}   	    
        
	    return ai;    	
    }
        
    public ApplicationInfo buildApplication(String source, boolean waitDeploy) throws ApplicationBuilderException, EventSourceParserException, ApplicationDeployerException, IOException, RegistryInsertionError, ParserConfigurationException, ApplicationAlreadyExistsException {
    	// waitDeploy = true
    	try (StringReader sr=new StringReader(source)) {
    		ApplicationInfo app=buildApplication(sr, waitDeploy);	
    		app.setSourceFileName(app.getDeployedModuleName().replace(".ear",".xml"));
    		storeSourceFile(source, app.getSourceFileName());
    		return app;
    	}    
    }
    
    private void storeSourceFile(String fileSource, String filename) {
		String sourcePath=getSourcesPath();
		//String sourcePath="c:/development/IDIRA/jboss-as-7.1.0.Final/standalone/scripts/";		
		Path path=Paths.get(sourcePath,filename);
		Charset charset = Charset.forName("UTF-8");
		try (BufferedWriter writer = Files.newBufferedWriter(path,charset, new OpenOption[] {StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE})) {
		    writer.write(fileSource, 0, fileSource.length());
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
    }

    private String getSourcesPath() {
		ConfigurationParameter sourcePathParam=fDbManager.getConfigParameter(IFusionConstants.SOURCES_FILE_PATH);
		String sourcePath=sourcePathParam.getValue();
		sourcePath+=sourcePath.endsWith(File.separator)?"":File.separator;
		return sourcePath;
    }
    
    private void deleteSourceFile(String filename) {
		String sourcePath=getSourcesPath();
		//String sourcePath="c:/development/IDIRA/jboss-as-7.1.0.Final/standalone/scripts/";		
		Path path=Paths.get(sourcePath,filename);
		try {
			Files.delete(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public boolean destroyApplication(String applicationId, boolean waitUndeploy) throws ApplicationBuilderException, EventSourceParserException, ApplicationDeployerException, IOException {
    	    
    	boolean destroyStatus=false;
    	
    	ApplicationInfo ai=applicationRegistry.getEntry(applicationId);
    	if (ai==null) {
    		throw new ApplicationBuilderException("Specified application with id"+ applicationId + " does not exist");
    	} else {
    			
	    	undeploy(ai, waitUndeploy);
	    	
	    	try {
	    		TVFS.umount();
	    		deleteFile(ai.getEarFilePath()); 	
	    	} catch (FsSyncException e) {
	    		e.printStackTrace();
	    	} 
	
	    	destroyStatus=true;    		
			applicationRegistry.removeEntry(applicationId);
			deleteSourceFile(ai.getSourceFileName());
    	}
    	
    	return destroyStatus;
    }    
    
    private void configureApplication(ApplicationInfo appInfo) throws ApplicationBuilderException  {
    	if (appInfo==null) {
    		throw new ApplicationBuilderException("A NULL application Info object was specified");
    	}
    	try {
    		String earName=appInfo.getDeployedModuleName();
			IConfigure configurator=serviceLocator.getConfigurator(earName.substring(0, earName.length()-4)); //remove .ear from the filename to determine the deployed module
			configurator.configure(appInfo);
		} catch (ServiceLocatorException | ApplicationConfigurationException e) {
			throw new ApplicationBuilderException(e);
		}    	
    }
    
    private boolean unconfigureApplication(ApplicationInfo appInfo) throws ApplicationBuilderException  {
    	boolean result=false;
    	
    	try {
    		String earName=appInfo.getDeployedModuleName();
			IConfigure configurator=serviceLocator.getConfigurator(earName.substring(0, earName.length()-4));
			result=configurator.unconfigure();
		} catch (ServiceLocatorException | ApplicationConfigurationException e) {
			throw new ApplicationBuilderException(e);
		}
    	return result;
    }
    
    private void deploy(ApplicationInfo appInfo, boolean waitDeploy) throws ApplicationBuilderException, ApplicationDeployerException, IOException {    	
    	
    	if (appInfo!=null) {
    		ApplicationDAO applicationSpecificationModel=appInfo.getModel();
    		String applicationId=applicationSpecificationModel.getId();
    		
    		//check if application with the same name already exists
    		appInfo.setDeployedModuleName(appInfo.getDeployedModuleName());
    		if (!deployer.checkIfDeployed(appInfo.getDeployedModuleName())) {
    			
    			
	    		//create new application ear
    			String fileToDeploy=createApplicationEarFileStructure(applicationId,appInfo.getDeployedModuleName());
    			appInfo.setEarFilePath(fileToDeploy);
   				deployer.deployApplication(appInfo);
    			if (waitDeploy) {//if deploy flag is set then wait for deployment to complete
    				if (!deployer.waitForDeployment(appInfo.getDeployedModuleName()))
    					throw new ApplicationBuilderException("Deploy of "+ appInfo.getDeployedModuleName() +" failed to complete");
    			}    			
    		} else
    			throw new ApplicationBuilderException("Application with id=" + applicationId + " already exists");    		
    	}
    	appInfo.setStatus(ApplicationStatus.DEPLOYED);
    	
    	//TODO
    	//Create DB entry for deployed application and specified source!
    	//So that if server shuts down, on startup re-deployments would take place again.
    }

    private void undeploy(ApplicationInfo appInfo, boolean waitUndeploy) throws ApplicationDeployerException, IOException, ApplicationBuilderException {
   	   	
    	if (appInfo==null)
    		throw new ApplicationDeployerException("No application id specified. Nothing to undeploy.");

    	//check if specified application is deployed  
    	boolean isDeployed=deployer.checkIfDeployed(appInfo.getDeployedModuleName());
    	
    	if (isDeployed) {//perform undeployment
    		if (unconfigureApplication(appInfo)) { //wait for the application to unconfigure
    			deployer.undeployApplication(appInfo);
    		}    		
    	} else { //if not deployed then report the undeployment failure
    		System.out.println("Specified application ("+ appInfo.getId() +") is not deployed. No undeployment performed");
    	}
  
    	if (waitUndeploy) {//if undeploy flag is set then wait for undeployment to complete
    		if (!deployer.waitForUndeployment(appInfo.getDeployedModuleName()))
    			throw new ApplicationBuilderException("UnDeployment of "+ appInfo.getId() +" failed to complete. Please try to perform it manually");
    	}
    	
    	appInfo.setStatus(ApplicationStatus.UNDEPLOYED);
    }
        
    
    private String determineDeployableModuleName(String applicationId) {    	
    	return applicationFileNamePrefix+applicationId+".ear";
    }    
    
    private String determineDeployableModuleFullPath(String applicationId) throws ApplicationBuilderException {    	
    	return deployer.getDeploymentPath() + File.separator+ determineDeployableModuleName(applicationId);
    }    
        
    //returns the final filename to be deployed
    private String createApplicationEarFileStructure(String applicationId, String filenameToDeploy) throws ApplicationBuilderException {
    	
    	int i=1;
    	try {
    		//copy template file with the appplicationId name
    		String srcPath=templateApplicationEar;
    		String dstPath=determineDeployableModuleFullPath(applicationId);
    		
    		//copy file to destination folder with new name based on applciationId
    		copyFileAs(srcPath, dstPath);        	        	
        	System.out.println("Copying "+ srcPath + "--->"+ dstPath);
        	displayMemoryUsage(i++);
        	
   			//open ear and modify files
			updateEjbDeploymentDescriptor(dstPath, applicationId);
			displayMemoryUsage(i++);
			updateEarApplicationDescriptor(dstPath, applicationId);
			displayMemoryUsage(i++);
			updateJmsDeploymentDescriptor(dstPath, applicationId);
			displayMemoryUsage(i++);

			//rename web part
			moveFile(dstPath+"/"+"FusionApplication.war", dstPath+"/"+applicationId+".war");
        	System.out.println("Renaming web interface to " + applicationId);
        	displayMemoryUsage(i++);
        	
        	return dstPath;
		} catch (IOException e) {
			e.printStackTrace();
			throw new ApplicationBuilderException("Unable to copy application archive with id=" + applicationId + ". "+e.getMessage());
		} finally {
        	try {
				TVFS.umount();
				displayMemoryUsage(i++);
			} catch (FsSyncException e) {
				e.printStackTrace();
			}
		}
    	
    }
     
    private void displayMemoryUsage(int index) {
		System.out.println(index+".Total Memory="+Runtime.getRuntime().totalMemory()/1024/1024);
		System.out.println(index+".Free Memory="+Runtime.getRuntime().freeMemory()/1024/1024);
    }
		
    private void updateEjbDeploymentDescriptor(String earFilePath, String applicationId) throws IOException {
    	String ejbJarFilePath=earFilePath+File.separator+fileToModifyEjbJar;
    	updateFile(ejbJarFilePath, applicationId);
    }

    private void updateEarApplicationDescriptor(String earFilePath, String applicationId) throws IOException {
    	String applicationDescriptorFilePath=earFilePath+File.separator+fileToModifyApplicationXml;
    	updateFile(applicationDescriptorFilePath, applicationId);
    }
    
    private void updateJmsDeploymentDescriptor(String earFilePath, String applicationId) throws IOException {
    	String jmsFilePath=earFilePath+File.separator+fileToModifyDataJmsXml;
    	updateFile(jmsFilePath, applicationId);
    }
    
    private void moveFile(String fileToRename, String newName) throws IOException {
    	TFile file=new TFile(new File(fileToRename));
    	file.mv(new TFile(newName));
    }

    private void copyFileAs(String srcFileName, String dstFileName) throws IOException {
    	TFile srcFile=new TFile(srcFileName);
    	srcFile.cp_rp(new TFile(dstFileName));
    }
    
    private void deleteFile(String fileName) throws IOException {
    	TFile file=new TFile(fileName);
    	if (file.exists())
    		file.rm_r();    	
    }    

    private void updateFile(String fileName, String applicationId) throws IOException {

    	String retrievedData;
    	TFile file=new TFile(fileName);

     	TFileReader reader= new TFileReader(file);
    	try {
	    	int c;
	    	retrievedData="";
	    	while ((c=reader.read())!=-1) {
	    		retrievedData+=((char)c);
	    	}
    	} finally {
    		reader.close();
    	}
    	
    	if (retrievedData!=null)
    		retrievedData=retrievedData.replaceAll("#fusionApplication#", applicationId);
    	//System.out.println(retrievedData);    	
    	
    	TFileWriter writer=new TFileWriter(file);
    	try {
    		writer.write(retrievedData);
    	} finally {
    		writer.close();    	
    	}
    }
            
}
