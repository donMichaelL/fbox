package org.fbox.fusion.algorithms.siso.ejb.impl;

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
import org.fbox.common.exception.FormatterException;
import org.fbox.common.exception.FormatterInitializationException;
import org.fbox.fusion.algorithms.siso.AbstractSISOAlgorithm;
import org.fbox.fusion.output.formatter.impl.AsciiRasterSISOFormatter;

@Stateless (name="asciiRasterSingle")
@Remote( {IAlgorithm.class, IStructure.class })
public class AsciiRaster extends AbstractSISOAlgorithm {

	/**
     * Default constructor. 
     */
    public AsciiRaster() {
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
    public void initialize(IAlgorithmContext state, HashMap<String,InputParameter> iparams) throws AlgorithmInitializationException {
    	
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
							throw new AlgorithmInitializationException("The expected format for the \""+paramName+"\" parameter is: \"area_tile_0_12,area_tile_2_3,...,area_tile_4_90\".\n");
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
							throw new AlgorithmInitializationException("ERROR --> Init (Required) Parameter '"+ paramName + "' for Formatter " + getType() +" can only take 'true' or 'false' as value.\n");
						else
							state.setContextParameter(paramName, param.getValue());
					}*/
					
					if(paramName.equalsIgnoreCase("rows") || paramName.equalsIgnoreCase("columns")) {
						
						try{
							if(Integer.parseInt(param.getValue()) < 1)
								throw new AlgorithmInitializationException("A non valid number(" + param.getValue() + ") was specified as '"+paramName+"'. The given value must be at least 1.\n");
							
							state.setContextParameter(paramName, Integer.parseInt(param.getValue()));
						}
						catch (NumberFormatException e){
							throw new AlgorithmInitializationException("A non valid number(" + param.getValue() + ") was specified as '"+paramName+"'.\n");
						}
					}
					
					//Check if the cutoff value belongs in the interval [0,1]
					if(paramName.equalsIgnoreCase("threshold")) {
						try{
							state.setContextParameter(paramName, param.getValue());
							
							if(Double.parseDouble(param.getValue()) < 0.0 || Double.parseDouble(param.getValue()) > 1.0) {
								throw new AlgorithmInitializationException("The value for 'threshold' parameter must be a probability that belongs to the range [0,1].\n");
							}
						}
						catch (NumberFormatException e){
							throw new AlgorithmInitializationException("A non valid number(" + param.getValue() + ") was specified as '"+paramName+"'.\n");
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
				throw new AlgorithmInitializationException(errorMessage);
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
						throw new AlgorithmInitializationException("The given tileIDs as input must have a row value in the [0,"+ (rows-1) +"] range and a column value in the [0,"+ (columns-1) +"] range respectively.\n");
					}
					
					tileIDsArray[counter][0] = row;
					tileIDsArray[counter][1] = column;
					
					counter++;
				}
			}
			catch (NoSuchElementException e1) {
				throw new AlgorithmInitializationException("The expected format for the 'tileIDs' parameter is: \"area_tile_0_12,area_tile_2_3,...,area_tile_4_90\".\n");
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
									throw new AlgorithmInitializationException("The value for 'threshold' parameter must be a probability that belongs to the range [0,1].\n");
								}
							}
							catch (NumberFormatException e){
								throw new AlgorithmInitializationException("A non valid number(" + param.getValue() + ") was specified as '"+paramName+"'.\n");
							}
						}
						*/
						//Check if the given parameter is the "output-interval"
						if(paramName.equalsIgnoreCase("output-interval")) {
							try {
								if(Integer.parseInt(param.getValue()) < new Integer(0)) {
									throw new AlgorithmInitializationException("The value for 'output-interval' parameter must be a non negative integer.\n");
								}
								
								// Set the appropriate values for the rest context parameters
								state.setContextParameter(paramName, Integer.parseInt(param.getValue())); //output-interval context parameter
								state.setContextParameter("last-output-timestamp", new Long(0));
							}
							catch (NumberFormatException e) {
								throw new AlgorithmInitializationException("A non valid number(" + param.getValue() + ") was specified as '"+paramName+"'. A non negative integer must be provided. \n");
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
					throw new AlgorithmInitializationException(errorMessage);
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
			asciiBinaryArray[tileIDs[i][0]][tileIDs[i][1]] = Double.valueOf(decimalFormat.format(propValue));
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
    protected Comparable<?> _update(IAlgorithmContext state, IDataElement measurement) throws AlgorithmExecutionException {
    	
    	String formattedMessage=null;
		Double propValue = new Double(0.0);
		Double thresholdedValue = new Double(0.0);
		Integer rows = 0, columns = 0, interval;
		Long lastOutputTimestamp, currentTimestamp;
		String id="";
		Integer tileIDsArray[][];
		Boolean locked = false;
		
		//Check the value that comes inside the formatter
		if(measurement != null) {
			if (measurement.getValue() instanceof Number) {
				
				tileIDsArray = (Integer[][])state.getContextParameter("tileIDs");
				id = (String)state.getContextParameter("id");
				rows = (Integer)state.getContextParameter("rows");
				columns = (Integer)state.getContextParameter("columns");
				interval = (Integer)state.getContextParameter("output-interval") * (new Integer(1000)); //Convert seconds to milliseconds
				lastOutputTimestamp = (Long)state.getContextParameter("last-output-timestamp");
				locked = (Boolean)state.getContextParameter("locked");
				
				if((Double)measurement.getValue() < 0.0 || (Double)measurement.getValue() > 1.0) {
					throw new AlgorithmExecutionException("The input value of the \""+this.getType()+"\" formatter must be a probability that belongs to the range [0,1].\n");
				}
				
				currentTimestamp = (new Date()).getTime();
				// Check if the interval since the last output has passed
				if( ((currentTimestamp - lastOutputTimestamp) >= new Long(interval)) && (!locked)) {
					
					// Handle lock policy to avoid concurrent access because of high processing time
					if( (interval != 0)) { // Lock the contextor
						state.setContextParameter("locked", new Boolean(true));
					}
						
					propValue = (Double)measurement.getValue();
					
					if(propValue < Double.parseDouble(state.getContextParameter("threshold").toString()))
						thresholdedValue = 0.0;
					else
						thresholdedValue = 1.0;
					
					formattedMessage = createAsciiRaster (tileIDsArray, propValue, id, rows, columns, thresholdedValue);
					
					currentTimestamp = (new Date()).getTime(); //Since the processing stage lasts over 1s
					state.setContextParameter("last-output-timestamp", currentTimestamp);
						
					// Un-Lock the contextor
					state.setContextParameter("locked", new Boolean(false));
					
				} //else, a null value will be returned and the respective adapter will not send any output outside the FBox
				else { // For debug purposes only!
					System.out.println("[asciiRasterSingle-"+id+"] Diff: "+(currentTimestamp - lastOutputTimestamp)+"   Interval: "+interval+"   Locked: "+locked);
				}
			} else {
				throw new AlgorithmExecutionException("Invalid data value detected("+measurement.getValue()+"). Value must be Double");
			}
		}
		
		if(formattedMessage == null) {
			System.out.println("[asciiRasterSingle-"+id+"] OUTPUT: null");
			return null;
		}
		else 
			System.out.println("[asciiRasterSingle-"+id+"] OUTPUT: PROPABILITY_MAP "+rows+"x"+columns);
		
		System.out.println("[asciiRasterSingle-"+id+"] GENERAL OUTPUT: "+formattedMessage);
		
		return formattedMessage;
    }
    
    @Override
    public String getType() {
		return "asciiRasterSingle";
    }
    
    //Test implemented algorithm
    public static void main(String args[]) {
    	
		AsciiRaster asciiRasterSISO = new AsciiRaster();

		InputParameter tileIDs = new InputParameter("tileIDs","area_tile_2_4,area_tile_12_2,area_tile_5_9,area_tile_12_14");//,area_tile_112_12,area_tile_15_19,area_tile_22_24,area_tile_122_22,area_tile_25_29");
		InputParameter id = new InputParameter("id","FusionRaster");
		InputParameter rows = new InputParameter("rows","335");
		InputParameter columns = new InputParameter("columns","335");
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
		
		IAlgorithmContext context = new AlgorithmContext("1", "asciiRasterSingle");
		
		try {
			asciiRasterSISO.initialize(context, iparams);
		} catch (AlgorithmInitializationException e1) {
			e1.printStackTrace();
		}
		
		IDataElement data;
		
		double measurement = 0.76;
		data = new DataElement("test");
		data.setValue(measurement);
		
		//for(int i=0 ; i<30 ; i++) {
			try {
				Date start = new Date();
				String output = (String)asciiRasterSISO.update(context, data).getValue();
				Date end = new Date();
				
				System.out.println(output);
				System.out.println("Contextor time: "+(end.getTime() - start.getTime()));
				final byte[] utf8Bytes = output.getBytes("UTF-8");
				System.out.println("Message size in bytes: "+utf8Bytes.length); 
				System.out.println("Message length: "+output.length());
		//		Thread.sleep(2000);
			} catch (AlgorithmExecutionException e) {
				e.printStackTrace();
			} //catch (InterruptedException e) {
				// TODO Auto-generated catch block
		//		e.printStackTrace();
		//	}
		//}
			catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
