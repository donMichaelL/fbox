//*************************************//
//*   SWeFS custom implementation     *//
//*************************************//

package org.fbox.fusion.algorithms.siso.ejb.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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

@Stateless (name="MapTileIDs")
@Remote( {IAlgorithm.class, IStructure.class })

public class TileIDsMapper extends AbstractSISOAlgorithm {

	 /**
     * Default constructor. 
     */
	public TileIDsMapper () {
	}
	
	@Override
    public String[] getRequiredParameters() {
		String[] parameters={"tileIDs"};
		return parameters;
    }
	
	 /**
     * @see IAlgorithm#getType()
     */
    public String getType() {
		return "MapTileIDs";
    }
	
	/**
     * @throws AlgorithmInitializationException 
	 * @see IAlgorithm#initialize(IExecutionContext, ArrayList<InputParameter>)
     */
    public void initialize(IAlgorithmContext state, HashMap<String, InputParameter> iparams) throws AlgorithmInitializationException {
				
    	//set initialization runtime parameters
		Set<String> requiredParameters=new HashSet<String>(Arrays.asList(getRequiredParameters()));
		
		Collection<InputParameter> paramValues=iparams.values();
		for (InputParameter param : paramValues) {
			String paramName=param.getName();
			switch (paramName) { 
				case "tileIDs":
					state.setContextParameter("tileIDs", param.getValue());
					requiredParameters.remove(paramName);
					break;
				default:
					System.out.println("WARNING --> Input Parameter '"+ paramName + "' not applicable for Operator " + getType() +". Will be Ignored.");
			}			
		}
		
		//check missing parameters
		String errorMessage="";
		for (String s : requiredParameters) {
			errorMessage+="ERROR --> Input Parameter '"+ s + "' is needed but has not been specified.\n";
		}
		if (!errorMessage.isEmpty()) {
			System.out.println(errorMessage);
			throw new AlgorithmInitializationException(errorMessage);
		}	
    }

    @Override
    //This algorithm returns a string that encapsulates the tileID along with the fire probability assigned to it 
    protected Comparable<?> _update(IAlgorithmContext state, IDataElement measurement) throws AlgorithmExecutionException { //operates on double measurements

    	String outValue; 
    	Comparable<?> mValue=measurement.getValue();

    	if (mValue!=null && mValue instanceof Number) { //if a double value has come		
    		
    		//We have to check if the received value corresponds to a probability
    		if(((Double)mValue > 1.0) || ((Double)mValue < 0.0)) {
    			String errorMessage="Algorithm cannot operate on a value that is not a probability";
        		//System.out.println(errorMessage);
        		throw new AlgorithmExecutionException(errorMessage);
    		}
    		
    		outValue = state.getContextParameter("tileIDs").toString() + "," + (Double)mValue; 		
    	} else {
    		String errorMessage="Algorithm cannot operate on a non numeric or null value";
    		System.out.println(errorMessage);
    		throw new AlgorithmExecutionException(errorMessage);
    	}

    	//System.out.println("[TileIDMapper] OUTPUT: "+outValue);
    	return outValue;
    }
    
    //Test implemented algorithm
    public static void main(String args[]) {
    	
		TileIDsMapper idMapper = new TileIDsMapper();

		InputParameter tileID = new InputParameter("tileIDs","area_tile_3_6|area_tile_4_3|area_tile_4_4|area_tile_4_5");
		HashMap<String, InputParameter> iparams=new HashMap<String, InputParameter>();
		iparams.put(tileID.getName(), tileID);
		
		IAlgorithmContext context = new AlgorithmContext("1", "MapTileIDs");
		IDataElement data;
		
		try {
			idMapper.initialize(context, iparams);
		} catch (AlgorithmInitializationException e1) {
			e1.printStackTrace();
		}
		
		double measurement = 0.23;
		System.out.println("Value to encapsulate -->" + measurement);
		data = new DataElement("test");
		data.setValue(measurement);	
		
		try {
			System.out.println(idMapper.update(context, data));
		} catch (AlgorithmExecutionException e) {
			e.printStackTrace();
		}
	}
}
