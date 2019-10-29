package org.fbox.fusion.application.algorithms.invoker.ejb.impl;

import java.util.ArrayList;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.fbox.common.application.configuration.ApplicationInfo;
import org.fbox.common.application.data.ContextorSource;
import org.fbox.common.data.IContextorContext;
import org.fbox.common.data.IDataElement;
import org.fbox.common.data.IOutputContext;
import org.fbox.common.exception.AlgorithmExecutionException;
import org.fbox.common.exception.FormatterException;
import org.fbox.common.exception.OutputAdapterException;
import org.fbox.common.registry.IRegistry;
import org.fbox.common.registry.RegistryInsertionError;
import org.fbox.fusion.application.algorithms.invoker.IContextorExecute;
import org.fbox.fusion.application.communication.DataElementProviderBean;
import org.fbox.fusion.application.exception.ContextNotFoundException;
import org.fbox.fusion.application.registry.ContextorRegistry;
import org.fbox.fusion.application.registry.InputOutputMapRegistry;
import org.fbox.fusion.application.registry.OutputRegistry;

/**
 * Session Bean implementation class SimpleAlgorithmInvoker
 */
@Stateless
@Local( { IContextorExecute.class })
@LocalBean
public class ContextorExecutor implements IContextorExecute {

	@EJB
	DataElementProviderBean provider;
	@EJB
	InputOutputMapRegistry ioRegistry;
	@EJB
	ContextorRegistry cRegistry;
	@EJB
	OutputRegistry oRegistry;
	
	/** NEW **/
	//In order to load the ApplicationRegistry and update the lastReportTime
	@EJB(lookup="app/FBoxCore/FusionCore/ApplicationInfoRegistry", beanInterface=IRegistry.class)
	IRegistry<ApplicationInfo> applicationRegistry;
	//@EJB
	//IRegistry<ApplicationInfo> applicationRegistry;
	
	/**
     * Default constructor. 
     */
    public ContextorExecutor() {
    }

    
	/**
	 * Executes the Contextor with id=targetContextor. Data for the calculations of the output are retrieved from sources as 
	 * have been defined i the IORegistry 
	 * 
     * @throws AlgorithmExecutionException 
	 * @throws ContextNotFoundException 
	 * @throws FormatterException 
	 * @throws OutputAdapterException 
	 * @see IContextorExecute#update(MeasurementMessage)
     */
    @Override    
    public void update(String targetContextor) throws AlgorithmExecutionException, ContextNotFoundException, OutputAdapterException, FormatterException, RegistryInsertionError {

    	//collect data from all sources for the specific destination(target) Contextor
		IDataElement[] dataCollected=collectData(ioRegistry.getSources(targetContextor));  
		// System.out.println("Collected --->" +dataCollected.length +" data elements");
		update(targetContextor, dataCollected);  	
    }
    

	/**
	 * Executes the Contextor with id=targetContextor, using the specified data as input
	 * 
     * @throws AlgorithmExecutionException 
	 * @throws ContextNotFoundException 
	 * @throws FormatterException 
	 * @throws OutputAdapterException 
	 * @throws RegistryInsertionError 
	 * @see IContextorExecute#update(MeasurementMessage)
     */
    public void update(String targetContextor, IDataElement... data) throws AlgorithmExecutionException, ContextNotFoundException, OutputAdapterException, FormatterException, RegistryInsertionError {        	    	
		//get context
        IContextorContext contextToInvoke=cRegistry.getEntry(targetContextor);
        if (contextToInvoke!=null) { //if context to invoke is not null then execute contexter
        	//invoke algorithm
        	
        	//long sequenceLast=contextToInvoke.getBaseAlgorithmContext().getData().getSequenceNumber();
        	IDataElement producedData=contextToInvoke.updateContext(data);
        	
        	//removed because it resulted to messages not delivered in queue as expected, as contextor's levels increased.
        	//if (contextToInvoke.getBaseAlgorithmContext().getData().getSequenceNumber()>sequenceLast) //put data back to queue only if new data has been generated

        	provider.addDataInQueue(targetContextor, producedData);
        }
        
        //check also if an output exists for specified target
        IOutputContext outputToInvoke=oRegistry.getEntry(targetContextor);
        if (outputToInvoke!=null) { 
       		outputToInvoke.setData(data); //single output is supported
       		
       		/** NEW **/ //Update application registry
       		//Since the setData has been successfully executed, the lastReportTime timestamp must be updated
       		//System.out.println("\n[DEBUG]: Update registry for deployed app!! \n");
       		
       		ApplicationInfo currentApp = applicationRegistry.getEntry(outputToInvoke.getAppID());
       		
       		//System.out.println("\n[DEBUG]: AppID -> "+currentApp.getId()+"\n");
       		//System.out.println("\n[DEBUG]: Old report -> "+currentApp.getLastReportTime().getTime()+"\n");

       		currentApp.updateStats(new Date());
       		
       		//System.out.println("\n[DEBUG]: Last report -> "+currentApp.getLastReportTime().getTime()+"\n\n");
       		
       		applicationRegistry.addEntry(currentApp.getId(), currentApp, true);
       	}

        //if both are null display a message
    	if (contextToInvoke==null & outputToInvoke==null) {
    		System.out.println("############# No context mapped to specific GUID:"+targetContextor);
        } 
    }
    
    /**
     * Collects output data as it has been produced from the specified list of sources to an Array of IDataElement
     * 
     * @param srcGuids
     * @return
     * @throws ContextNotFoundException
     */
    private IDataElement[] collectData(ArrayList<ContextorSource> srcList) throws ContextNotFoundException {
    	
    	ArrayList<IDataElement> dataList=null;
    	
    	if (srcList!=null) {
    		dataList=new ArrayList<IDataElement>();
	    	
			for (ContextorSource src : srcList) {
				IContextorContext context=cRegistry.getEntry(src.getId());
				if (context==null) {
					throw new ContextNotFoundException();
				} else {
					dataList.add(context.getValidData(src.getTimeLimit()));
				}
			}
    	}
    	
    	return dataList.toArray(new IDataElement[dataList.size()]);
    }    

}
