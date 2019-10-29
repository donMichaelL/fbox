package org.fbox.fusion.output.formatter.impl;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.fbox.common.IStructure;
import org.fbox.common.algorithms.IAlgorithm;
import org.fbox.common.application.data.DataElement;
import org.fbox.common.data.AlgorithmContext;
import org.fbox.common.data.IAlgorithmContext;
import org.fbox.common.data.IContext;
import org.fbox.common.data.IDataElement;
import org.fbox.common.data.InputParameter;
import org.fbox.common.exception.AlgorithmExecutionException;
import org.fbox.common.exception.AlgorithmInitializationException;
import org.fbox.common.exception.FormatterException;
import org.fbox.common.exception.FormatterInitializationException;
import org.fbox.common.output.IFormatter;
import org.fbox.fusion.algorithms.siso.AbstractSISOAlgorithm;
import org.fbox.fusion.algorithms.siso.ejb.impl.AsciiRaster;
import org.fbox.fusion.output.formatter.AbstractFormatter;
/*
@Stateless (name="asciiRasterDualSISO")
@Remote ({IFormatter.class})
public class AsciiRasterDualSISOFormatter extends AbstractFormatter<String> {

    //@EJB
	private static AsciiRasterSISOFormatter sisoForm1;

	//@EJB
	private static AsciiRasterSISOFormatter sisoForm2;
	
	private static IAlgorithmContext state1;
	private static IAlgorithmContext state2;
	
	@Override
	public void initialize(IContext state, HashMap<String, InputParameter> iparams) throws FormatterInitializationException {
		
		HashMap<String, InputParameter> iparams1 = new HashMap<String, InputParameter>(); //For the binary version
		HashMap<String, InputParameter> iparams2 = new HashMap<String, InputParameter>(); //For the probabilistic version
		state1 = new AlgorithmContext("2", "asciiRasterSISO");
		state2 = new AlgorithmContext("3", "asciiRasterSISO");
		
		//We have to build the parameters for the formatters
		
		//initialize params
		String[] params=getRequiredParameters();
		if (params!=null) { //parse required params

			Set<String> requiredParameters=new HashSet<String>(Arrays.asList(params));
			
			Collection<InputParameter> paramValues=iparams.values();
			
			//Check for parameters that do not belong in the mandatory list
			for (InputParameter param : paramValues) {
				String paramName=param.getName();
				if (!requiredParameters.contains(paramName)) 
					System.out.println("WARNING --> Init Parameter '"+ paramName + "' not applicable for Formatter " + getType() +". Will be Ignored.");
			}
			
			for (InputParameter param : paramValues) {
				String paramName=param.getName();
				if (requiredParameters.remove(paramName)) {
					
					if(paramName.equalsIgnoreCase("binary")){
						if(Boolean.parseBoolean(param.getValue())) {
							iparams1.put(paramName, param); //We put the boolean param only for the firstformatter
							iparams2.put(paramName, new InputParameter(paramName,"false"));
							
							state1.setContextParameter(paramName, param.getValue());
							state2.setContextParameter(paramName, "false");
						}
					}
					else if(paramName.equalsIgnoreCase("threshold")) {
						iparams1.put(paramName, param);
					
						state1.setContextParameter(paramName, param.getValue());
					}
					else if(paramName.equalsIgnoreCase("id")) {
						iparams1.put(paramName, new InputParameter(paramName,param.getValue() + "_binary"));
						iparams2.put(paramName, new InputParameter(paramName,param.getValue() + "_probabilistic"));
						
						state1.setContextParameter(paramName, param.getValue() + "_binary");
						state2.setContextParameter(paramName, param.getValue() + "_probabilistic");
					}
					else {
						iparams1.put(paramName, param);
						iparams2.put(paramName, param);
						
						state1.setContextParameter(paramName, param.getValue());
						state2.setContextParameter(paramName, param.getValue());
					}
				}
			}
		}
		
		sisoForm1 = new AsciiRasterSISOFormatter();
		sisoForm2 = new AsciiRasterSISOFormatter();
		
		try {
			sisoForm1.initialize(state1, iparams1);
			sisoForm2.initialize(state2, iparams2);
		} catch (FormatterInitializationException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public String format(IContext state, IDataElement... data) throws FormatterException {
		
		String raster1 = sisoForm1.format(state1, data);
		String raster2 = sisoForm2.format(state2, data);
		
		if(raster1 == null || raster2 == null) // Based on the value of "output-interval" now we may have such cases
			return null;
		else
			return (raster1 + "|" + raster2);
	}
	
	@Override
	public String[] getRequiredParameters() {
		String[] params={"tileIDs","id","rows","columns","binary","threshold","output-interval"};
		return params;
	}
	
	@Override
	public String getType() {
		return "asciiRasterDualSISO";
	}

	@Override
	public boolean allowsMultipleInputs() {
		return true;
	}
	
	//Test implemented formatter
    public static void main(String args[]) {
    	
    	AsciiRasterDualSISOFormatter dualSISO = new AsciiRasterDualSISOFormatter();
    	
    	InputParameter tileIDs = new InputParameter("tileIDs","area_tile_2_4,area_tile_12_2,area_tile_5_9");
		InputParameter id = new InputParameter("id","FusionRaster");
		InputParameter rows = new InputParameter("rows","201");
		InputParameter columns = new InputParameter("columns","201");
		InputParameter binary = new InputParameter("binary","true");
		InputParameter threshold = new InputParameter("threshold","0.6");
		InputParameter interval = new InputParameter("output-interval", "20");
		
		HashMap<String, InputParameter> iparams=new HashMap<String, InputParameter>();
		iparams.put(tileIDs.getName(), tileIDs);
		iparams.put(rows.getName(), rows);
		iparams.put(columns.getName(), columns);
		iparams.put(binary.getName(), binary);
		iparams.put(id.getName(), id);
		iparams.put(threshold.getName(), threshold);
		iparams.put(interval.getName(), interval);
		
		IAlgorithmContext context = new AlgorithmContext("1", "asciiRasterDualSISO");
		
		try {
			dualSISO.initialize(context, iparams);
		} catch (FormatterInitializationException e1) {
			e1.printStackTrace();
		}
		
		IDataElement data;
		
		double measurement = 0.76;
		data = new DataElement("test");
		data.setValue(measurement);	
		
		//for(int i=0 ; i<30 ; i++) {
			try {
				//System.out.println(dualSISO.format(context, data));
				//System.out.println(dualSISO.format(context, data));
				dualSISO.format(context, data);
				dualSISO.format(context, data);
		//		Thread.sleep(2000);
			} catch (FormatterException e) {
				e.printStackTrace();
			} //catch (InterruptedException e) {
		//		// TODO Auto-generated catch block
		//		e.printStackTrace();
		//	}
		//}
    }
}*/


@Stateless (name="asciiRasterDualSISO")
@Remote ({IFormatter.class})
public class AsciiRasterDualSISOFormatter extends AbstractFormatter<String> {

	/**
     * Default constructor. 
     */
    public AsciiRasterDualSISOFormatter() {
    }

    @Override
	public String[] getRequiredParameters() {
		String[] params={"tileIDs","id","rows","columns","threshold"};//""binary"};
		return params;
	}
    
    //Only if "binary = true"
  	@Override
  	public String[] getOptionalParameters() {
  		String[] params={"output-interval"};//"threshold"};
  		return params;
  	}
  	
  	@Override
	public boolean allowsMultipleInputs() {
		return true;
	}
    
  	@Override
	public void initialize(IContext state, HashMap<String, InputParameter> iparams) throws FormatterInitializationException {
    	
    	ArrayList<String> tileIDs = new ArrayList<String>();
		Integer row = -1;
		Integer column = -1;
		Integer rows = -1;
		Integer columns = -1;
		
		//initialize params
		String[] params=getRequiredParameters();
		if (params!=null) { //parse required params

			Set<String> requiredParameters=new HashSet<String>(Arrays.asList(params));
			Set<String> optionalParameters=new HashSet<String>(Arrays.asList(getOptionalParameters()));
			
			Collection<InputParameter> paramValues=iparams.values();
			
			//Check for parameters that do not belong to the two lists
			for (InputParameter param : paramValues) {
				String paramName=param.getName();
				if (!requiredParameters.contains(paramName) && !optionalParameters.contains(paramName)) 
					System.out.println("WARNING --> Init Parameter '"+ paramName + "' not applicable for Formatter " + getType() +". Will be Ignored.");
			}
			
			//Check first for the required parameters
			for (InputParameter param : paramValues) {
				String paramName=param.getName();
				if (requiredParameters.remove(paramName)) {
					
					if(paramName.equalsIgnoreCase("id")) {
						state.setContextParameter(paramName, param.getValue());
					}
					
					if(paramName.equalsIgnoreCase("tileIDs")) {
						
						if(param.getValue() == null || param.getValue().isEmpty()) {
							throw new FormatterInitializationException("The expected format for the \""+paramName+"\" parameter is: \"area_tile_0_12,area_tile_2_3,...,area_tile_4_90\".\n");
						}
						
						//Parse the tile IDs and load them to an ArrayList
						StringTokenizer st1 = new StringTokenizer(param.getValue(), ",");
						
						while(st1.hasMoreTokens()) {
							tileIDs.add(st1.nextToken());
						}
						
						//state.setContextParameter(paramName, tileIDs);
					}
					
					//Check if we have a boolean value
					/*
					if(paramName.equalsIgnoreCase("binary")) {
						if(!param.getValue().equalsIgnoreCase("true") && !param.getValue().equalsIgnoreCase("false"))
							throw new FormatterInitializationException("ERROR --> Init (Required) Parameter '"+ paramName + "' for Formatter " + getType() +" can only take 'true' or 'false' as value.\n");
						else
							state.setContextParameter(paramName, param.getValue());
					}*/
					
					if(paramName.equalsIgnoreCase("rows") || paramName.equalsIgnoreCase("columns")) {
						
						try{
							if(Integer.parseInt(param.getValue()) < 1)
								throw new FormatterInitializationException("A non valid number(" + param.getValue() + ") was specified as '"+paramName+"'. The given value must be at least 1.\n");
							
							state.setContextParameter(paramName, Integer.parseInt(param.getValue()));
						}
						catch (NumberFormatException e){
							throw new FormatterInitializationException("A non valid number(" + param.getValue() + ") was specified as '"+paramName+"'.\n");
						}
					}
					
					//Check if the cutoff value belongs in the interval [0,1]
					if(paramName.equalsIgnoreCase("threshold")) {
						try{
							state.setContextParameter(paramName, param.getValue());
							
							if(Double.parseDouble(param.getValue()) < 0.0 || Double.parseDouble(param.getValue()) > 1.0) {
								throw new FormatterInitializationException("The value for 'threshold' parameter must be a probability that belongs to the range [0,1].\n");
							}
						}
						catch (NumberFormatException e){
							throw new FormatterInitializationException("A non valid number(" + param.getValue() + ") was specified as '"+paramName+"'.\n");
						}
					}
				} 
			}

			//check missing required parameters
			String errorMessage="";
			for (String s : requiredParameters) {
				errorMessage+="ERROR --> Init (Required) Parameter '"+ s + "' is needed for Formatter " + getType() +" but has not been specified.\n";
			}
			if (!errorMessage.isEmpty()) {
				System.out.println(errorMessage);
				throw new FormatterInitializationException(errorMessage);
			}		
			
			rows = (Integer)state.getContextParameter("rows");
			columns = (Integer)state.getContextParameter("columns");
			
			Integer [][] tileIDsArray = new Integer[tileIDs.size()][2];
			Integer counter = 0;
			
			//Check the consistency of the "rows" and "columns" values with the given tileIDs
			try {
				for(String tileID : tileIDs) {
					StringTokenizer st2 = new StringTokenizer(tileID, "_");

					st2.nextToken();
					st2.nextToken();

					row = Integer.parseInt(st2.nextToken());
					column = Integer.parseInt(st2.nextToken());

					if( (row >= rows) || (column >= columns) ) {
						throw new FormatterInitializationException("The given tileIDs as input must have a row value in the [0,"+ (rows-1) +"] range and a column value in the [0,"+ (columns-1) +"] range respectively.\n");
					}
					
					tileIDsArray[counter][0] = row;
					tileIDsArray[counter][1] = column;
					
					counter++;
				}
			}
			catch (NoSuchElementException e1) {
				throw new FormatterInitializationException("The expected format for the 'tileIDs' parameter is: \"area_tile_0_12,area_tile_2_3,...,area_tile_4_90\".\n");
			}
			
			state.setContextParameter("tileIDs", tileIDsArray);
			
			//If there is no required parameter missing then we check the value of the "binary" parameter
			//If this value is true then we have to collect the optional parameters (threshold)
			//if(Boolean.parseBoolean(state.getContextParameter("binary").toString())) {
				
				//We have to create a probability matrix, hence the rest parameters must be loaded
				for (InputParameter param : paramValues) {
					String paramName=param.getName();
					if (optionalParameters.remove(paramName)) {
						//Check if the cutoff value belongs in the interval [0,1]
						
						/*if(paramName.equalsIgnoreCase("threshold")) {
							try{
								state.setContextParameter(paramName, param.getValue());
								
								if(Double.parseDouble(param.getValue()) < 0.0 || Double.parseDouble(param.getValue()) > 1.0) {
									throw new FormatterInitializationException("The value for 'threshold' parameter must be a probability that belongs to the range [0,1].\n");
								}
							}
							catch (NumberFormatException e){
								throw new FormatterInitializationException("A non valid number(" + param.getValue() + ") was specified as '"+paramName+"'.\n");
							}
						}
						*/
						//Check if the given parameter is the "output-interval"
						if(paramName.equalsIgnoreCase("output-interval")) {
							try {
								if(Integer.parseInt(param.getValue()) < new Integer(0)) {
									throw new FormatterInitializationException("The value for 'output-interval' parameter must be a non negative integer.\n");
								}
								
								// Set the appropriate values for the rest context parameters
								state.setContextParameter(paramName, Integer.parseInt(param.getValue())); //output-interval context parameter
								//state.setContextParameter("last-output-timestamp", new Long(0)); //With this approach we get one output at the beginning of the algorithm
								state.setContextParameter("last-output-timestamp", (new Date()).getTime()); // With this approach we get the first output after the predefined interval
							}
							catch (NumberFormatException e) {
								throw new FormatterInitializationException("A non valid number(" + param.getValue() + ") was specified as '"+paramName+"'. A non negative integer must be provided. \n");
							}
						}
					} 
				}

				//check missing optional parameters
				for (String s : optionalParameters) {
					/*
					if(s.equalsIgnoreCase("threshold") && Boolean.parseBoolean(state.getContextParameter("binary").toString())) //In this case (binary = true) the optional parameter "threshold" must be present
						errorMessage+="ERROR --> Init (Optional) Parameter '"+ s + "' is needed for Formatter " + getType() +" but has not been specified.\n";
					*/
					if(s.equalsIgnoreCase("output-interval")) {
						state.setContextParameter(s, new Integer(0)); //output-interval context parameter
						state.setContextParameter("last-output-timestamp", new Long(0)); 
					}
				}
				if (!errorMessage.isEmpty()) {
					System.out.println(errorMessage);
					throw new FormatterInitializationException(errorMessage);
				}
			//}
				
			//Add context parameter to "lock" the respective contextor
			state.setContextParameter("locked", new Boolean(false));
		}
    }
    
    private String createAsciiRaster (Integer[][] tileIDs, Double propValue, String id, Integer rows, Integer columns, Double thresholdedValue) {
		
		String asciiRasterProbabilistic = null;
		String asciiRasterBinary = null;
		String concatenated = null;
		
		final DecimalFormat decimalFormat;
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
		otherSymbols.setDecimalSeparator('.');
		decimalFormat = new DecimalFormat("#.##", otherSymbols);
		
		Double [][] asciiProbabilisticArray = new Double[rows][columns];
		Double [][] asciiBinaryArray = new Double[rows][columns];
		
		for(int i=0 ; i<rows ; i++) {
			Arrays.fill(asciiProbabilisticArray[i], Double.valueOf(decimalFormat.format(new Double(0.00))));
			Arrays.fill(asciiBinaryArray[i], Double.valueOf(decimalFormat.format(new Double(0.00))));
		}
		
		for(int i=0 ; i<tileIDs.length ; i++) {
			asciiProbabilisticArray[tileIDs[i][0]][tileIDs[i][1]] = Double.valueOf(decimalFormat.format(propValue));
			asciiBinaryArray[tileIDs[i][0]][tileIDs[i][1]] = Double.valueOf(decimalFormat.format(thresholdedValue));
		}
		
		asciiRasterProbabilistic = "id: "+id+"\n";
		asciiRasterProbabilistic = asciiRasterProbabilistic + "rows: "+rows+"\n";
		asciiRasterProbabilistic = asciiRasterProbabilistic + "cols: "+columns+"\n";
		
		asciiRasterBinary = "id: "+id+"\n";
		asciiRasterBinary = asciiRasterBinary + "rows: "+rows+"\n";
		asciiRasterBinary = asciiRasterBinary + "cols: "+columns+"\n";
		
		for(int i=0 ; i<rows ; i++) {
			for(int j=0 ; j<columns ; j++) {
				if( (j == columns - 1) && (i != rows - 1) ) { //Last column
					//if(!binary)
						asciiRasterProbabilistic = asciiRasterProbabilistic + String.format(Locale.ENGLISH, "%1$,.2f", asciiProbabilisticArray[i][j]) + "\n";
					//else
						asciiRasterBinary = asciiRasterBinary + Math.round(asciiBinaryArray[i][j].floatValue()) + "\n";
				}
				else {
					if( (j == columns - 1) && (i == rows - 1) ) { //Last row && Last column
						//if(!binary)
							asciiRasterProbabilistic = asciiRasterProbabilistic + String.format(Locale.ENGLISH, "%1$,.2f", asciiProbabilisticArray[i][j]);
						//else
							asciiRasterBinary = asciiRasterBinary + Math.round(asciiBinaryArray[i][j].floatValue());
					}
					else {
						//if(!binary)
							asciiRasterProbabilistic = asciiRasterProbabilistic + String.format(Locale.ENGLISH, "%1$,.2f", asciiProbabilisticArray[i][j]) + " ";
						//else
							asciiRasterBinary = asciiRasterBinary + Math.round(asciiBinaryArray[i][j].floatValue()) + " ";
					}
				}
			}
		}
		
		concatenated = asciiRasterBinary + "|" + asciiRasterProbabilistic;
		
		return concatenated;
	}
    
    @Override
	public String format(IContext state, IDataElement... data) throws FormatterException {
    	
    	String formattedMessage=null;
		Double propValue = new Double(0.0);
		Double thresholdedValue = new Double(0.0);
		Integer rows = 0, columns = 0, interval;
		Long lastOutputTimestamp, currentTimestamp;
		String id="";
		Integer tileIDsArray[][];
		Boolean locked = false;
		
		//Check the value that comes inside the formatter
		if(data != null) {
			if (data[0].getValue() instanceof Number) {
				
				tileIDsArray = (Integer[][])state.getContextParameter("tileIDs");
				id = (String)state.getContextParameter("id");
				rows = (Integer)state.getContextParameter("rows");
				columns = (Integer)state.getContextParameter("columns");
				interval = (Integer)state.getContextParameter("output-interval") * (new Integer(1000)); //Convert seconds to milliseconds
				lastOutputTimestamp = (Long)state.getContextParameter("last-output-timestamp");
				locked = (Boolean)state.getContextParameter("locked");
				
				if((Double)data[0].getValue() < 0.0 || (Double)data[0].getValue() > 1.0) {
					throw new FormatterException("The input value of the \""+this.getType()+"\" formatter must be a probability that belongs to the range [0,1].\n");
				}
				
				currentTimestamp = (new Date()).getTime();
				// Check if the interval since the last output has passed and the inserted value is non-zero
				if( ((currentTimestamp - lastOutputTimestamp) >= new Long(interval)) && (!locked) && (((Double)data[0].getValue()).compareTo(new Double(0.0)) != 0)) {
					
					// Handle lock policy to avoid concurrent access because of high processing time
					/*if( (interval != 0)) { // Lock the contextor
						state.setContextParameter("locked", new Boolean(true));
					}*/
						
					propValue = (Double)data[0].getValue();
					
					if(propValue < Double.parseDouble(state.getContextParameter("threshold").toString()))
						thresholdedValue = 0.0;
					else
						thresholdedValue = 1.0;
					
					// Handle lock policy to avoid concurrent access because of high processing time
					state.setContextParameter("locked", new Boolean(true));
					
					formattedMessage = createAsciiRaster (tileIDsArray, propValue, id, rows, columns, thresholdedValue);
					
					//currentTimestamp = (new Date()).getTime(); //Since the processing stage lasts over 1s (Without it, we "eat" the delay caused by the processing)
					state.setContextParameter("last-output-timestamp", currentTimestamp);
						
					// Un-Lock the contextor
					state.setContextParameter("locked", new Boolean(false));
					
				} //else, a null value will be returned and the respective adapter will not send any output outside the FBox
				else { // For debug purposes only!
					System.out.println("[asciiRasterDualSISO-"+id+"] Diff: "+(currentTimestamp - lastOutputTimestamp)+"   Interval: "+interval+"   Locked: "+locked);
				}
			} else {
				throw new FormatterException("Invalid data value detected("+data[0].getValue()+"). Value must be Double");
			}
		}
		
		if(formattedMessage == null) {
			System.out.println("[asciiRasterDualSISO-"+id+"] OUTPUT: null");
		}
		else 
			System.out.println("[asciiRasterDualSISO-"+id+"] OUTPUT: PROPABILITY_MAPS "+rows+"x"+columns);
		
		//System.out.println("[asciiRasterDualSISO-"+id+"] GENERAL OUTPUT: "+formattedMessage);
		
		return formattedMessage;
    }
    
    @Override
    public String getType() {
		return "asciiRasterDualSISO";
    }
    
    //Test implemented formatter
    public static void main(String args[]) {
    	
		AsciiRasterDualSISOFormatter asciiRasterDualSISO = new AsciiRasterDualSISOFormatter();

		String tiles = "area_tile_130_75,area_tile_130_76,area_tile_130_77,area_tile_130_78,area_tile_130_79,area_tile_130_80,area_tile_130_81,area_tile_130_82,area_tile_130_83,area_tile_130_84,area_tile_130_85,area_tile_130_86,area_tile_130_87,area_tile_130_88,area_tile_130_89,area_tile_130_90,area_tile_130_91,area_tile_130_92,area_tile_130_93,area_tile_130_94,area_tile_130_95,area_tile_130_96,area_tile_130_97,area_tile_130_98,area_tile_130_99,area_tile_130_100,area_tile_130_101,area_tile_130_102,area_tile_130_103,area_tile_130_104,area_tile_130_105,area_tile_130_106,area_tile_130_107,area_tile_130_108,area_tile_130_109,area_tile_130_110,area_tile_130_111,area_tile_130_112,area_tile_130_113,area_tile_130_114,area_tile_130_115,area_tile_130_116,area_tile_130_117,area_tile_130_118,area_tile_130_119,area_tile_131_75,area_tile_131_76,area_tile_131_77,area_tile_131_78,area_tile_131_79,area_tile_131_80,area_tile_131_81,area_tile_131_82,area_tile_131_83,area_tile_131_84,area_tile_131_85,area_tile_131_86,area_tile_131_87,area_tile_131_88,area_tile_131_89,area_tile_131_90,area_tile_131_91,area_tile_131_92,area_tile_131_93,area_tile_131_94,area_tile_131_95,area_tile_131_96,area_tile_131_97,area_tile_131_98,area_tile_131_99,area_tile_131_100,area_tile_131_101,area_tile_131_102,area_tile_131_103,area_tile_131_104,area_tile_131_105,area_tile_131_106,area_tile_131_107,area_tile_131_108,area_tile_131_109,area_tile_131_110,area_tile_131_111,area_tile_131_112,area_tile_131_113,area_tile_131_114,area_tile_131_115,area_tile_131_116,area_tile_131_117,area_tile_131_118,area_tile_131_119,area_tile_132_75,area_tile_132_76,area_tile_132_77,area_tile_132_78,area_tile_132_79,area_tile_132_80,area_tile_132_81,area_tile_132_82,area_tile_132_83,area_tile_132_84,area_tile_132_85,area_tile_132_86,area_tile_132_87,area_tile_132_88,area_tile_132_89,area_tile_132_90,area_tile_132_91,area_tile_132_92,area_tile_132_93,area_tile_132_94,area_tile_132_95,area_tile_132_96,area_tile_132_97,area_tile_132_98,area_tile_132_99,area_tile_132_100,area_tile_132_101,area_tile_132_102,area_tile_132_103,area_tile_132_104,area_tile_132_105,area_tile_132_106,area_tile_132_107,area_tile_132_108,area_tile_132_109,area_tile_132_110,area_tile_132_111,area_tile_132_112,area_tile_132_113,area_tile_132_114,area_tile_132_115,area_tile_132_116,area_tile_132_117,area_tile_132_118,area_tile_132_119,area_tile_133_75,area_tile_133_76,area_tile_133_77,area_tile_133_78,area_tile_133_79,area_tile_133_80,area_tile_133_81,area_tile_133_82,area_tile_133_83,area_tile_133_84,area_tile_133_85,area_tile_133_86,area_tile_133_87,area_tile_133_88,area_tile_133_89,area_tile_133_90,area_tile_133_91,area_tile_133_92,area_tile_133_93,area_tile_133_94,area_tile_133_95,area_tile_133_96,area_tile_133_97,area_tile_133_98,area_tile_133_99,area_tile_133_100,area_tile_133_101,area_tile_133_102,area_tile_133_103,area_tile_133_104,area_tile_133_105,area_tile_133_106,area_tile_133_107,area_tile_133_108,area_tile_133_109,area_tile_133_110,area_tile_133_111,area_tile_133_112,area_tile_133_113,area_tile_133_114,area_tile_133_115,area_tile_133_116,area_tile_133_117,area_tile_133_118,area_tile_133_119,area_tile_134_75,area_tile_134_76,area_tile_134_77,area_tile_134_78,area_tile_134_79,area_tile_134_80,area_tile_134_81,area_tile_134_82,area_tile_134_83,area_tile_134_84,area_tile_134_85,area_tile_134_86,area_tile_134_87,area_tile_134_88,area_tile_134_89,area_tile_134_90,area_tile_134_91,area_tile_134_92,area_tile_134_93,area_tile_134_94,area_tile_134_95,area_tile_134_96,area_tile_134_97,area_tile_134_98,area_tile_134_99,area_tile_134_100,area_tile_134_101,area_tile_134_102,area_tile_134_103,area_tile_134_104,area_tile_134_105,area_tile_134_106,area_tile_134_107,area_tile_134_108,area_tile_134_109,area_tile_134_110,area_tile_134_111,area_tile_134_112,area_tile_134_113,area_tile_134_114,area_tile_134_115,area_tile_134_116,area_tile_134_117,area_tile_134_118,area_tile_134_119,area_tile_135_75,area_tile_135_76,area_tile_135_77,area_tile_135_78,area_tile_135_79,area_tile_135_80,area_tile_135_81,area_tile_135_82,area_tile_135_83,area_tile_135_84,area_tile_135_85,area_tile_135_86,area_tile_135_87,area_tile_135_88,area_tile_135_89,area_tile_135_90,area_tile_135_91,area_tile_135_92,area_tile_135_93,area_tile_135_94,area_tile_135_95,area_tile_135_96,area_tile_135_97,area_tile_135_98,area_tile_135_99,area_tile_135_100,area_tile_135_101,area_tile_135_102,area_tile_135_103,area_tile_135_104,area_tile_135_105,area_tile_135_106,area_tile_135_107,area_tile_135_108,area_tile_135_109,area_tile_135_110,area_tile_135_111,area_tile_135_112,area_tile_135_113,area_tile_135_114,area_tile_135_115,area_tile_135_116,area_tile_135_117,area_tile_135_118,area_tile_135_119,area_tile_136_75,area_tile_136_76,area_tile_136_77,area_tile_136_78,area_tile_136_79,area_tile_136_80,area_tile_136_81,area_tile_136_82,area_tile_136_83,area_tile_136_84,area_tile_136_85,area_tile_136_86,area_tile_136_87,area_tile_136_88,area_tile_136_89,area_tile_136_90,area_tile_136_91,area_tile_136_92,area_tile_136_93,area_tile_136_94,area_tile_136_95,area_tile_136_96,area_tile_136_97,area_tile_136_98,area_tile_136_99,area_tile_136_100,area_tile_136_101,area_tile_136_102,area_tile_136_103,area_tile_136_104,area_tile_136_105,area_tile_136_106,area_tile_136_107,area_tile_136_108,area_tile_136_109,area_tile_136_110,area_tile_136_111,area_tile_136_112,area_tile_136_113,area_tile_136_114,area_tile_136_115,area_tile_136_116,area_tile_136_117,area_tile_136_118,area_tile_136_119,area_tile_137_75,area_tile_137_76,area_tile_137_77,area_tile_137_78,area_tile_137_79,area_tile_137_80,area_tile_137_81,area_tile_137_82,area_tile_137_83,area_tile_137_84,area_tile_137_85,area_tile_137_86,area_tile_137_87,area_tile_137_88,area_tile_137_89,area_tile_137_90,area_tile_137_91,area_tile_137_92,area_tile_137_93,area_tile_137_94,area_tile_137_95,area_tile_137_96,area_tile_137_97,area_tile_137_98,area_tile_137_99,area_tile_137_100,area_tile_137_101,area_tile_137_102,area_tile_137_103,area_tile_137_104,area_tile_137_105,area_tile_137_106,area_tile_137_107,area_tile_137_108,area_tile_137_109,area_tile_137_110,area_tile_137_111,area_tile_137_112,area_tile_137_113,area_tile_137_114,area_tile_137_115,area_tile_137_116,area_tile_137_117,area_tile_137_118,area_tile_137_119,area_tile_138_75,area_tile_138_76,area_tile_138_77,area_tile_138_78,area_tile_138_79,area_tile_138_80,area_tile_138_81,area_tile_138_82,area_tile_138_83,area_tile_138_84,area_tile_138_85,area_tile_138_86,area_tile_138_87,area_tile_138_88,area_tile_138_89,area_tile_138_90,area_tile_138_91,area_tile_138_92,area_tile_138_93,area_tile_138_94,area_tile_138_95,area_tile_138_96,area_tile_138_97,area_tile_138_98,area_tile_138_99,area_tile_138_100,area_tile_138_101,area_tile_138_102,area_tile_138_103,area_tile_138_104,area_tile_138_105,area_tile_138_106,area_tile_138_107,area_tile_138_108,area_tile_138_109,area_tile_138_110,area_tile_138_111,area_tile_138_112,area_tile_138_113,area_tile_138_114,area_tile_138_115,area_tile_138_116,area_tile_138_117,area_tile_138_118,area_tile_138_119,area_tile_139_75,area_tile_139_76,area_tile_139_77,area_tile_139_78,area_tile_139_79,area_tile_139_80,area_tile_139_81,area_tile_139_82,area_tile_139_83,area_tile_139_84,area_tile_139_85,area_tile_139_86,area_tile_139_87,area_tile_139_88,area_tile_139_89,area_tile_139_90,area_tile_139_91,area_tile_139_92,area_tile_139_93,area_tile_139_94,area_tile_139_95,area_tile_139_96,area_tile_139_97,area_tile_139_98,area_tile_139_99,area_tile_139_100,area_tile_139_101,area_tile_139_102,area_tile_139_103,area_tile_139_104,area_tile_139_105,area_tile_139_106,area_tile_139_107,area_tile_139_108,area_tile_139_109,area_tile_139_110,area_tile_139_111,area_tile_139_112,area_tile_139_113,area_tile_139_114,area_tile_139_115,area_tile_139_116,area_tile_139_117,area_tile_139_118,area_tile_139_119,area_tile_140_75,area_tile_140_76,area_tile_140_77,area_tile_140_78,area_tile_140_79,area_tile_140_80,area_tile_140_81,area_tile_140_82,area_tile_140_83,area_tile_140_84,area_tile_140_85,area_tile_140_86,area_tile_140_87,area_tile_140_88,area_tile_140_89,area_tile_140_90,area_tile_140_91,area_tile_140_92,area_tile_140_93,area_tile_140_94,area_tile_140_95,area_tile_140_96,area_tile_140_97,area_tile_140_98,area_tile_140_99,area_tile_140_100,area_tile_140_101,area_tile_140_102,area_tile_140_103,area_tile_140_104,area_tile_140_105,area_tile_140_106,area_tile_140_107,area_tile_140_108,area_tile_140_109,area_tile_140_110,area_tile_140_111,area_tile_140_112,area_tile_140_113,area_tile_140_114,area_tile_140_115,area_tile_140_116,area_tile_140_117,area_tile_140_118,area_tile_140_119,area_tile_141_75,area_tile_141_76,area_tile_141_77,area_tile_141_78,area_tile_141_79,area_tile_141_80,area_tile_141_81,area_tile_141_82,area_tile_141_83,area_tile_141_84,area_tile_141_85,area_tile_141_86,area_tile_141_87,area_tile_141_88,area_tile_141_89,area_tile_141_90,area_tile_141_91,area_tile_141_92,area_tile_141_93,area_tile_141_94,area_tile_141_95,area_tile_141_96,area_tile_141_97,area_tile_141_98,area_tile_141_99,area_tile_141_100,area_tile_141_101,area_tile_141_102,area_tile_141_103,area_tile_141_104,area_tile_141_105,area_tile_141_106,area_tile_141_107,area_tile_141_108,area_tile_141_109,area_tile_141_110,area_tile_141_111,area_tile_141_112,area_tile_141_113,area_tile_141_114,area_tile_141_115,area_tile_141_116,area_tile_141_117,area_tile_141_118,area_tile_141_119,area_tile_142_75,area_tile_142_76,area_tile_142_77,area_tile_142_78,area_tile_142_79,area_tile_142_80,area_tile_142_81,area_tile_142_82,area_tile_142_83,area_tile_142_84,area_tile_142_85,area_tile_142_86,area_tile_142_87,area_tile_142_88,area_tile_142_89,area_tile_142_90,area_tile_142_91,area_tile_142_92,area_tile_142_93,area_tile_142_94,area_tile_142_95,area_tile_142_96,area_tile_142_97,area_tile_142_98,area_tile_142_99,area_tile_142_100,area_tile_142_101,area_tile_142_102,area_tile_142_103,area_tile_142_104,area_tile_142_105,area_tile_142_106,area_tile_142_107,area_tile_142_108,area_tile_142_109,area_tile_142_110,area_tile_142_111,area_tile_142_112,area_tile_142_113,area_tile_142_114,area_tile_142_115,area_tile_142_116,area_tile_142_117,area_tile_142_118,area_tile_142_119,area_tile_143_75,area_tile_143_76,area_tile_143_77,area_tile_143_78,area_tile_143_79,area_tile_143_80,area_tile_143_81,area_tile_143_82,area_tile_143_83,area_tile_143_84,area_tile_143_85,area_tile_143_86,area_tile_143_87,area_tile_143_88,area_tile_143_89,area_tile_143_90,area_tile_143_91,area_tile_143_92,area_tile_143_93,area_tile_143_94,area_tile_143_95,area_tile_143_96,area_tile_143_97,area_tile_143_98,area_tile_143_99,area_tile_143_100,area_tile_143_101,area_tile_143_102,area_tile_143_103,area_tile_143_104,area_tile_143_105,area_tile_143_106,area_tile_143_107,area_tile_143_108,area_tile_143_109,area_tile_143_110,area_tile_143_111,area_tile_143_112,area_tile_143_113,area_tile_143_114,area_tile_143_115,area_tile_143_116,area_tile_143_117,area_tile_143_118,area_tile_143_119,area_tile_144_75,area_tile_144_76,area_tile_144_77,area_tile_144_78,area_tile_144_79,area_tile_144_80,area_tile_144_81,area_tile_144_82,area_tile_144_83,area_tile_144_84,area_tile_144_85,area_tile_144_86,area_tile_144_87,area_tile_144_88,area_tile_144_89,area_tile_144_90,area_tile_144_91,area_tile_144_92,area_tile_144_93,area_tile_144_94,area_tile_144_95,area_tile_144_96,area_tile_144_97,area_tile_144_98,area_tile_144_99,area_tile_144_100,area_tile_144_101,area_tile_144_102,area_tile_144_103,area_tile_144_104,area_tile_144_105,area_tile_144_106,area_tile_144_107,area_tile_144_108,area_tile_144_109,area_tile_144_110,area_tile_144_111,area_tile_144_112,area_tile_144_113,area_tile_144_114,area_tile_144_115,area_tile_144_116,area_tile_144_117,area_tile_144_118,area_tile_144_119,area_tile_145_75,area_tile_145_76,area_tile_145_77,area_tile_145_78,area_tile_145_79,area_tile_145_80,area_tile_145_81,area_tile_145_82,area_tile_145_83,area_tile_145_84,area_tile_145_85,area_tile_145_86,area_tile_145_87,area_tile_145_88,area_tile_145_89,area_tile_145_90,area_tile_145_91,area_tile_145_92,area_tile_145_93,area_tile_145_94,area_tile_145_95,area_tile_145_96,area_tile_145_97,area_tile_145_98,area_tile_145_99,area_tile_145_100,area_tile_145_101,area_tile_145_102,area_tile_145_103,area_tile_145_104,area_tile_145_105,area_tile_145_106,area_tile_145_107,area_tile_145_108,area_tile_145_109,area_tile_145_110,area_tile_145_111,area_tile_145_112,area_tile_145_113,area_tile_145_114,area_tile_145_115,area_tile_145_116,area_tile_145_117,area_tile_145_118,area_tile_145_119,area_tile_146_75,area_tile_146_76,area_tile_146_77,area_tile_146_78,area_tile_146_79,area_tile_146_80,area_tile_146_81,area_tile_146_82,area_tile_146_83,area_tile_146_84,area_tile_146_85,area_tile_146_86,area_tile_146_87,area_tile_146_88,area_tile_146_89,area_tile_146_90,area_tile_146_91,area_tile_146_92,area_tile_146_93,area_tile_146_94,area_tile_146_95,area_tile_146_96,area_tile_146_97,area_tile_146_98,area_tile_146_99,area_tile_146_100,area_tile_146_101,area_tile_146_102,area_tile_146_103,area_tile_146_104,area_tile_146_105,area_tile_146_106,area_tile_146_107,area_tile_146_108,area_tile_146_109,area_tile_146_110,area_tile_146_111,area_tile_146_112,area_tile_146_113,area_tile_146_114,area_tile_146_115,area_tile_146_116,area_tile_146_117,area_tile_146_118,area_tile_146_119,area_tile_147_75,area_tile_147_76,area_tile_147_77,area_tile_147_78,area_tile_147_79,area_tile_147_80,area_tile_147_81,area_tile_147_82,area_tile_147_83,area_tile_147_84,area_tile_147_85,area_tile_147_86,area_tile_147_87,area_tile_147_88,area_tile_147_89,area_tile_147_90,area_tile_147_91,area_tile_147_92,area_tile_147_93,area_tile_147_94,area_tile_147_95,area_tile_147_96,area_tile_147_97,area_tile_147_98,area_tile_147_99,area_tile_147_100,area_tile_147_101,area_tile_147_102,area_tile_147_103,area_tile_147_104,area_tile_147_105,area_tile_147_106,area_tile_147_107,area_tile_147_108,area_tile_147_109,area_tile_147_110,area_tile_147_111,area_tile_147_112,area_tile_147_113,area_tile_147_114,area_tile_147_115,area_tile_147_116,area_tile_147_117,area_tile_147_118,area_tile_147_119,area_tile_148_75,area_tile_148_76,area_tile_148_77,area_tile_148_78,area_tile_148_79,area_tile_148_80,area_tile_148_81,area_tile_148_82,area_tile_148_83,area_tile_148_84,area_tile_148_85,area_tile_148_86,area_tile_148_87,area_tile_148_88,area_tile_148_89,area_tile_148_90,area_tile_148_91,area_tile_148_92,area_tile_148_93,area_tile_148_94,area_tile_148_95,area_tile_148_96,area_tile_148_97,area_tile_148_98,area_tile_148_99,area_tile_148_100,area_tile_148_101,area_tile_148_102,area_tile_148_103,area_tile_148_104,area_tile_148_105,area_tile_148_106,area_tile_148_107,area_tile_148_108,area_tile_148_109,area_tile_148_110,area_tile_148_111,area_tile_148_112,area_tile_148_113,area_tile_148_114,area_tile_148_115,area_tile_148_116,area_tile_148_117,area_tile_148_118,area_tile_148_119,area_tile_149_75,area_tile_149_76,area_tile_149_77,area_tile_149_78,area_tile_149_79,area_tile_149_80,area_tile_149_81,area_tile_149_82,area_tile_149_83,area_tile_149_84,area_tile_149_85,area_tile_149_86,area_tile_149_87,area_tile_149_88,area_tile_149_89,area_tile_149_90,area_tile_149_91,area_tile_149_92,area_tile_149_93,area_tile_149_94,area_tile_149_95,area_tile_149_96,area_tile_149_97,area_tile_149_98,area_tile_149_99,area_tile_149_100,area_tile_149_101,area_tile_149_102,area_tile_149_103,area_tile_149_104,area_tile_149_105,area_tile_149_106,area_tile_149_107,area_tile_149_108,area_tile_149_109,area_tile_149_110,area_tile_149_111,area_tile_149_112,area_tile_149_113,area_tile_149_114,area_tile_149_115,area_tile_149_116,area_tile_149_117,area_tile_149_118,area_tile_149_119,area_tile_150_75,area_tile_150_76,area_tile_150_77,area_tile_150_78,area_tile_150_79,area_tile_150_80,area_tile_150_81,area_tile_150_82,area_tile_150_83,area_tile_150_84,area_tile_150_85,area_tile_150_86,area_tile_150_87,area_tile_150_88,area_tile_150_89,area_tile_150_90,area_tile_150_91,area_tile_150_92,area_tile_150_93,area_tile_150_94,area_tile_150_95,area_tile_150_96,area_tile_150_97,area_tile_150_98,area_tile_150_99,area_tile_150_100,area_tile_150_101,area_tile_150_102,area_tile_150_103,area_tile_150_104,area_tile_150_105,area_tile_150_106,area_tile_150_107,area_tile_150_108,area_tile_150_109,area_tile_150_110,area_tile_150_111,area_tile_150_112,area_tile_150_113,area_tile_150_114,area_tile_150_115,area_tile_150_116,area_tile_150_117,area_tile_150_118,area_tile_150_119,area_tile_151_75,area_tile_151_76,area_tile_151_77,area_tile_151_78,area_tile_151_79,area_tile_151_80,area_tile_151_81,area_tile_151_82,area_tile_151_83,area_tile_151_84,area_tile_151_85,area_tile_151_86,area_tile_151_87,area_tile_151_88,area_tile_151_89,area_tile_151_90,area_tile_151_91,area_tile_151_92,area_tile_151_93,area_tile_151_94,area_tile_151_95,area_tile_151_96,area_tile_151_97,area_tile_151_98,area_tile_151_99,area_tile_151_100,area_tile_151_101,area_tile_151_102,area_tile_151_103,area_tile_151_104,area_tile_151_105,area_tile_151_106,area_tile_151_107,area_tile_151_108,area_tile_151_109,area_tile_151_110,area_tile_151_111,area_tile_151_112,area_tile_151_113,area_tile_151_114,area_tile_151_115,area_tile_151_116,area_tile_151_117,area_tile_151_118,area_tile_151_119,area_tile_152_75,area_tile_152_76,area_tile_152_77,area_tile_152_78,area_tile_152_79,area_tile_152_80,area_tile_152_81,area_tile_152_82,area_tile_152_83,area_tile_152_84,area_tile_152_85,area_tile_152_86,area_tile_152_87,area_tile_152_88,area_tile_152_89,area_tile_152_90,area_tile_152_91,area_tile_152_92,area_tile_152_93,area_tile_152_94,area_tile_152_95,area_tile_152_96,area_tile_152_97,area_tile_152_98,area_tile_152_99,area_tile_152_100,area_tile_152_101,area_tile_152_102,area_tile_152_103,area_tile_152_104,area_tile_152_105,area_tile_152_106,area_tile_152_107,area_tile_152_108,area_tile_152_109,area_tile_152_110,area_tile_152_111,area_tile_152_112,area_tile_152_113,area_tile_152_114,area_tile_152_115,area_tile_152_116,area_tile_152_117,area_tile_152_118,area_tile_152_119,area_tile_153_75,area_tile_153_76,area_tile_153_77,area_tile_153_78,area_tile_153_79,area_tile_153_80,area_tile_153_81,area_tile_153_82,area_tile_153_83,area_tile_153_84,area_tile_153_85,area_tile_153_86,area_tile_153_87,area_tile_153_88,area_tile_153_89,area_tile_153_90,area_tile_153_91,area_tile_153_92,area_tile_153_93,area_tile_153_94,area_tile_153_95,area_tile_153_96,area_tile_153_97,area_tile_153_98,area_tile_153_99,area_tile_153_100,area_tile_153_101,area_tile_153_102,area_tile_153_103,area_tile_153_104,area_tile_153_105,area_tile_153_106,area_tile_153_107,area_tile_153_108,area_tile_153_109,area_tile_153_110,area_tile_153_111,area_tile_153_112,area_tile_153_113,area_tile_153_114,area_tile_153_115,area_tile_153_116,area_tile_153_117,area_tile_153_118,area_tile_153_119,area_tile_154_75,area_tile_154_76,area_tile_154_77,area_tile_154_78,area_tile_154_79,area_tile_154_80,area_tile_154_81,area_tile_154_82,area_tile_154_83,area_tile_154_84,area_tile_154_85,area_tile_154_86,area_tile_154_87,area_tile_154_88,area_tile_154_89,area_tile_154_90,area_tile_154_91,area_tile_154_92,area_tile_154_93,area_tile_154_94,area_tile_154_95,area_tile_154_96,area_tile_154_97,area_tile_154_98,area_tile_154_99,area_tile_154_100,area_tile_154_101,area_tile_154_102,area_tile_154_103,area_tile_154_104,area_tile_154_105,area_tile_154_106,area_tile_154_107,area_tile_154_108,area_tile_154_109,area_tile_154_110,area_tile_154_111,area_tile_154_112,area_tile_154_113,area_tile_154_114,area_tile_154_115,area_tile_154_116,area_tile_154_117,area_tile_154_118,area_tile_154_119,area_tile_155_75,area_tile_155_76,area_tile_155_77,area_tile_155_78,area_tile_155_79,area_tile_155_80,area_tile_155_81,area_tile_155_82,area_tile_155_83,area_tile_155_84,area_tile_155_85,area_tile_155_86,area_tile_155_87,area_tile_155_88,area_tile_155_89,area_tile_155_90,area_tile_155_91,area_tile_155_92,area_tile_155_93,area_tile_155_94,area_tile_155_95,area_tile_155_96,area_tile_155_97,area_tile_155_98,area_tile_155_99,area_tile_155_100,area_tile_155_101,area_tile_155_102,area_tile_155_103,area_tile_155_104,area_tile_155_105,area_tile_155_106,area_tile_155_107,area_tile_155_108,area_tile_155_109,area_tile_155_110,area_tile_155_111,area_tile_155_112,area_tile_155_113,area_tile_155_114,area_tile_155_115,area_tile_155_116,area_tile_155_117,area_tile_155_118,area_tile_155_119,area_tile_156_75,area_tile_156_76,area_tile_156_77,area_tile_156_78,area_tile_156_79,area_tile_156_80,area_tile_156_81,area_tile_156_82,area_tile_156_83,area_tile_156_84,area_tile_156_85,area_tile_156_86,area_tile_156_87,area_tile_156_88,area_tile_156_89,area_tile_156_90,area_tile_156_91,area_tile_156_92,area_tile_156_93,area_tile_156_94,area_tile_156_95,area_tile_156_96,area_tile_156_97,area_tile_156_98,area_tile_156_99,area_tile_156_100,area_tile_156_101,area_tile_156_102,area_tile_156_103,area_tile_156_104,area_tile_156_105,area_tile_156_106,area_tile_156_107,area_tile_156_108,area_tile_156_109,area_tile_156_110,area_tile_156_111,area_tile_156_112,area_tile_156_113,area_tile_156_114,area_tile_156_115,area_tile_156_116,area_tile_156_117,area_tile_156_118,area_tile_156_119,area_tile_157_75,area_tile_157_76,area_tile_157_77,area_tile_157_78,area_tile_157_79,area_tile_157_80,area_tile_157_81,area_tile_157_82,area_tile_157_83,area_tile_157_84,area_tile_157_85,area_tile_157_86,area_tile_157_87,area_tile_157_88,area_tile_157_89,area_tile_157_90,area_tile_157_91,area_tile_157_92,area_tile_157_93,area_tile_157_94,area_tile_157_95,area_tile_157_96,area_tile_157_97,area_tile_157_98,area_tile_157_99,area_tile_157_100,area_tile_157_101,area_tile_157_102,area_tile_157_103,area_tile_157_104,area_tile_157_105,area_tile_157_106,area_tile_157_107,area_tile_157_108,area_tile_157_109,area_tile_157_110,area_tile_157_111,area_tile_157_112,area_tile_157_113,area_tile_157_114,area_tile_157_115,area_tile_157_116,area_tile_157_117,area_tile_157_118,area_tile_157_119,area_tile_158_75,area_tile_158_76,area_tile_158_77,area_tile_158_78,area_tile_158_79,area_tile_158_80,area_tile_158_81,area_tile_158_82,area_tile_158_83,area_tile_158_84,area_tile_158_85,area_tile_158_86,area_tile_158_87,area_tile_158_88,area_tile_158_89,area_tile_158_90,area_tile_158_91,area_tile_158_92,area_tile_158_93,area_tile_158_94,area_tile_158_95,area_tile_158_96,area_tile_158_97,area_tile_158_98,area_tile_158_99,area_tile_158_100,area_tile_158_101,area_tile_158_102,area_tile_158_103,area_tile_158_104,area_tile_158_105,area_tile_158_106,area_tile_158_107,area_tile_158_108,area_tile_158_109,area_tile_158_110,area_tile_158_111,area_tile_158_112,area_tile_158_113,area_tile_158_114,area_tile_158_115,area_tile_158_116,area_tile_158_117,area_tile_158_118,area_tile_158_119,area_tile_159_75,area_tile_159_76,area_tile_159_77,area_tile_159_78,area_tile_159_79,area_tile_159_80,area_tile_159_81,area_tile_159_82,area_tile_159_83,area_tile_159_84,area_tile_159_85,area_tile_159_86,area_tile_159_87,area_tile_159_88,area_tile_159_89,area_tile_159_90,area_tile_159_91,area_tile_159_92,area_tile_159_93,area_tile_159_94,area_tile_159_95,area_tile_159_96,area_tile_159_97,area_tile_159_98,area_tile_159_99,area_tile_159_100,area_tile_159_101,area_tile_159_102,area_tile_159_103,area_tile_159_104,area_tile_159_105,area_tile_159_106,area_tile_159_107,area_tile_159_108,area_tile_159_109,area_tile_159_110,area_tile_159_111,area_tile_159_112,area_tile_159_113,area_tile_159_114,area_tile_159_115,area_tile_159_116,area_tile_159_117,area_tile_159_118,area_tile_159_119,area_tile_160_75,area_tile_160_76,area_tile_160_77,area_tile_160_78,area_tile_160_79,area_tile_160_80,area_tile_160_81,area_tile_160_82,area_tile_160_83,area_tile_160_84,area_tile_160_85,area_tile_160_86,area_tile_160_87,area_tile_160_88,area_tile_160_89,area_tile_160_90,area_tile_160_91,area_tile_160_92,area_tile_160_93,area_tile_160_94,area_tile_160_95,area_tile_160_96,area_tile_160_97,area_tile_160_98,area_tile_160_99,area_tile_160_100,area_tile_160_101,area_tile_160_102,area_tile_160_103,area_tile_160_104,area_tile_160_105,area_tile_160_106,area_tile_160_107,area_tile_160_108,area_tile_160_109,area_tile_160_110,area_tile_160_111,area_tile_160_112,area_tile_160_113,area_tile_160_114,area_tile_160_115,area_tile_160_116,area_tile_160_117,area_tile_160_118,area_tile_160_119,area_tile_161_75,area_tile_161_76,area_tile_161_77,area_tile_161_78,area_tile_161_79,area_tile_161_80,area_tile_161_81,area_tile_161_82,area_tile_161_83,area_tile_161_84,area_tile_161_85,area_tile_161_86,area_tile_161_87,area_tile_161_88,area_tile_161_89,area_tile_161_90,area_tile_161_91,area_tile_161_92,area_tile_161_93,area_tile_161_94,area_tile_161_95,area_tile_161_96,area_tile_161_97,area_tile_161_98,area_tile_161_99,area_tile_161_100,area_tile_161_101,area_tile_161_102,area_tile_161_103,area_tile_161_104,area_tile_161_105,area_tile_161_106,area_tile_161_107,area_tile_161_108,area_tile_161_109,area_tile_161_110,area_tile_161_111,area_tile_161_112,area_tile_161_113,area_tile_161_114,area_tile_161_115,area_tile_161_116,area_tile_161_117,area_tile_161_118,area_tile_161_119,area_tile_162_75,area_tile_162_76,area_tile_162_77,area_tile_162_78,area_tile_162_79,area_tile_162_80,area_tile_162_81,area_tile_162_82,area_tile_162_83,area_tile_162_84,area_tile_162_85,area_tile_162_86,area_tile_162_87,area_tile_162_88,area_tile_162_89,area_tile_162_90,area_tile_162_91,area_tile_162_92,area_tile_162_93,area_tile_162_94,area_tile_162_95,area_tile_162_96,area_tile_162_97,area_tile_162_98,area_tile_162_99,area_tile_162_100,area_tile_162_101,area_tile_162_102,area_tile_162_103,area_tile_162_104,area_tile_162_105,area_tile_162_106,area_tile_162_107,area_tile_162_108,area_tile_162_109,area_tile_162_110,area_tile_162_111,area_tile_162_112,area_tile_162_113,area_tile_162_114,area_tile_162_115,area_tile_162_116,area_tile_162_117,area_tile_162_118,area_tile_162_119,area_tile_163_75,area_tile_163_76,area_tile_163_77,area_tile_163_78,area_tile_163_79,area_tile_163_80,area_tile_163_81,area_tile_163_82,area_tile_163_83,area_tile_163_84,area_tile_163_85,area_tile_163_86,area_tile_163_87,area_tile_163_88,area_tile_163_89,area_tile_163_90,area_tile_163_91,area_tile_163_92,area_tile_163_93,area_tile_163_94,area_tile_163_95,area_tile_163_96,area_tile_163_97,area_tile_163_98,area_tile_163_99,area_tile_163_100,area_tile_163_101,area_tile_163_102,area_tile_163_103,area_tile_163_104,area_tile_163_105,area_tile_163_106,area_tile_163_107,area_tile_163_108,area_tile_163_109,area_tile_163_110,area_tile_163_111,area_tile_163_112,area_tile_163_113,area_tile_163_114,area_tile_163_115,area_tile_163_116,area_tile_163_117,area_tile_163_118,area_tile_163_119,area_tile_164_75,area_tile_164_76,area_tile_164_77,area_tile_164_78,area_tile_164_79,area_tile_164_80,area_tile_164_81,area_tile_164_82,area_tile_164_83,area_tile_164_84,area_tile_164_85,area_tile_164_86,area_tile_164_87,area_tile_164_88,area_tile_164_89,area_tile_164_90,area_tile_164_91,area_tile_164_92,area_tile_164_93,area_tile_164_94,area_tile_164_95,area_tile_164_96,area_tile_164_97,area_tile_164_98,area_tile_164_99,area_tile_164_100,area_tile_164_101,area_tile_164_102,area_tile_164_103,area_tile_164_104,area_tile_164_105,area_tile_164_106,area_tile_164_107,area_tile_164_108,area_tile_164_109,area_tile_164_110,area_tile_164_111,area_tile_164_112,area_tile_164_113,area_tile_164_114,area_tile_164_115,area_tile_164_116,area_tile_164_117,area_tile_164_118,area_tile_164_119,area_tile_165_75,area_tile_165_76,area_tile_165_77,area_tile_165_78,area_tile_165_79,area_tile_165_80,area_tile_165_81,area_tile_165_82,area_tile_165_83,area_tile_165_84,area_tile_165_85,area_tile_165_86,area_tile_165_87,area_tile_165_88,area_tile_165_89,area_tile_165_90,area_tile_165_91,area_tile_165_92,area_tile_165_93,area_tile_165_94,area_tile_165_95,area_tile_165_96,area_tile_165_97,area_tile_165_98,area_tile_165_99,area_tile_165_100,area_tile_165_101,area_tile_165_102,area_tile_165_103,area_tile_165_104,area_tile_165_105,area_tile_165_106,area_tile_165_107,area_tile_165_108,area_tile_165_109,area_tile_165_110,area_tile_165_111,area_tile_165_112,area_tile_165_113,area_tile_165_114,area_tile_165_115,area_tile_165_116,area_tile_165_117,area_tile_165_118,area_tile_165_119,area_tile_166_75,area_tile_166_76,area_tile_166_77,area_tile_166_78,area_tile_166_79,area_tile_166_80,area_tile_166_81,area_tile_166_82,area_tile_166_83,area_tile_166_84,area_tile_166_85,area_tile_166_86,area_tile_166_87,area_tile_166_88,area_tile_166_89,area_tile_166_90,area_tile_166_91,area_tile_166_92,area_tile_166_93,area_tile_166_94,area_tile_166_95,area_tile_166_96,area_tile_166_97,area_tile_166_98,area_tile_166_99,area_tile_166_100,area_tile_166_101,area_tile_166_102,area_tile_166_103,area_tile_166_104,area_tile_166_105,area_tile_166_106,area_tile_166_107,area_tile_166_108,area_tile_166_109,area_tile_166_110,area_tile_166_111,area_tile_166_112,area_tile_166_113,area_tile_166_114,area_tile_166_115,area_tile_166_116,area_tile_166_117,area_tile_166_118,area_tile_166_119,area_tile_167_75,area_tile_167_76,area_tile_167_77,area_tile_167_78,area_tile_167_79,area_tile_167_80,area_tile_167_81,area_tile_167_82,area_tile_167_83,area_tile_167_84,area_tile_167_85,area_tile_167_86,area_tile_167_87,area_tile_167_88,area_tile_167_89,area_tile_167_90,area_tile_167_91,area_tile_167_92,area_tile_167_93,area_tile_167_94,area_tile_167_95,area_tile_167_96,area_tile_167_97,area_tile_167_98,area_tile_167_99,area_tile_167_100,area_tile_167_101,area_tile_167_102,area_tile_167_103,area_tile_167_104,area_tile_167_105,area_tile_167_106,area_tile_167_107,area_tile_167_108,area_tile_167_109,area_tile_167_110,area_tile_167_111,area_tile_167_112,area_tile_167_113,area_tile_167_114,area_tile_167_115,area_tile_167_116,area_tile_167_117,area_tile_167_118,area_tile_167_119,area_tile_168_75,area_tile_168_76,area_tile_168_77,area_tile_168_78,area_tile_168_79,area_tile_168_80,area_tile_168_81,area_tile_168_82,area_tile_168_83,area_tile_168_84,area_tile_168_85,area_tile_168_86,area_tile_168_87,area_tile_168_88,area_tile_168_89,area_tile_168_90,area_tile_168_91,area_tile_168_92,area_tile_168_93,area_tile_168_94,area_tile_168_95,area_tile_168_96,area_tile_168_97,area_tile_168_98,area_tile_168_99,area_tile_168_100,area_tile_168_101,area_tile_168_102,area_tile_168_103,area_tile_168_104,area_tile_168_105,area_tile_168_106,area_tile_168_107,area_tile_168_108,area_tile_168_109,area_tile_168_110,area_tile_168_111,area_tile_168_112,area_tile_168_113,area_tile_168_114,area_tile_168_115,area_tile_168_116,area_tile_168_117,area_tile_168_118,area_tile_168_119,area_tile_169_75,area_tile_169_76,area_tile_169_77,area_tile_169_78,area_tile_169_79,area_tile_169_80,area_tile_169_81,area_tile_169_82,area_tile_169_83,area_tile_169_84,area_tile_169_85,area_tile_169_86,area_tile_169_87,area_tile_169_88,area_tile_169_89,area_tile_169_90,area_tile_169_91,area_tile_169_92,area_tile_169_93,area_tile_169_94,area_tile_169_95,area_tile_169_96,area_tile_169_97,area_tile_169_98,area_tile_169_99,area_tile_169_100,area_tile_169_101,area_tile_169_102,area_tile_169_103,area_tile_169_104,area_tile_169_105,area_tile_169_106,area_tile_169_107,area_tile_169_108,area_tile_169_109,area_tile_169_110,area_tile_169_111,area_tile_169_112,area_tile_169_113,area_tile_169_114,area_tile_169_115,area_tile_169_116,area_tile_169_117,area_tile_169_118,area_tile_169_119";
		InputParameter tileIDs = new InputParameter("tileIDs",tiles);//"area_tile_2_4,area_tile_12_2,area_tile_5_9,area_tile_12_14");//,area_tile_112_12,area_tile_15_19,area_tile_22_24,area_tile_122_22,area_tile_25_29");
		InputParameter id = new InputParameter("id","FusionRaster");
		InputParameter rows = new InputParameter("rows","201");
		InputParameter columns = new InputParameter("columns","201");
		//InputParameter binary = new InputParameter("binary","false");
		InputParameter threshold = new InputParameter("threshold","0.6");
		InputParameter interval = new InputParameter("output-interval", "20");
		
		HashMap<String, InputParameter> iparams=new HashMap<String, InputParameter>();
		iparams.put(tileIDs.getName(), tileIDs);
		iparams.put(rows.getName(), rows);
		iparams.put(columns.getName(), columns);
		//iparams.put(binary.getName(), binary);
		iparams.put(id.getName(), id);
		iparams.put(threshold.getName(), threshold);
		iparams.put(interval.getName(), interval);
		
		IAlgorithmContext context = new AlgorithmContext("1", "asciiRasterDualSISO");
		
		try {
			asciiRasterDualSISO.initialize(context, iparams);
		} catch (FormatterInitializationException e1) {
			e1.printStackTrace();
		}
		
		IDataElement data;
		
		double measurement = 0.3;
		data = new DataElement("test");
		data.setValue(measurement);
		
		for(int i=0 ; i<30 ; i++) {
			try {
				Date start = new Date();
				String output = (String)asciiRasterDualSISO.format(context, data);
				Date end = new Date();
				
				//System.out.println(output);
				System.out.println("Contextor time: "+(end.getTime() - start.getTime()));
				Thread.sleep(5000);
			} catch (FormatterException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
