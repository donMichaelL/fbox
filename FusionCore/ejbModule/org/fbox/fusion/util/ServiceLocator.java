package org.fbox.fusion.util;

import javax.annotation.PostConstruct;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.fbox.common.IStructure;
import org.fbox.common.algorithms.IAlgorithm;
import org.fbox.common.application.configuration.IConfigure;
import org.fbox.common.application.util.IServiceLocator;
import org.fbox.common.exception.ServiceLocatorException;
import org.fbox.common.output.IAdapter;
import org.fbox.common.output.IFormatter;
import org.fbox.fusion.persistence.impl.Adapter;
import org.fbox.fusion.persistence.impl.Algorithm;
import org.fbox.fusion.persistence.impl.Formatter;

@Stateless
@Remote ( { IServiceLocator.class })
public class ServiceLocator implements IServiceLocator {

	private InitialContext ctx;
	//private static ServiceLocator self;
	
	//@EJB
	//private IFusionStorageBean fusionStorageBean;

    public ServiceLocator() {
    }
    
    @PostConstruct
    private void init() throws ServiceLocatorException {
    	System.out.println("Service Locator: Initializing...");	    
	    try {
	        ctx = new InitialContext();        
	    } catch (Exception e) {
	            throw new ServiceLocatorException(e);
	    }
    	System.out.println("Service Locator: Initialized");
    }
    
    public static ServiceLocator getInstance() throws ServiceLocatorException {
    /*	if (self==null)
    		self=new ServiceLocator();
    	return self;*/
    	return null;
    }
        
    /**
     * will get the ejb Local home factory. 
     * clients need to cast to the type of EJBHome they desire
     *
     * @return the Local EJB Home corresponding to the homeName
     */
    public IAlgorithm getAlgorithm(String algorithmName) throws ServiceLocatorException {
    	IAlgorithm algorithm=null;
    	try {
    		//System.out.println("Looking up Bean " + algorithmName + "...");
    		Algorithm alg = getIAlgorithmContextMapping(algorithmName, false);
    		if (alg != null)
    			algorithm=(IAlgorithm)ctx.lookup(alg.getJndi());
    		
			//System.out.println("Look up Complete!"+algorithm);
       } catch (NamingException ne) {
//    	   	System.out.println("Look up Failed!");
    	   throw new ServiceLocatorException("ERROR --> Service Locator: Unable to locate algorithm: " + algorithmName +". specific error is: " + ne.getMessage());
       }
       return algorithm;
    }
    
    public IStructure getAlgorithmStructure(String algorithmName) throws ServiceLocatorException {
    	
    	IStructure algorithm=null;
    	try {
    		//System.out.println("Looking up Bean " + algorithmName + "...");
    		Algorithm alg = getIAlgorithmContextMapping(algorithmName, true);
    		if (alg != null)
    			algorithm=(IStructure)ctx.lookup(alg.getJndi());
    		
			//System.out.println("Look up Complete!"+algorithm);
       } catch (NamingException ne) {
    	   	//System.out.println("Look up Failed!");
           throw new ServiceLocatorException("ERROR --> Service Locator: Unable to locate algorithm: " + algorithmName +". specific error is: " + ne.getMessage());
       }
       return algorithm;    	
    }
    
    protected Algorithm getIAlgorithmContextMapping(String name, boolean useStructureInterface) {
   	   
    	Algorithm algorithm = null;
    	algorithm=new Algorithm();
    	algorithm.setAlgorithmId(name);
    	if (useStructureInterface)
    		algorithm.setJndi("java:global/FBoxAlgorithms/FusionAlgorithms/"+name+"!org.fbox.common.algorithms.IStructure");
    	//algorithm.setJndi(" java:app/FusionAlgorithms/"+name+"!org.fbox.common.algorithms.IAlgorithmStructure");
    	
    	else
    		algorithm.setJndi("java:global/FBoxAlgorithms/FusionAlgorithms/"+name+"!org.fbox.common.algorithms.IAlgorithm");
    	return algorithm;
    }
        
    public IAdapter getAdapter(String adapterName) throws ServiceLocatorException {
    	IAdapter adapter = null;
    	try {
    		//System.out.println("Looking up Bean " + adapterType + "...");
    		Adapter ad = getIAdapterContextMapping(adapterName);
    		if (ad != null)
    			adapter = (IAdapter)ctx.lookup(ad.getJndi());
    		
			//System.out.println("Look up Complete!");
       } catch (NamingException e) {
    	    //System.out.println("Look up Failed!");
            throw new ServiceLocatorException("ERROR --> Service Locator: Unable to locate adapter: " + adapterName +". specific error is: " + e.getMessage());

       }
    	
       return adapter;
    }    
    
    protected Adapter getIAdapterContextMapping(String adapterName) {
   	    
    	Adapter adapter = null;
    	  
    	adapter=new Adapter();
    	adapter.setAdapterId(adapterName);
    	adapter.setJndi("java:global/FBoxAlgorithms/FusionAlgorithms/"+adapterName+"!org.fbox.common.output.IAdapter");
    	return adapter;
    }

	@Override
	public IConfigure getConfigurator(String applicationName) throws ServiceLocatorException {
		String jndiLookupEntry="/FusionApplication/Configurator";		
    	try {
			return (IConfigure)ctx.lookup("java:global/"+ applicationName +jndiLookupEntry);
		} catch (NamingException e) {			
			throw new ServiceLocatorException(e);
		}
    }

	@Override
	public String getXsltConfiguration(String xsltConfId) {
		//TODO
		//Formatter formatter = fusionStorageBean.getXslt(xsltConfId);
		
		//return formatter!=null?formatter.getXslt():null;
		return null;
	}

	
	@Override
	public IFormatter<?> getFormatter(String formatterName) throws ServiceLocatorException {
		IFormatter<?> formatter = null;
	
    	try {
    		//System.out.println("Looking up Bean " + formatterName + "...");
    		Formatter ft = getIFormatterContextMapping(formatterName);
    		if (ft != null)
    			formatter = (IFormatter<?>)ctx.lookup(ft.getJndi());
    		
			//System.out.println("Look up Complete!");
       } catch (NamingException e) {
    	    //System.out.println("Look up Failed!");
           throw new ServiceLocatorException("ERROR --> Service Locator: Unable to locate adapter: " + formatterName +". specific error is: " + e.getMessage());
       }
    	
       return formatter;
	}	

	
    protected Formatter getIFormatterContextMapping(String formatterName) throws ServiceLocatorException {
   	    
    	Formatter formatter = null;
  
      	formatter=new Formatter();
      	formatter.setFormatterId(formatterName);
      	formatter.setJndi("java:global/FBoxAlgorithms/FusionAlgorithms/"+formatterName+"!org.fbox.common.output.IFormatter");
		
    	return formatter;
    }
    
}
