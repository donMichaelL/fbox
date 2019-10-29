package org.fbox.fusion.algorithms.siso.ejb.impl;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.fbox.common.IStructure;
import org.fbox.common.algorithms.IAlgorithm;
import org.fbox.common.application.data.DataElement;
import org.fbox.common.data.AlgorithmContext;
import org.fbox.common.data.IAlgorithmContext;
import org.fbox.common.data.IDataElement;
import org.fbox.common.data.InputParameter;
import org.fbox.common.exception.AlgorithmExecutionException;
import org.fbox.common.exception.AlgorithmInitializationException;
import org.fbox.fusion.algorithms.siso.AbstractSISOAlgorithm;


/**
 * Session Bean implementation class Identity
 */
@Stateless (name="Identity")
@Remote( {IAlgorithm.class, IStructure.class })
public class Identity extends AbstractSISOAlgorithm {

    /**
     * Default constructor. 
     */
    public Identity() {
    }

	/**
     * @see IAlgorithm#getRequiredParameters()
     */
    public String[] getRequiredParameters() {
			return null;
    }

    @Override
    protected Comparable<?> _update(IAlgorithmContext state, IDataElement measurement) throws AlgorithmExecutionException{
    	
    	String dataAccessor;
    	String field=(String)state.getContextParameter("access");
    	Comparable<?> valueToReturn=null;
    	
    	if (field==null) {
    		field="value";
    	} 
    	dataAccessor="get"+field.substring(0,1).toUpperCase()+ field.substring(1);
    	
    	
    	Class<?> c =measurement.getClass();
    	Method method;
		try {
			method = c.getDeclaredMethod (dataAccessor);
			Object obj=method.invoke (measurement);
			if (obj!=null && obj instanceof Comparable<?>) {
				valueToReturn=(Comparable<?>)obj;
			}
		} catch (NoSuchMethodException e) {			
			e.printStackTrace();
			throw new AlgorithmExecutionException("Cannot access field: "+field+". Field does not exist");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new AlgorithmExecutionException("Cannot invoke accessor for field: "+field+".");			
		} catch (IllegalArgumentException e) {			
			e.printStackTrace();
			throw new AlgorithmExecutionException("Ivalid arguments passes while invoking accessor for field "+field+".");
		} catch (InvocationTargetException e) {			
			e.printStackTrace();
			throw new AlgorithmExecutionException("Invoking accessor for field "+field+" failed!");
		}
    	return valueToReturn;
    	
    	//A simple forwarding of measurement timestamped
    	//return measurement.getValue();
    }

    @Override
    public void initialize(IAlgorithmContext state,	HashMap<String, InputParameter> iparams) throws AlgorithmInitializationException {
    	super.initialize(state, iparams);
    	
    	InputParameter accessParam=iparams.get("access");
    	if (accessParam!=null) {
    		state.setContextParameter("access", accessParam.getValue());
    	}
    	
    }
    
    @Override
    public String[] getOptionalParameters() {
    	String[] params={"access"};
    	return params;
    }
    
	/**
     * @see IAlgorithm#getType()
     */
    public String getType() {
			return "Identity";
    }

    public static void main(String[] args) {
    	
    	Identity id=new Identity();
    	
		InputParameter access=new InputParameter("access","sequenceNumber");
		HashMap<String, InputParameter> iparams=new HashMap<String, InputParameter>();
		iparams.put(access.getName(), access);

		
		IAlgorithmContext context=new AlgorithmContext("1", "Identity");		
		try {
			id.initialize(context, iparams);
		} catch (AlgorithmInitializationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		IDataElement data;
		data=new DataElement("test1");
		data.setValue(100);				
	
		try {
			System.out.println(id.update(context, data));
		} catch (AlgorithmExecutionException e) {
			e.printStackTrace();
		}
    	
    }
}
