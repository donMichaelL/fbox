package org.fbox.fusion.algorithms.miso.ejb.impl;

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
import org.fbox.fusion.algorithms.miso.AbstractMISOAlgorithm;
import org.fbox.fusion.algorithms.siso.ejb.impl.ValueMapper;

/**
 * Session Bean implementation class Average
 */
@Stateless(name="Average")
@Remote( { IAlgorithm.class, IStructure.class })
public class Average extends AbstractMISOAlgorithm {
	
	/**
     * Default constructor. 
     */
    public Average() {
    }
    
    public String[] getRequiredParameters() {
    	String[] requiredParams={"type"}; //Define the type of the mean algorithm (hard or soft)
		return requiredParams; 
	}
    
    @Override
    public void initialize(IAlgorithmContext state, HashMap<String, InputParameter> iparams) throws AlgorithmInitializationException {

    	Set<String> requiredParameters=new HashSet<String>(Arrays.asList(getRequiredParameters()));
		
		//intialize execution params
		Collection<InputParameter> paramValues=iparams.values();
		for (InputParameter param : paramValues) {
			switch (param.getName()) { 
				case "type":
					
					String type = (String)param.getValue();
					
					// Check if we have a valid input for "type" parameter
					if(!type.equalsIgnoreCase("hard") && !type.equalsIgnoreCase("soft")) {
							throw new AlgorithmInitializationException("[AV] ERROR: \""+type+"\" is not a valid algorithm's type. Type can only be \"hard\" or \"soft\"");	
					} else {
						state.setContextParameter("type", type); 
						requiredParameters.remove(param.getName());
					}
					
					break;
				default:
					System.out.println("[AV] WARNING: Input Parameter '"+ param.getName() + "' not applicable for Detection Algorithm " + getType() +". Will be Ignored.");
			}
		}
		
		for (String s : requiredParameters) {
			System.out.println("[AV] ERROR: Input Parameter '"+ s + "' is needed but has not been specified.");
		}		
		
		//System.out.println("[AV] Succesfully Initialized!!");
    }
    
    @Override
    protected Comparable<?> _update(IAlgorithmContext state, IDataElement[] measurementList) throws AlgorithmExecutionException {
    	
    	//Count the input values
    	double avValue = 0.0;
    	int inputs = measurementList.length; 
    	boolean rightInputs = true;
    	String type = (String)state.getContextParameter("type"); //Retrieve algorithm's type
    	
    	//Check values that come inside the formatter
    	for(int i=0; i<measurementList.length ; i++) {
    		if(measurementList[i] == null || measurementList[i].getValue() == null) {
    			rightInputs = false;  //If one input is empty
    			
    			if(type.equalsIgnoreCase("hard"))
    				break;
    		}
    	}
    	
    	if(type.equalsIgnoreCase("hard")) { //All input streams must give a value
    		if(!rightInputs) {
    			//throw new AlgorithmExecutionException(getType() +" algorithm in "+type+" mode--> All input streams must be present and valid");
    			System.out.println("[AV] WARNING: Algorithm in "+type+" mode--> All input streams must be present and not \"null\"");
    			return null;
    		}
    		//Input streams must be more than 1 in "hard" mode of MultiMean algorithm
        	if(inputs < 2) {
        		//throw new AlgorithmExecutionException("Algorithm "+getType()+" must have 2 or more input streams in "+type+" mode");	
        		System.out.println("[AV] WARNING: Algorithm must have 2 or more input streams in "+type+" mode");
    			return null;
        	}
        	
        	//If all input streams have a value, check if they are all numbers
    		for (IDataElement d : measurementList) {
				Comparable<?> value=d.getValue();

				if (value instanceof Number) {
					avValue = avValue + (Double)value;			
				} else {
					//throw new AlgorithmExecutionException(getType() +" algorithm in "+type+" mode--> Invalid data value detected("+value+"). Value must be Double");
					System.out.println("[AV] WARNING: Algorithm in "+type+" mode--> Invalid data value detected("+value+"). Value must be Double");
	    			return null;
				}
			}
    	} else { //Take into consideration only the available (not null) input streams as long as they are more than 0
    		if(inputs == 0) { //If there is no input in current condition
    			//throw new AlgorithmExecutionException(getType() +" algorithm in "+type+" mode--> We must have at least 1 input stream");
    			System.out.println("[AV] WARNING: Algorithm in "+type+" mode--> At least 1 input stream must have a not \"null\" value");
    			return null;
    		}
    		
    		//If some input streams have a value, check if they are all numbers
    		for (IDataElement d : measurementList) {
				
    			if(d!=null && d.getValue()!=null) { //Do not take into account "null" input streams
    				Comparable<?> value=d.getValue();

    				if (value instanceof Number) {
    					avValue = avValue + (Double)value;		
    				} else {
    					//throw new AlgorithmExecutionException(getType() +" algorithm in "+type+" mode--> Invalid data value detected("+value+"). Value must be Double");
    					System.out.println("[AV] WARNING: Algorithm in "+type+" mode--> Invalid data value detected("+value+"). Value must be Double");
    	    			return null;
    				}
    			}
    		}
    	}
    	
    	avValue =  avValue * (1.0 / (double) inputs);
    	
    	//System.out.println("[AV] OUTPUT: "+avValue);
		return avValue;
    }

    @Override
    public String getType() {
      return "Average";
    }
    
    @Override
	public boolean allowsMultipleInputs() {
		return true;
	}
    
    //Testing
    public static void main(String[] argv){
    	
    	Average mm=new Average();
    	
    	InputParameter type=new InputParameter("type","hard");
    	HashMap<String, InputParameter> iparams=new HashMap<String, InputParameter>();
    	iparams.put(type.getName(), type);
    	
    	IAlgorithmContext context=new AlgorithmContext("1", "Average");
    	IDataElement [] data = new IDataElement[5];
    	
    	try {
			mm.initialize(context, iparams);
		} catch (AlgorithmInitializationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	//Create values
    	data[0]=new DataElement("input0");
		data[0].setValue(new Double(3.0));
		data[1]=new DataElement("input1");
		data[1].setValue(new Double(3.0));
		data[2]=new DataElement("input2");
		data[2].setValue(new Double(4.0));
		data[3]=new DataElement("input3");
		data[3].setValue(null);
		data[4]=new DataElement("input4");
		data[4].setValue(null);
		
		try {
			System.out.println(mm.update(context, data));
		} catch (AlgorithmExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
}
