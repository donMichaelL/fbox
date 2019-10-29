package org.fbox.fusion.application.configuration.ejb.impl;

import java.io.File;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.jms.JMSException;
import javax.xml.parsers.ParserConfigurationException;

import org.fbox.common.IFusionConstants;
import org.fbox.common.algorithms.IAlgorithm;
import org.fbox.common.application.configuration.ApplicationInfo;
import org.fbox.common.application.configuration.ApplicationStatus;
import org.fbox.common.application.configuration.IConfigure;
import org.fbox.common.application.data.ContextRegistryEntity;
import org.fbox.common.application.data.ContextorSource;
import org.fbox.common.application.util.IServiceLocator;
import org.fbox.common.data.DataStream;
import org.fbox.common.data.IContextorContext;
import org.fbox.common.data.IOutputContext;
import org.fbox.common.db.ConfigurationParameter;
import org.fbox.common.db.IDBManager;
import org.fbox.common.exception.AdapterInitializationException;
import org.fbox.common.exception.AlgorithmInitializationException;
import org.fbox.common.exception.ApplicationConfigurationException;
import org.fbox.common.exception.EventSourceParserException;
import org.fbox.common.exception.FormatterInitializationException;
import org.fbox.common.exception.ServiceLocatorException;
import org.fbox.common.network.INetworkFusion;
import org.fbox.common.network.data.DataStreamSelector;
import org.fbox.common.output.IAdapter;
import org.fbox.common.output.IFormatter;
import org.fbox.common.registry.IRegistry;
import org.fbox.common.registry.RegistryInsertionError;
import org.fbox.common.xml.data.AdapterDAO;
import org.fbox.common.xml.data.AlgorithmDAO;
import org.fbox.common.xml.data.ApplicationDAO;
import org.fbox.common.xml.data.ContextorDAO;
import org.fbox.common.xml.data.FormatterDAO;
import org.fbox.common.xml.data.OutputDAO;
import org.fbox.common.xml.data.SourceDAO;
import org.fbox.common.xml.data.StreamerSelectorDAO;
import org.fbox.common.xml.parsers.ApplicationSourceParser;
import org.fbox.fusion.application.communication.DataStreamConsumerBean;
import org.fbox.fusion.application.configuration.ConfigurationRegistry;
import org.fbox.fusion.application.output.OutputContextRegistryEntity;
import org.fbox.fusion.application.registry.ContextorRegistry;
import org.fbox.fusion.application.registry.InputOutputMapRegistry;
import org.fbox.fusion.application.registry.OutputRegistry;
import org.fbox.util.HashMapHelper;

import com.javacodegeeks.concurrent.ConcurrentLinkedHashMap;

/**
 * Session Bean implementation class Configurator
 */
@Singleton
@Remote( { IConfigure.class })
@Startup
public class Configurator implements IConfigure { 
	
	@EJB
	InputOutputMapRegistry ioRegistry=new InputOutputMapRegistry();
	@EJB
	OutputRegistry outputRegistry=new OutputRegistry();
	@EJB
	ContextorRegistry contextRegistry=new ContextorRegistry();		

	//the application id 
	ApplicationInfo applicationInfo;	
	
	//A registry used during configuration 
	ConfigurationRegistry configReg;
	
	@EJB(lookup="app/FBoxNetwork/FusionNetwork/NetworkModule!org.fbox.common.network.INetworkFusion", beanInterface=INetworkFusion.class)
	INetworkFusion network;
	@EJB
	DataStreamConsumerBean dataConsumer;	
	@EJB(lookup="app/FBoxCore/FusionCore/ServiceLocator", beanInterface=IServiceLocator.class)
	IServiceLocator serviceLocator;
	@EJB(lookup="app/FBoxCore/FusionCore/ApplicationInfoRegistry", beanInterface=IRegistry.class)
	IRegistry<ApplicationInfo> applicationRegistry;		
	@Resource(lookup="java:app/AppName")
	private String appName;
	@EJB (lookup="app/FBoxCore/FusionCore/DBManager")
	IDBManager fDbManager;

	boolean shouldValidate=false;
		
	
    /**
     * Default constructor. 
     */
    public Configurator() {
    }
    
	@PostConstruct
	private void auto_configure() {			
		
		System.out.print("Bootstrapping  service...");
		String appID=appName.replaceFirst(getEarFilePrefix(), "");
		System.out.print("...with ID="+ appID);
			
		if (!applicationRegistry.getRegistryEntries().containsKey(appID)) {
			System.out.println("Configuring from disk...");
			
			//initialize the application info object
			applicationInfo=new ApplicationInfo();
			applicationInfo.setId(appID);
			applicationInfo.setStatus(ApplicationStatus.DEPLOYED);
			
			//determine the ear filename
			String earToDeploy=appName+".ear";
		  	System.out.println("!!!!!!!!!!!!!!!"+appName);
		  	System.out.println("!!!!!!!!!!!!!!!"+appID);
	  	  		   	
		  	//construct applicationInfo
		  	applicationInfo.setSourceFileName(appName+".xml");
		  	applicationInfo.setDeployedModuleName(earToDeploy);
		  	applicationInfo.setEarFilePath(getDeployPath()+File.separator+earToDeploy);
	    	//build the Application DAO from the xml source
	       	try {
		       	//register to the core Application Registry
		       	applicationRegistry.addEntry(appID, applicationInfo, false);		       	
		       	//initialize configuration
			  	configure(applicationInfo);
	       	} catch (ApplicationConfigurationException | RegistryInsertionError e) {
				e.printStackTrace();
				return;
			}		
	   	} else {
	   		System.out.println("Configuring from interface...");
	   		applicationInfo=applicationRegistry.getEntry(appID);
	   		applicationInfo.setStatus(ApplicationStatus.DEPLOYED);
	   	}							
	}	

	@PreDestroy
	private void auto_unconfigure() {
		unconfigure();
		applicationRegistry.removeEntry(applicationInfo.getId());
	}
	
	private String getSourcesPath() {
		ConfigurationParameter sourcePathParam=fDbManager.getConfigParameter(IFusionConstants.SOURCES_FILE_PATH);
		String sourcePath=sourcePathParam.getValue();
		sourcePath+=sourcePath.endsWith(File.separator)?"":File.separator;
		return sourcePath;
	}
	
	
	private String getDeployPath() {
		String pathToReturn="";
		ConfigurationParameter deployPath=fDbManager.getConfigParameter(IFusionConstants.DEPLOYMENT_PATH);
    	if (deployPath!=null && deployPath.getValue()!=null) {
    		pathToReturn= deployPath.getValue();	
    	} 
    	// return "c:/development/IDIRA/jboss-as-7.1.0.Final/standalone/fusion";
    	return pathToReturn;
	}
	
	private String getEarFilePrefix() {
		String prefix="";
		ConfigurationParameter filePrefix=fDbManager.getConfigParameter(IFusionConstants.APPLICATION_FILE_NAME_PREFIX);
    	if (filePrefix!=null && filePrefix.getValue()!=null) {
    		prefix= filePrefix.getValue();	
    	}  
    	return prefix;
	}

    @Override
	public void configure(ApplicationInfo appInfo) throws ApplicationConfigurationException  {
    	    	
    	configReg=new ConfigurationRegistry();
    	if (appInfo!=null) {
    		applicationInfo=appInfo;
    	}
    	
		if (applicationInfo!=null) {

			//if no model has been configured then try to create it from the source file
			if (applicationInfo.getModel()==null) {
				try {
					//retrieve path from DB			
					String sourcePath=getSourcesPath();
					//String sourcePath="c:/development/IDIRA/jboss-as-7.1.0.Final/standalone/scripts/";		
					applicationInfo.setModel(new ApplicationSourceParser(sourcePath+applicationInfo.getSourceFileName()).parse(shouldValidate));
				} catch (EventSourceParserException	| ParserConfigurationException e) {
					e.printStackTrace();
					throw new ApplicationConfigurationException(e.getMessage());
				}
			} 
			
			//if model loaded successfully then continue with configuring the application 			
			//update status
			applicationInfo.setStatus(ApplicationStatus.STARTING);
			
			//register in configuration registry
			ApplicationDAO applicationSource=applicationInfo.getModel();

			try {
				//retrieve the configuration of DataStreams (StreamSelectorId-->{DataStreams List}) for the application				
				HashMap<String, Set<DataStream>> streamerConfiguration=retrieveStreamers(applicationInfo.getId(),applicationSource.getStreamers());						
				HashMap<String, ContextorDAO> contextorsMap=applicationSource.getContextors();
				HashMap<String, OutputDAO>  outputs=applicationSource.getOutput();
				
				//configReg.setStreamerConfiguration(streamerConfiguration);
				build(contextorsMap, outputs, streamerConfiguration, applicationInfo);											

				System.out.println("-------IN-->OUT--------------");
				new HashMapHelper().printMultiValueMap(configReg.getInitialInputToOutputMap());
				System.out.println("-------OUT-->IN--------------");
				new HashMapHelper().printMultiValueMap(configReg.getInitialOutputToInputMap());
				
				
				
				System.out.println("START----------------------------");
				System.out.println("Output-->Input----------------------------");
				configReg.printOutputToInputContextorMap();
				System.out.println("-------");
				System.out.println("Input-->Output----------------------------");
				configReg.printInputToOutputContextorMap();
				System.out.println("-------");
				System.out.println("Generated----------------------------");
				configReg.printGeneratedContextorMap();
				System.out.println("-------");				
				System.out.println("END----------------------------");
				
				//Finally define the message selector so that DatasStream information could start flowing to the application
				Set<String> dataStreamUniqueIds=getDataStreamIds(streamerConfiguration);
				dataConsumer.subscribeForData(dataStreamUniqueIds); // Define single selector -> single subscriber...
				
			} catch (RegistryInsertionError e) {
				throw new ApplicationConfigurationException("Error while configuring streamers for application with id="+appInfo.getId(), e);
			} catch (JMSException e) {
				throw new ApplicationConfigurationException("Error while configuring message selector for application with id="+appInfo.getId(), e);
			} 
			//update status
			applicationInfo.setStatus(ApplicationStatus.STARTED);
		} else {
			throw new ApplicationConfigurationException("Application Info object is NULL. Unable to configure");
		}
		
	}	   
 
    @Override    
    public boolean unconfigure() {
    	
    	applicationInfo.setStatus(ApplicationStatus.STOPPING);
    	//unsubscribe from queue so that to stop receiving data
		dataConsumer.unsubscribe();
    	
		//retrieve the streamsource Ids
		if (configReg.configuredStreamers!=null) {
			Set<String> datastreamSourceIds=configReg.configuredStreamers.keySet();
    	
			//iterate on datastreamSource Ids and deregister each one from the network
			for (String streamerSetId : datastreamSourceIds) {
				String actualDataStreamSourceId=applicationInfo.getId()+"!"+streamerSetId;
				System.out.println("Deregistering DataStreamSource with ID:"+actualDataStreamSourceId);    	
				if (!network.deRegisterDataStreamSource(actualDataStreamSourceId)) {
					System.out.println("WARNING -----> Unable to deregister datastreamSourceId");
				}		   		
			}
		}
    	configReg.clear();
    	applicationInfo.setStatus(ApplicationStatus.STOPPED);
    	return true; 
    }
    
	
	/**
	 * Transforms the HashMap as produced by the configureStreamers method to a flat set of DataStream IDs 
	 * 
	 * @param streamerConfiguration --> the HashMap containing the  set of DataStreams keyed by the StreamSelectorID
	 * @return
	 */
	private Set<String> getDataStreamIds(HashMap<String, Set<DataStream>> streamerConfiguration) {
	 	
		Set<String> streamerIdsSet=new LinkedHashSet<String>();
	 	Collection<Set<DataStream>> dataSetCollection=streamerConfiguration.values();
	 	
	 	for (Set<DataStream> dataSet: dataSetCollection) {
	 		for (DataStream dStreamer: dataSet) {
	 			streamerIdsSet.add(dStreamer.getId());
	 		}
	 	}
	 	
	 	return streamerIdsSet;
	}
	
	/**
	 * this private method performs communication with the networkFusion module in order to acquire the list of DataStreams
	 * per StreamerSelector 
	 * 
	 * @param applicationID --> The unique ID of the application
	 * @param streamers		--> The HashMap containing the streamsSelectors (keys) along with each select constraints (values)
	 * @return				--> A HashMap populated with the the DataStream list (values) for each StreamSelector ID (string) 
	 * @throws RegistryInsertionError
	 */
	private LinkedHashMap<String, Set<DataStream>> retrieveStreamers(String applicationID, HashMap<String, StreamerSelectorDAO> streamers) throws RegistryInsertionError {
		
		LinkedHashMap<String, Set<DataStream>> streamerConfiguration=new LinkedHashMap<String, Set<DataStream>>();
	 	Set<Entry<String, StreamerSelectorDAO>> streamerSet=streamers.entrySet();
	 	
	 	for (Entry<String, StreamerSelectorDAO> streamer :  streamerSet) {
	 		
	 		//get the id as defined by the user (this should be unique per application)
	 		String streamerSetId=streamer.getKey();

	 		//query the network using the combination [applicationID]![StreamerId] as key for each DataStreamSelector, so that global uniqueness of DataStreamSelector IDs is maintained  
	 		Set<DataStream> retrievedStreamers=network.registerDataStreamSource(applicationID+"!"+streamerSetId, new DataStreamSelector(streamer.getValue()));
	 		
	 		/*for testing*/
	 		/*Set<DataStream> retrievedStreamers=new LinkedHashSet<DataStream>();
	 		for (int i=0;i<5;i++) {
	 		
	 			DataStream ds=new DataStream(streamerSetId+"-"+i, streamerSetId+"!sensor"+i, "Temperature", DataType.NUMERIC);
	 			retrievedStreamers.add(ds);
	 		}*/
	 			 			 		
	 		//add set of DataStreams to the returning HashMap
	 		streamerConfiguration.put(streamerSetId, retrievedStreamers);
	 	}
		
	 	return streamerConfiguration;		
	}


    public IContextorContext configureContextor(ContextorDAO contextorItem) throws ApplicationConfigurationException {
//		System.out.println(contextorItem.getAlgorithm());
//		System.out.println(contextorItem.getMissingValueAlgorithm());
		//create Contextor
		IContextorContext context=new ContextRegistryEntity(contextorItem.getId());
		
		//configure base algorithm 
		AlgorithmDAO alg=contextorItem.getAlgorithm();
		if (alg!=null) {
			IAlgorithm baseAlgorithm;
			try {
				baseAlgorithm = serviceLocator.getAlgorithm(alg.getName());
				context.setBaseAlgorithm(baseAlgorithm);
				//System.out.println("&&&&&&&&&&&&"+context.getBaseAlgorithmContext());
				baseAlgorithm.initialize(context.getBaseAlgorithmContext(), alg.getInputParameters());
				//System.out.println("-----------Initialized--------------");
			} catch (ServiceLocatorException | AlgorithmInitializationException e) {
				throw new ApplicationConfigurationException("Error while configuring base Algorithm for contextor with id="+contextorItem.getId(), e);
			}
		}
				
		//configure missing value algorithm 
		alg=contextorItem.getMissingValueAlgorithm();
		//System.out.println("******************Found Missing Algorithm="+(alg!=null?alg.getName():""));
		if (alg!=null) {
			IAlgorithm missingValueAlgorithm;
			try {
				missingValueAlgorithm=serviceLocator.getAlgorithm(alg.getName());
				context.setMissingAlgorithm(missingValueAlgorithm);
				missingValueAlgorithm.initialize(context.getMissingAlgorithmContext(), alg.getInputParameters());
			} catch (ServiceLocatorException | AlgorithmInitializationException e) {
				throw new ApplicationConfigurationException("Error while configuring missingValue Algorithm for contextor with id="+contextorItem.getId(), e);
			}
			
		}

		//configure IO
		//ArrayList<SourceDAO> sources=contextorItem.getSources();
		
		return context;
	}	

    public IOutputContext configureOutput(OutputDAO outputContextorItem, ApplicationInfo appInfo) throws ApplicationConfigurationException {
    	IOutputContext context=new OutputContextRegistryEntity(outputContextorItem.getId(), appInfo.getId());    	    
		
    	//configure base algorithm 
		FormatterDAO ft=outputContextorItem.getFormatter();
		if (ft!=null) {
			IFormatter<?> formatter;
			try {
				formatter = serviceLocator.getFormatter(ft.getType());
				context.setFormatter(formatter);
				formatter.initialize(context.getformatContext(), ft.getInputParameters());
			} catch (ServiceLocatorException | FormatterInitializationException e) {
				throw new ApplicationConfigurationException("Error while configuring base Algorithm for contextor with id="+outputContextorItem.getId(), e);
			}
		}   
		
		//configure base algorithm 
		AdapterDAO ad=outputContextorItem.getAdapter();
		if (ad!=null) {
			IAdapter adapter;
			try {
				adapter = serviceLocator.getAdapter(ad.getType());
				context.setAdapter(adapter);
				adapter.initialize(context.getAdapterContext(), ad.getInputParameters());
			} catch (ServiceLocatorException | AdapterInitializationException e) {
				throw new ApplicationConfigurationException("Error while configuring base Algorithm for contextor with id="+outputContextorItem.getId(), e);
			}
		}		
    	  	
    	return context;
    }

    
	private void updateRegistryWithStreamers() throws RegistryInsertionError {

		if (configReg.configuredStreamers!=null) {
			Set<Entry<String, Set<DataStream>>> streamerSetAggregation=configReg.configuredStreamers.entrySet();
		 	for (Entry<String, Set<DataStream>> streamerSet :  streamerSetAggregation) {
	
		 		Set<DataStream> retrievedStreamers=streamerSet.getValue();
		 		//for each DataStream add a new entry to the contextRegistry with null algorithm indicating identity
		 		for (DataStream ds : retrievedStreamers) { 
		 			String id=ds.getId();
		 			IContextorContext context=new ContextRegistryEntity(id);
		 			contextRegistry.addEntry(id,context,false);
		 		}
		 	}
		}
	}
	
	private void build(HashMap<String, ContextorDAO> contextorsMap, HashMap<String, OutputDAO>  outputs, HashMap<String, Set<DataStream>> streamerConfiguration, ApplicationInfo appInfo) throws ApplicationConfigurationException, RegistryInsertionError {
		
		configReg.clear(); //clear help registry
		
		//configure streamers
		if (streamerConfiguration!=null) {
			configReg.setStreamerConfiguration(streamerConfiguration);
			updateRegistryWithStreamers();			
		}
		
		buildInitialContextorMap(contextorsMap);
		buildFinalContextorMap(contextorsMap,streamerConfiguration);
		updateContextorAndIORegistry(contextorsMap,streamerConfiguration);
		updateOutputRegistry(outputs, appInfo);		
	}
	
	//creates the finalized contextors maps taking into consideration the streamer configuration as produced by the Network
	private void buildFinalContextorMap(HashMap<String, ContextorDAO> contextorsMap, HashMap<String, Set<DataStream>> streamerConfiguration) throws ApplicationConfigurationException, RegistryInsertionError {
		
		//a temporary list of generated Contextors IDs used while building the finalized lists				
		ConcurrentLinkedHashMap<String, ArrayList<String>> tmpGeneratedMappings=new ConcurrentLinkedHashMap<String, ArrayList<String>>();				
		for (String sId : streamerConfiguration.keySet()) { //Initialize temporary List with the streamers
			ArrayList<String> generatedStreamerIds=getStreamerIds(streamerConfiguration.get(sId));
			tmpGeneratedMappings.put(sId, generatedStreamerIds);
		}	
		
		System.out.println(contextorsMap.keySet());
		for (String sId : contextorsMap.keySet()) { //Initialize temporary List with the streamers		
			
			//if contextor has no sources
			if ((contextorsMap.get(sId)).getSources().isEmpty()) {
				ArrayList<String> generatedStreamerIds=new ArrayList<String>();
				generatedStreamerIds.add(sId);
				tmpGeneratedMappings.put(sId, generatedStreamerIds); //add to temporary list of generated Contextors IDs
				configReg.generatedContextorMap.put(sId, sId);  //add also to generatedContextorMap
			}
				
		}
		
		//contains the index Id for each contextor. All contextors start from Zero(0) but in the end SISO contextors should contain a number greater than zero, while MISO will
		//always contain zero(0)!
		LinkedHashMap<String, Integer> generatedContextorsIndexMap=new LinkedHashMap<String, Integer>();
		
		for (String cdId : contextorsMap.keySet()) {
			generatedContextorsIndexMap.put(cdId, new Integer(0));
		}
		
		//tmpGeneratedMappings contains initially the streamers 
		int i=0;
		while (!tmpGeneratedMappings.isEmpty()) {
			//System.out.println((++i) + "-->"+tmpGeneratedMappings);
			
			Set<String> initialSourceContextorIds=tmpGeneratedMappings.keySet(); 						
			
			int j=0;
			for (String initialSourceContextorId : initialSourceContextorIds) {
				j++;
				System.out.println(j+"-->Checking Contextor: "+initialSourceContextorId);
				if (initialSourceContextorId.equals("LOP")) {
					System.out.println("LOP");
				}
				
				ArrayList<String> newSourceContextorsIdList=tmpGeneratedMappings.remove(initialSourceContextorId);  //remove contextor from list of sources and store it to the designated variable
				
				ArrayList<String> initialTargetContextorIds=configReg.initialInputToOutputContextorMap.get(initialSourceContextorId);  //get destination contextors for contextor with id=initialContextorId
				System.out.println("Destinations="+ initialTargetContextorIds);
				//if no target can be detected for specified source the do nothing, else
				if (initialTargetContextorIds!=null) {
					int k=0;
					//else loop on destination contextors
					System.out.println("Start Parsing destinations...");
					for (String initialTargetContextorId : initialTargetContextorIds) {
						k++;
						//check if targetContextor is multi
						System.out.println("Check Destination #"+k+" ("+initialTargetContextorId+") ...");
						boolean isMulti=configureContextor(contextorsMap.get(initialTargetContextorId)).getBaseAlgorithm().allowsMultipleInputs();
						System.out.println("Destination #"+k+ " allows " + (isMulti?" MULTI":"SINGLE") +"  Inputs");	
						
						if (isMulti) {	
							
							//Add new Target Id to the final targets List for the specific source (newSourceContextorId)
							for (String newSourceContextorId : newSourceContextorsIdList) {
								configReg.finalInputToOutputMap.put(newSourceContextorId, initialTargetContextorId);
							}
														
							//add to final list of generated targets only once
							if (configReg.generatedContextorMap.get(initialTargetContextorId)==null ) {
								
								ArrayList<String> generatedTargetContextorIds=new ArrayList<String>();
								generatedTargetContextorIds.add(initialTargetContextorId);
								tmpGeneratedMappings.put(initialTargetContextorId, generatedTargetContextorIds); //and add it to the generatedMappings Map									
								configReg.generatedContextorMap.put(initialTargetContextorId, initialTargetContextorId);
							}
														
						} else {
							//Create new Target Contextor Id
							Integer targetIndex=generatedContextorsIndexMap.get(initialTargetContextorId)+1; //get next index to assign to target contextor id
							String generatedTargetContextorId=initialTargetContextorId+"@"+(targetIndex); //create the new target contextor id (indexed)
							generatedContextorsIndexMap.put(initialTargetContextorId,targetIndex); //re-store the index for the specific contextor
							
							//Add new Target Id to the final targets List for the specific source (newSourceContextorId)
							for (String newSourceContextorId : newSourceContextorsIdList) {
								configReg.finalInputToOutputMap.put(newSourceContextorId,generatedTargetContextorId);
							}
							
							//update the generatedMappings map with the new Target Contextor Id generated above for the specific target id (initialTargetContextorId)
							ArrayList<String> generatedTargetContextorIds=tmpGeneratedMappings.get(initialTargetContextorId);//retrieve mappings for specified initial target if any
							if (generatedTargetContextorIds==null) { //if no mapping exists then create a empty list of generated ids  
								generatedTargetContextorIds=new ArrayList<String>();
								tmpGeneratedMappings.put(initialTargetContextorId, generatedTargetContextorIds); //and add it to the generatedMappings Map
							}
							//add id to the list of generated ids for the specific Contextor only if not present MISO Case 
							
							generatedTargetContextorIds.add(generatedTargetContextorId);
														
							configReg.generatedContextorMap.put(initialTargetContextorId, generatedTargetContextorId);							
						}
					}
				} //end if (initialTargetContextorIds!=null)						
			} //end for (String initialSourceContextorId : initialSourceContextorIds)													
		} //end while
	}

	private void updateOutputRegistry(HashMap<String, OutputDAO>  outputs, ApplicationInfo appInfo) throws ApplicationConfigurationException, RegistryInsertionError {

		//System.out.println("Outputs:"+outputs);
		configReg.generatedContextorMap.printKeyToValueMap();
		//Define the outputs
		for (OutputDAO outputItem : outputs.values()) {				
			
			int outputIndex=0;
			ArrayList<SourceDAO> outputSources=outputItem.getSources();
			IOutputContext outContext=configureOutput(outputItem, appInfo); //create OutputContext instance
			String outputId=outputItem.getId(); //the initial output id
			boolean isMulti=outContext.getFormater().allowsMultipleInputs(); //check if is a MISO or SISO formatter					
				
			if (isMulti) { //MISO formatter case					
				outputRegistry.addEntry(outputId, outContext, false);	//add outputContext to Output registry only once
				for (SourceDAO sourceItem: outputSources) { //for each source
					ArrayList<String> newSources=configReg.generatedContextorMap.get(sourceItem.getId());	
					//System.out.println("Output:"+ outputId +".Sources to add:"+newSources);
					//iterate on list of generated ids assigned to the specific sourceItem 
					for (String source : newSources) {
						
						ioRegistry.addMapping(new ContextorSource(source, sourceItem.getTimeLimit()), outputId); //add io Mapping of source contextor to specified output						
					}					
				}
				
			} else { //SISO formatter CASE
				for (SourceDAO sourceItem: outputSources) { //for each source
					ArrayList<String> newSources=configReg.generatedContextorMap.get(sourceItem.getId());
					if (newSources==null)
						throw new ApplicationConfigurationException("Source contextor " + sourceItem.getId() +" for Output contextor "+ outputId +" is invalid");
					//System.out.println("Source key:"+ sourceItem.getId() +"new Sources:"+newSources);
					//iterate on list of generated ids assigned to the specific sourceItem
					for (String source : newSources) {
						String generatedOutputId=outputId+"-"+(++outputIndex); //generate a new output contextor id			
						outputItem.setId(generatedOutputId);
						IOutputContext generatedOutContext=configureOutput(outputItem, appInfo); //create OutputContext instance						
						outputRegistry.addEntry(outputItem.getId(), generatedOutContext, false);	//add outputContext to Output registry
						ioRegistry.addMapping(new ContextorSource(source, sourceItem.getTimeLimit()), generatedOutputId); //add io Mapping of source contextor to specified output					
					}								
				}
			}					
		}
	}
		
	//creates the initial mapping based on the configuration file
	private void buildInitialContextorMap(HashMap<String, ContextorDAO> contextorsMap) {
		
		for (ContextorDAO contextorItem : contextorsMap.values()) {
			String contextorId=contextorItem.getId();
			ArrayList<SourceDAO> contextorSources=contextorItem.getSources();
								
			ArrayList<ContextorSource> sources=configReg.initialOutputToInputContextorMap.get(contextorId);
			if (sources==null) {
				sources=new ArrayList<ContextorSource>();
				configReg.initialOutputToInputContextorMap.put(contextorId, sources);
			}
			
			//add source-->destination mapping for each source 
 			for (SourceDAO source : contextorSources) {
 				String sId=source.getId(); 				 			

 					ArrayList<String> destinations=configReg.initialInputToOutputContextorMap.get(sId); //get exiting set of destinations for specific contextor if any

	 				if (destinations==null) {
	 					destinations=new ArrayList<String>();
	 					configReg.initialInputToOutputContextorMap.put(sId, destinations);
	 				}
	 							 				
	 				destinations.add(contextorId);

	 				//add streamer to output-->input Mapping
	 				sources.add(new ContextorSource(sId, source.getTimeLimit()));
 				//}
 			}		 								 
		}				
	}
	
	private void updateContextorAndIORegistry(HashMap<String, ContextorDAO> contextorsMap, HashMap<String, Set<DataStream>> streamerConfiguration) throws ApplicationConfigurationException, RegistryInsertionError {
		
		//retrieve all contextors configuration (id, sources)
		Set<Entry<String, ArrayList<ContextorSource>>> initialContextorConfList=configReg.initialOutputToInputContextorMap.entrySet();																				
		
		//iterate on list of contextor's configurations
		for (Entry<String, ArrayList<ContextorSource>> contextorConf : initialContextorConfList) {
			String rootContextorId=contextorConf.getKey(); //get initial(root) name for contextor
			ArrayList<ContextorSource> initialSourcesList=contextorConf.getValue(); //get initial sources for contextor
			
			ArrayList<String> generatedContextorIdList = configReg.generatedContextorMap.get(rootContextorId); //get generated ids for specific contextor
			ContextorDAO cDao=contextorsMap.get(rootContextorId);		//get DAO object for initial contextor
			
			if (generatedContextorIdList!=null) { 
				//iterate on all generated contextors from this specific initial contextor
				for (String generatedContextorId: generatedContextorIdList) {
					cDao.setId(generatedContextorId);
					IContextorContext cContext=configureContextor(cDao);
					
					contextRegistry.addEntry(generatedContextorId, cContext, false); //add context for the generated ID to the registry
					
					//iterate on sources
					for (ContextorSource initialSource : initialSourcesList) {
						ArrayList<String> generatedSourceIdList;
												
						//if source is a streamer then
						if (streamerConfiguration.containsKey(initialSource.getId())) {								
							generatedSourceIdList=getStreamerIds(streamerConfiguration.get(initialSource.getId()));
						} else { //source is a contextor
							generatedSourceIdList=configReg.generatedContextorMap.get(initialSource.getId()); //retrieve generated ids for specific initial source
						}
						
						//if found generated sources then add them to the IO Registry
						if (generatedSourceIdList!=null) {
							for (String generatedSourceId : generatedSourceIdList) {
								//add source only if it is contained to the list of sources as defined by the finalInputToOutputMap
								
								ArrayList<String> sourcesForGeneratedContextorId=configReg.finalInputToOutputMap.getKey(generatedContextorId);
								if (sourcesForGeneratedContextorId!=null && sourcesForGeneratedContextorId.contains(generatedSourceId)) {
									ContextorSource contextorSourceForGeneratedSourceId=new ContextorSource(generatedSourceId, initialSource.getTimeLimit()); //create a new contxtor source with the new id
									ioRegistry.addMapping(contextorSourceForGeneratedSourceId, generatedContextorId); //add mapping to IO registry
								}
							}
						}
					}
				}
			} else { //case of no sources contextors
				System.out.println("!!!!!!!!!!!!!!!!!!Should add no sources contextor with id:"+rootContextorId +" with destination ?");
			}
		}
	}

	private ArrayList<String> getStreamerIds(Set<DataStream> streamers) {
    	ArrayList<String> result=new ArrayList<String>();
    	for (DataStream ds:streamers) {
    		result.add(ds.getId());
    	}
    	
    	return result;
    }
	
	
	public void clear() {
		contextRegistry.clear();
		outputRegistry.clear();
		ioRegistry.clear();
	}

}
