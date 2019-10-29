package org.fbox.fusion.algorithms.siso.ejb.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.fbox.common.IStructure;
import org.fbox.common.algorithms.IAlgorithm;
import org.fbox.common.data.DataType;
import org.fbox.common.data.IAlgorithmContext;
import org.fbox.common.data.IDataElement;
import org.fbox.common.data.InputParameter;
import org.fbox.common.exception.AlgorithmExecutionException;
import org.fbox.common.exception.AlgorithmInitializationException;
import org.fbox.fusion.algorithms.siso.AbstractSISOAlgorithm;
import org.fbox.fusion.algorithms.siso.ejb.impl.threshold.ComputationType;
import org.fbox.fusion.algorithms.siso.ejb.impl.threshold.Operator;

import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * Session Bean implementation class Threshold
 */
@Stateless (name="Threshold")
@Remote( {IAlgorithm.class, IStructure.class })
public class Threshold extends AbstractSISOAlgorithm {

    /**
     * Default constructor. 
     */
    public Threshold() {
    }

    @Override
    public String[] getRequiredParameters() {
		String[]  parameters={"operator", "value"};
		return parameters;
    }
    
    @Override
	public String[] getOptionalParameters() {
		String[] params={"data-type", "soft-base", "computation-type"};
		return params;
	}

	/**
     * @throws AlgorithmInitializationException 
	 * @see IAlgorithm#initialize(IExecutionContext, ArrayList<InputParameter>)
     */
    public void initialize(IAlgorithmContext state, HashMap<String, InputParameter> iparams) throws AlgorithmInitializationException {
		
    	//initialize metric
    	//state.getData().setValue(new Double(0.0)); //initialize metric as double
    			
    	//set initialization runtime parameters
		Set<String> requiredParameters=new HashSet<String>(Arrays.asList(getRequiredParameters()));
		Set<String> optionalParameters=new HashSet<String>(Arrays.asList(getOptionalParameters()));
		
		
		state.setContextParameter("data-type", DataType.NUMERIC); //default value could be changed by optional parameter
		state.setContextParameter("soft-base", 0.5); //default value could be changed by optional parameter
		state.setContextParameter("computation-type", ComputationType.IDENTITY); //default value could be changed by optional parameter
		
		Collection<InputParameter> paramValues=iparams.values();
		for (InputParameter param : paramValues) {
			String paramName=param.getName();
			switch (paramName) { 
			case "operator": 
				 Operator operator=Operator.select(param.getValue());
				 state.setContextParameter("operator",operator);
				 requiredParameters.remove(paramName);
				 break;
			case "value":
				state.setContextParameter("value", param.getValue());
				requiredParameters.remove(paramName);
				break;
			default:
				if (optionalParameters.contains(paramName)) {
					switch (paramName) { 
					case "data-type":
						DataType type=DataType.select(param.getValue());
						if (type!=null) {
							state.setContextParameter("data-type", type);	
						} else
							throw new AlgorithmInitializationException("Non valid data-type attribute specified");
						break;
					case "soft-base": state.setContextParameter("soft-base", Double.parseDouble(param.getValue()));
									 break;
					case "computation-type":
						ComputationType cType=ComputationType.select(param.getValue());
						if (cType!=null) {
							state.setContextParameter("computation-type", cType);	
						} else
							throw new AlgorithmInitializationException("Non valid computation-type attribute specified");
						break;
					}		
				} else
					System.out.println("WARNING --> Input Parameter '"+ paramName + "' not applicable for Operator " + getType() +". Will be Ignored.");
			}			
		}
		
		
		//check missing parameters
		String errorMessage="";
		for (String s : requiredParameters) {
			if (s.equals("type")) {//optional parameter -default value numeric
				state.setContextParameter("type", DataType.NUMERIC);	
			} else				
				errorMessage+="ERROR --> Input Parameter '"+ s + "' is needed but has not been specified.\n";
		}
		if (!errorMessage.isEmpty()) {
			System.out.println(errorMessage);
			throw new AlgorithmInitializationException(errorMessage);
		}	
    }

	/**
     * @see IAlgorithm#getType()
     */
    public String getType() {
		return "Threshold";
    }

    @Override
    public Comparable<?> _update(IAlgorithmContext state, IDataElement measurement) throws AlgorithmExecutionException {
		Comparable<?> valueToReturn=null;
		DataType type= (DataType)state.getContextParameter("data-type");
		
		//System.out.println("Data to parse="+measurement);
		//System.out.println("type="+type);
		
		Comparable<?> tValue=null;
		switch (type) {
		case NUMERIC: 	tValue=Double.parseDouble((String)state.getContextParameter("value"));
						break;
		case SPATIAL: 	try {
				tValue=new WKTReader().read((String)state.getContextParameter("value"));
			} catch (ParseException e) {				
				e.printStackTrace();
				throw new AlgorithmExecutionException("Unable to parse Geometry String definition");
			}
						break;
		case CATEGORY: 	tValue=(String)state.getContextParameter("value");
						break;
		}
				
		Operator operator=(Operator)state.getContextParameter("operator");
		if (measurement!=null) {
			Comparable value=measurement.getValue();
			//System.out.println("value="+value);			
			if (value!=null) {
				switch(operator) {
					case GREATER_THAN:	//System.out.println(value+">"+tValue);
										//System.out.println(value.getClass());
										//System.out.println(tValue.getClass());
										if (value.compareTo(tValue)>0) 
											valueToReturn=value;
										//System.out.println("valueToReturn="+valueToReturn);
										break;
					case LESS_THAN: if (value.compareTo(tValue)<0) 
									valueToReturn=value;
									break;
					case EQUAL:		if (value.compareTo(tValue)==0) 
									valueToReturn=value;
									break;
					case NOT_EQUAL:	if (value.compareTo(tValue)!=0) 
									valueToReturn=value;
									break;
					case NONE: valueToReturn=value;
								break;				
				}
			}
		}
		
		if (type==DataType.NUMERIC) {
			valueToReturn=applyComputation((Double)valueToReturn, (Double)tValue, state);
		} 
		
		//System.out.println("!#####################Value to return is="+ valueToReturn + "########################");
				
		//System.out.println("valueToReturn="+valueToReturn);
		
		//state.getData().setTimestamp(measurement.getTimestamp()); //operator maintains timestamping of input
		
		return valueToReturn;
    }

	private Double applyComputation(Double value, Double tValue, IAlgorithmContext state) {
		ComputationType cType=(ComputationType)state.getContextParameter("computation-type");
		Double valueToReturn=null;
		switch (cType) {
		case IDENTITY:	valueToReturn=value;
						break;
		case HARD:	if (value!=null) 
						valueToReturn=1.0;
					else
						valueToReturn=0.0;
					break;
		case SOFT:	Double softBase=(Double)state.getContextParameter("soft-base");
					if (value!=null) 
						valueToReturn=(value-tValue)/tValue*softBase;
					else
						valueToReturn=0.0;
					break;
		}

		return valueToReturn;	
	}
    
}
