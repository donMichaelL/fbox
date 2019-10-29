package org.fbox.fusion.output.formatter.impl;

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

import org.fbox.common.application.data.DataElement;
import org.fbox.common.data.AlgorithmContext;
import org.fbox.common.data.IAlgorithmContext;
import org.fbox.common.data.IContext;
import org.fbox.common.data.IDataElement;
import org.fbox.common.data.InputParameter;
import org.fbox.common.exception.FormatterException;
import org.fbox.common.exception.FormatterInitializationException;
import org.fbox.common.output.IFormatter;
import org.fbox.fusion.output.formatter.AbstractFormatter;

@Stateless (name="asciiRasterMISO")
@Remote ({IFormatter.class})
public class AsciiRasterMISOFormatter extends AbstractFormatter<String> {

	@Override
	public void initialize(IContext state, HashMap<String, InputParameter> iparams) throws FormatterInitializationException {
		
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
					
					//Check if we have a boolean value
					if(paramName.equalsIgnoreCase("binary")) {
						if(!param.getValue().equalsIgnoreCase("true") && !param.getValue().equalsIgnoreCase("false"))
							throw new FormatterInitializationException("ERROR --> Init (Required) Parameter '"+ paramName + "' for Formatter " + getType() +" can only take 'true' or 'false' as value.\n");
						else
							state.setContextParameter(paramName, param.getValue());
					}
					
					if(paramName.equalsIgnoreCase("id")) {
						state.setContextParameter(paramName, param.getValue());
					}
					
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
			
			//If there is no required parameter missing then we check the value of the "binary" parameter
			//If this value is true then we have to collect the optional parameters (threshold)
			//if(Boolean.parseBoolean(state.getContextParameter("binary").toString())) {
				
				//We have to create a probability matrix, hence the rest parameters must be loaded
				for (InputParameter param : paramValues) {
					String paramName=param.getName();
					if (optionalParameters.remove(paramName)) {
						//state.setContextParameter(paramName, param.getValue());
						
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
						
						//Check if the given parameter is the "output-interval"
						if(paramName.equalsIgnoreCase("output-interval")) {
							try {
								if(Integer.parseInt(param.getValue()) < new Integer(0)) {
									throw new FormatterInitializationException("The value for 'output-interval' parameter must be a non negative integer.\n");
								}
								
								// Set the appropriate values for the rest context parameters
								state.setContextParameter(paramName, Integer.parseInt(param.getValue())); //output-interval context parameter
								state.setContextParameter("last-output-timestamp", new Long(0));
							}
							catch (NumberFormatException e) {
								throw new FormatterInitializationException("A non valid number(" + param.getValue() + ") was specified as '"+paramName+"'. A non negative integer must be provided. \n");
							}
						}
					} 
				}

				//check missing optional parameters
				for (String s : optionalParameters) {
					if(s.equalsIgnoreCase("threshold") && Boolean.parseBoolean(state.getContextParameter("binary").toString())) //In this case (binary = true) the optional parameter "threshold" must be present
						errorMessage+="ERROR --> Init (Optional) Parameter '"+ s + "' is needed for Formatter " + getType() +" but has not been specified.\n";
				
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
		}
	}
	
	private String createAsciiRaster (Integer [][] tileIDs, String id, Integer rows, Integer columns, ArrayList<Double> propValues, boolean binary) {
		
		String asciiRaster=null;
		
		final DecimalFormat decimalFormat;
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
		otherSymbols.setDecimalSeparator('.');
		decimalFormat = new DecimalFormat("#.##", otherSymbols);
		
		Double [][] asciiArray = new Double[rows][columns];
		for(int i=0 ; i<rows ; i++)
			Arrays.fill(asciiArray[i], Double.valueOf(decimalFormat.format(new Double(0.00))));
		
		for(int i=0 ; i<tileIDs.length ; i++) {
			asciiArray[tileIDs[i][0]][tileIDs[i][1]] = Double.valueOf(decimalFormat.format(propValues.get(i)));
		}
		
		asciiRaster = "id: "+id+"\n";
		asciiRaster = asciiRaster + "rows: "+rows+"\n";
		asciiRaster = asciiRaster + "cols: "+columns+"\n";
		
		for(int i=0 ; i<rows ; i++) {
			for(int j=0 ; j<columns ; j++) {
				if( (j == columns - 1) && (i != rows - 1) ) { //Last column
					if(!binary)
						asciiRaster = asciiRaster + String.format(Locale.ENGLISH, "%1$,.2f", asciiArray[i][j]) + "\n";
					else
						asciiRaster = asciiRaster + Math.round(asciiArray[i][j].floatValue()) + "\n";
				}
				else {
					if( (j == columns - 1) && (i == rows - 1) ) { //Last row && Last column
						if(!binary)
							asciiRaster = asciiRaster + String.format(Locale.ENGLISH, "%1$,.2f", asciiArray[i][j]);
						else
							asciiRaster = asciiRaster + Math.round(asciiArray[i][j].floatValue());
					}
					else {
						if(!binary)
							asciiRaster = asciiRaster + String.format(Locale.ENGLISH, "%1$,.2f", asciiArray[i][j]) + " ";
						else
							asciiRaster = asciiRaster + Math.round(asciiArray[i][j].floatValue()) + " ";
					}
				}
			}
		}
		
		return asciiRaster;
	}
	
	@Override
	public String format(IContext state, IDataElement... dataArray) throws FormatterException {
		
		String formattedMessage=null;
		String id = (String)state.getContextParameter("id");
		
		//ArrayList<String> tileIDs = new ArrayList<String>();
		ArrayList<Double> propValues = new ArrayList<Double>();
		Integer row = -1, column = -1, rows = -1, columns = -1, counter = 0, interval;
		Boolean binary = Boolean.parseBoolean(state.getContextParameter("binary").toString());
		Double threshold = 0.0;
		Long lastOutputTimestamp, currentTimestamp;
		
		Integer [][] tileIDs = new Integer[dataArray.length][2];
		
		if(binary)
			threshold = Double.parseDouble(state.getContextParameter("threshold").toString());
		
		rows = (Integer)state.getContextParameter("rows");
		columns = (Integer)state.getContextParameter("columns");
		interval = (Integer)state.getContextParameter("output-interval") * (new Integer(1000)); //Convert seconds to milliseconds
		lastOutputTimestamp = (Long)state.getContextParameter("last-output-timestamp");
		
		currentTimestamp = (new Date()).getTime();
		// Check if the interval since the last output has passed
		if( (currentTimestamp - lastOutputTimestamp) >= new Long(interval) ) {
			//Check values that come inside the formatter
			for(IDataElement argData : dataArray) {
				if(argData != null && argData.getValue() != null) {
					
					String tileID = null, number = null;
					Double propValue = null;
					
					try {
						StringTokenizer st = new StringTokenizer(argData.getValue().toString(),",");
						tileID = st.nextToken();
						number = st.nextToken();
						
						propValue = Double.parseDouble(number);
						
						if(propValue < 0.0 || propValue > 1.0) {
							throw new FormatterException("The value for 'propValue' field in the input stream must be a probability that belongs to the range [0,1].\n");
						}
					}
					catch (NoSuchElementException e1) {
						throw new FormatterException("The input of the \"" + this.getType() + "\" formatter must be of \"area_tile_x_y,propValue\" type.\n");
					}
					catch (NumberFormatException e3){
						throw new FormatterException("A non valid number(" + number + ") was specified as 'propValue' field in the input stream.\n");
					}
					
					//Check the consistency of the "rows" and "columns" values with the given tileIDs
					try {
						StringTokenizer st2 = new StringTokenizer(tileID, "_");
	
						st2.nextToken();
						st2.nextToken();
	
						row = Integer.parseInt(st2.nextToken());
						column = Integer.parseInt(st2.nextToken());
	
						if( (row >= rows) || (column >= columns) ) {
							throw new FormatterException("The given tileIDs as input must have a row value in the [0,"+ (rows-1) +"] range and a column value in the [0,"+ (columns-1) +"] range respectively.\n");
						}
					}
					catch (NoSuchElementException e2) {
						throw new FormatterException("The provided tileID \"" + tileID + "\" as input to the \""+ this.getType() +"\" formatter must be of \"area_tile_x_y\" format.\n");
					}
					
					tileIDs[counter][0] = row;
					tileIDs[counter][1] = column;
					//tileIDs.add(tileID);
					
					if(binary) {
						if(propValue < threshold) {
							propValues.add(new Double(0.0));
						}
						else
							propValues.add(new Double(1.0));
					}
					else
						propValues.add(propValue);
					
					counter++;
				}
			}
			
			formattedMessage = createAsciiRaster (tileIDs, id, rows, columns, propValues, binary);
			
			currentTimestamp = (new Date()).getTime(); //Since the processing stage lasts over 1s
			state.setContextParameter("last-output-timestamp", currentTimestamp);
		}
		
		return formattedMessage;
	}
	
	@Override
	public String[] getRequiredParameters() {
		String[] params={"id","rows","columns","binary"};
		return params;
	}
	
	//Only if "binary = true"
	@Override
	public String[] getOptionalParameters() {
		String[] params={"threshold","output-interval"};
		return params;
	}

	@Override
	public String getType() {
		return "asciiRasterMISO";
	}

	@Override
	public boolean allowsMultipleInputs() {
		return true;
	}
	
	//Test implemented formatter
    public static void main(String args[]) {
    	
		AsciiRasterMISOFormatter asciiRasterMISO = new AsciiRasterMISOFormatter();

		InputParameter id = new InputParameter("id","FusionRaster");
		InputParameter rows = new InputParameter("rows","10");
		InputParameter columns = new InputParameter("columns","10");
		InputParameter binary = new InputParameter("binary","true");
		InputParameter threshold = new InputParameter("threshold","0.6");
		InputParameter interval = new InputParameter("output-interval", "10");
		
		HashMap<String, InputParameter> iparams=new HashMap<String, InputParameter>();
		iparams.put(rows.getName(), rows);
		iparams.put(columns.getName(), columns);
		iparams.put(binary.getName(), binary);
		iparams.put(id.getName(), id);
		iparams.put(threshold.getName(), threshold);
		iparams.put(interval.getName(), interval);
		
		IAlgorithmContext context = new AlgorithmContext("1", "asciiRasterMISO");
		
		try {
			asciiRasterMISO.initialize(context, iparams);
		} catch (FormatterInitializationException e1) {
			e1.printStackTrace();
		}
		
		IDataElement[] data=new DataElement[12];
		
		data[0]=new DataElement("test1");
		data[0].setValue("area_tile_2_3,0.64");	
		data[1]=new DataElement("test2");
		data[1].setValue("area_tile_2_4,0.64");
		data[2]=new DataElement("test3");
		data[2].setValue("area_tile_2_5,0.64");	
		data[3]=new DataElement("test4");
		data[3].setValue("area_tile_2_6,0.64");	
		data[4]=new DataElement("test5");
		data[4].setValue("area_tile_3_3,0.64");
		data[5]=new DataElement("test6");
		data[5].setValue("area_tile_3_4,0.82");	
		data[6]=new DataElement("test7");
		data[6].setValue("area_tile_3_5,0.82");	
		data[7]=new DataElement("test8");
		data[7].setValue("area_tile_3_6,0.64");
		data[8]=new DataElement("test9");
		data[8].setValue("area_tile_4_3,0.64");	
		data[9]=new DataElement("test10");
		data[9].setValue("area_tile_4_4,0.64");	
		data[10]=new DataElement("test11");
		data[10].setValue("area_tile_4_5,0.64");
		data[11]=new DataElement("test12");
		data[11].setValue("area_tile_4_6,0.64");
		
		for(int i=0 ; i<30 ; i++) {
			try {
				System.out.println(asciiRasterMISO.format(context, data));
				Thread.sleep(2000);
			} catch (FormatterException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
