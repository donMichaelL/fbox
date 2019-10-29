package org.fbox.fusion.algorithms.siso.ejb.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.fbox.common.IStructure;
import org.fbox.common.algorithms.IAlgorithm;
import org.fbox.common.application.data.DataElement;
import org.fbox.common.data.AlgorithmContext;
import org.fbox.common.data.DataType;
import org.fbox.common.data.IAlgorithmContext;
import org.fbox.common.data.IDataElement;
import org.fbox.common.data.InputParameter;
import org.fbox.common.exception.AlgorithmExecutionException;
import org.fbox.common.exception.AlgorithmInitializationException;
import org.fbox.fusion.algorithms.siso.AbstractSISOAlgorithm;


/**
 * Session Bean implementation class ValueMapper
 */
@Stateless(name = "ValueMapper")
@Remote( {IAlgorithm.class, IStructure.class })
public class ValueMapper extends AbstractSISOAlgorithm {

    /**
     * Default constructor. 
     */
    public ValueMapper() {
        // TODO Auto-generated constructor stub
    }

	@Override
	public String getType() {
		return "ValueMapper";
	}

	@Override
	public String[] getRequiredParameters() {
		String[] requiredParams={"type", "interval-configuration", "mapping-values"};
		return requiredParams;
	}

	@Override
	public String[] getOptionalParameters() {
		String[] requiredParams={"map-type", "default"};
		return requiredParams;
	}
	
	/**
	 * The value mapper accepts 3 basic parameters, whose definition is mandatory as follows (Inside the parenthesis the acceptable value or format if no specific values exits):
	 * 1. type (fixed|custom)
	 * 2. interval-configuration (low;high;N | val1;val2;val3;...;valN | val01,val02;val11,val12;val21,va22;...;valN1,valN2).
	 * The three acceptable formats are used as follows:
	 * The first one (low;high;N) is used when type is set to 'fixed'. In this format low defines the low limit of values to map. High the corresponding high limit and N the number of intervals(sets) to create between the two limits
	 * So intervals created with this method will look like this: (low, low+(high-low)/N], (low+(high-low)/N, low+2(high-low)/N)],...,(low+(N-1)(high-low)/N, high].
	 * The other two formats are used in case type is set to 'custom'. In the first case (val1;val2;val3;...;valN) the intervals(sets) to be created are as follows: (val1,val2],(val2,vale3],...(valN-1,valN].
	 * While in the second format val01,val02;val11,val12;val21,va22;...;valN1,valN2 created intervals/sets will be like (val01,val02], (val11,val12],...,(valN1,valN2]
	 * 3. mapping-values (m1;m2;m3;...;mN). where mi values that will be used to map inputs pertaining to interval i as has been defined by the 'interval-configuration' parameter.
	 * The number of map values (N) should match the numbers of intervals created, else an Initialization exception is thrown.   
	 * Both NUMERIC and TEXT(CATEGORY) Map values are supported,
	 * Optional parameters include the following:
	 * 1. map-type (text|numeric), which defines the type of mapping-values (mi). Default mapping assumes mapping0values as text elements.
	 * 2. default (value), which corresponds to a numeric or text value used to map inputs that do not match any of the defined intervals. If not specified a null value is returned in this case.
	 * 
	 */
    public void initialize(IAlgorithmContext state, HashMap<String, InputParameter> iparams) throws AlgorithmInitializationException {
		    			
    	//set initialization runtime parameters
		Set<String> requiredParameters=new HashSet<String>(Arrays.asList(getRequiredParameters()));
		Set<String> optionalParameters=new HashSet<String>(Arrays.asList(getOptionalParameters()));
		
		//mapType defines the type of mapping (acceptable values: fixed, custom)
		String mapType=null;
		//intervals defines the intervals [low, high) which will be used for mapping
		Double[][] intervals=null;
		//the mapped per interval values
		Comparable[] map=null;
		//determine type of mapping
		
		InputParameter parameter=iparams.get("type");
		if (parameter!=null) {
			mapType=parameter.getValue();
			requiredParameters.remove(parameter.getName());			
		} else {
			throw new AlgorithmInitializationException("Required parameter 'type' has not been specified Unable to initialize algorithm properly.");
		}
		
		//create intervals vectors
		parameter=iparams.get("interval-configuration");
		if (parameter!=null) {
			switch (mapType) {
			case "fixed" :		
				String[] intervalConfig=parameter.getValue().split(";");
				if (intervalConfig.length==3) {
					intervals=getMapVector(intervalConfig[0], intervalConfig[1], intervalConfig[2]);					
				} else {
					throw new AlgorithmInitializationException("Non valid format for parameter 'interval-configuration'. For 'type'=" + mapType + " mapping, acceptable format should be as follows: low;high;step");					
				}
				break;
			case "custom":
				intervals=getMapVector(parameter.getValue());				
				break;
			}
			state.setContextParameter("intervals", intervals);
			requiredParameters.remove("interval-configuration");
		} else {
			throw new AlgorithmInitializationException("Required parameter 'interval-configuration' has not been specified Unable to initialize algorithm properly.");			
		}
		
		//create map vector
		parameter=iparams.get("mapping-values");
		if (parameter!=null) {
			map=parameter.getValue().split(";");				
			if (map.length!= intervals.length) {
				throw new AlgorithmInitializationException("Defined 'mapping-values' does not comply with the 'interval-configuration'. Found " + map.length +" map values while expecting "+intervals.length);
			} else {
				parameter=iparams.get("map-type");
				String mapDataType="text";
				if (parameter!=null) {
					mapDataType=parameter.getValue();
				}
				parameter=iparams.get("default");
				String defaultMapping=null;
				if (parameter!=null) {
					defaultMapping=parameter.getValue();
				}
				
				switch (DataType.select(mapDataType)) {
					case NUMERIC:
						state.setContextParameter("map", createNumericVector((String[])map));
						if (defaultMapping!=null) {
							try {
								state.setContextParameter("default", Double.parseDouble(defaultMapping));
							} catch (NumberFormatException e) {
								throw new AlgorithmInitializationException("Invlaid format for 'default' parameter. Expecting double!");
							}
						}
						break;
					case CATEGORY:
						state.setContextParameter("map", (String[])map);
						if (defaultMapping!=null) {
							state.setContextParameter("default", defaultMapping);
						}
						break;
					case SPATIAL:
						throw new AlgorithmInitializationException("SPATIAL 'map-type' is not supported!");
					default:
						throw new AlgorithmInitializationException("Specified 'map-type' is not valid!");						
				}
			}
			requiredParameters.remove("mapping-values");
		} else {
			throw new AlgorithmInitializationException("Required parameter 'mapping-values' has not been specified Unable to initialize algorithm properly.");			
		}
		
			
		//check missing parameters
		String errorMessage="";
		for (String s : requiredParameters) {
			errorMessage+="ERROR --> Input Parameter '"+ s + "' is needed but has not been specified.\n";
		}
		if (!errorMessage.isEmpty()) {
			throw new AlgorithmInitializationException(errorMessage);
		}	
    }
    
    private Double[] createNumericVector(String[] values) throws AlgorithmInitializationException {
    	
    	Double[] result=new Double[values.length];

		for (int i=0;i<values.length;i++) {
			try {
    			result[i]=Double.parseDouble(values[i]);
			} catch (NumberFormatException e) {
				throw new AlgorithmInitializationException("Error in data type ("+ values[i] +"). Expecting Double value");    		
			}
		}
    	
    	return result;
    }
    
    private class Interval {
    	String high;
    	String low;   
    	
    	public Interval(String low, String high) {
    		this.high=high;
    		this.low=low;
    	}

    	public Interval(String[] bounds) {
    		this.high=bounds[0];
    		this.low=bounds[1];
    	}

    	public Double[] toArray() throws AlgorithmInitializationException {
    		Double[] result=new Double[2];
    		try {
    			result[0]=Double.parseDouble(this.low);
    		} catch (NumberFormatException e) {
    			throw new AlgorithmInitializationException("Low Interval boundary "+this.low+") should be double");
    		}
    		try {
    			result[1]=Double.parseDouble(this.high);
    		} catch (NumberFormatException e) {
    			throw new AlgorithmInitializationException("High Interval boundary ("+this.high+") should be double");
    		}	   		
    		return result;
    	}
    }
    
    private Double[][] getMapVector(String intervalMatrix) throws AlgorithmInitializationException {
    	
    	Double[][] intervalsVector=null;
    	
    	String[] intervals=intervalMatrix.split(";");
		
		ArrayList<Interval> intervalList=new ArrayList<Interval>();
		for (int i=0;i<intervals.length;i++) {
			if (intervals[i].indexOf(",")<0) {
				if (i<(intervals.length-1))
					intervalList.add(new Interval(intervals[i], intervals[i+1]));
			} else {				
				 intervalList.add(new Interval(intervals[i].split(",")));
			}
		}
		
		int numOfIntervals=intervalList.size();
		intervalsVector=new Double[numOfIntervals][2];
		for (int i=0;i<numOfIntervals;i++) {
			intervalsVector[i]=intervalList.get(i).toArray();
		}

		return intervalsVector;
    }
    
	private Double[][] getMapVector(String low, String high, String instances) throws AlgorithmInitializationException {
		
		Double lowBound;
		try {
			lowBound=Double.parseDouble(low);
		} catch (NumberFormatException e) {
			throw new AlgorithmInitializationException("Low bound  should be double");
		}
		
		Double highBound;
		try {
			highBound=Double.parseDouble(high);
		} catch (NumberFormatException e) {
			throw new AlgorithmInitializationException("High bound should be double");
		}
		
		Integer n;
		try {
			n=Integer.parseInt(instances);
		} catch (NumberFormatException e) {
			throw new AlgorithmInitializationException("High bound should be integer");
		}
		
		Double[][] intervalsVector=new Double[n][2];
		
		Double step=(highBound-lowBound)/n;				
		for (int i=0;i<n;i++) {
			intervalsVector[i][0]=lowBound+i*step;
			intervalsVector[i][1]=lowBound+(i+1)*step;
		}
		
		return intervalsVector;		
	}

	@Override
	protected Comparable<?> _update(IAlgorithmContext state, IDataElement measurement) throws AlgorithmExecutionException {
		
		//intervals defines the intervals [low, high) which will be used for mapping
		Double[][] intervals=(Double[][])state.getContextParameter("intervals");
		//the mapped per interval values
		Comparable<?>[] map=(Comparable<?>[])state.getContextParameter("map");
		
		if (intervals==null || map==null) {
			throw new AlgorithmExecutionException("Algorithm VaueMapper has not been initialized properly");
		}
		
		Comparable<?>  defaultValue=(Comparable<?>)state.getContextParameter("default");
		
		Comparable<?> value=measurement.getValue();
		if (measurement.getValue() instanceof Double) {
			int index=determineIndex(intervals, (Double)value);
			if (index!=-1) {
				return map[index];
			} else {
				return defaultValue;
			}
		} else {
			throw new AlgorithmExecutionException("Error while executing algorithm. Expecting a Double Value! Exiting algorithm!");
		}
	}

	
	private int determineIndex(Double[][] map, Double value) {
		int result=0;
		
		if (value<=map[0][0] || value>map[map.length-1][1]) {
			result=-1;
		} else {			
			for (int i=0;i<map.length;i++) {
				//System.out.print("value="+value +" checking if in ("+evidenceSets[i][0]+","+evidenceSets[i][1] +"]");
				//The only non covered case is if value==evidenceSets[0][0] where it will return 0 as required
				if (value>map[i][0] && value<=map[i][1])  {//if it is within the specified limits
					result=i;
					//System.out.print("--->success");					
					break;
				}
				//System.out.print("--->failure");
			}
		}
		
		//System.out.println(":Evidence determined===>"+evidenceIndex);
		return result; 
	}
	
	
	public static void main(String args[]) {
		ValueMapper vm=new ValueMapper();
		
		InputParameter type=new InputParameter("type","custom");
		//InputParameter interval=new InputParameter("interval-configuration","0;3;6;9;15;20;25;26;27;28;30");
		//InputParameter interval=new InputParameter("interval-configuration","0.0;0.1;0.5;1.0");
		InputParameter interval=new InputParameter("interval-configuration","0.0;1.0");
		InputParameter maptype = new InputParameter("map-type","NUMERIC");
		//InputParameter interval=new InputParameter("interval-configuration","0;30;10");
		//InputParameter map=new InputParameter("mapping-values","1;2;3;4;5;6;7;8;9;10");
		InputParameter map=new InputParameter("mapping-values","1.0");
		//InputParameter map_type=new InputParameter("map-type","numeric");
		//InputParameter defaultValue=new InputParameter("default", "no");
		HashMap<String, InputParameter> iparams=new HashMap<String, InputParameter>();
		iparams.put(type.getName(), type);
		iparams.put(interval.getName(),interval);
		iparams.put(maptype.getName(), maptype);
		iparams.put(map.getName(), map);
		//iparams.put(map_type.getName(), map_type);
		//iparams.put(defaultValue.getName(), defaultValue);

		
		IAlgorithmContext context=new AlgorithmContext("1", "ValueMapper");
		IDataElement data;
		
		try {
			vm.initialize(context, iparams);
		} catch (AlgorithmInitializationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		double measurement = 0.1;
		System.out.println("value to map -->" + measurement);
		data=new DataElement("test");
		data.setValue(measurement);	
		
		try {
			System.out.println(vm.update(context, data));
		} catch (AlgorithmExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		Random generator = new Random(195804271);
		for(int i = 0; i < 20; i++){

			try {
				Thread.sleep(50);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("\n---------Iteration "+ i+" START");
			double measurement = generator.nextDouble()*40;
			System.out.println("value to map -->" + measurement);
			data=new DataElement("test");
			data.setValue(measurement);				
		

			
			//System.out.print(" observation: {"+data[0].getValue()+"} ");
			try {
				System.out.println(vm.update(context, data));
			} catch (AlgorithmExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("!!!"+context.getData());
			System.out.println("\n---------Iteration "+ i+" END");
		}//fusion for all sensors
		*/


	}
}
