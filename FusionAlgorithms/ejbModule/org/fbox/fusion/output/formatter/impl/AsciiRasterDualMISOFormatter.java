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
import org.fbox.fusion.output.formatter.data.AccountedArea;
import org.fbox.fusion.output.formatter.data.AreaPoint;

@Stateless (name="asciiRasterDualMISO")
@Remote ({IFormatter.class})
public class AsciiRasterDualMISOFormatter extends AbstractFormatter<String> {

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
					
					if(paramName.equalsIgnoreCase("id")) {
						state.setContextParameter(paramName, param.getValue());
					}
					
					if(paramName.equalsIgnoreCase("rows") || paramName.equalsIgnoreCase("columns")) {
						
						try{
							if(Integer.parseInt(param.getValue()) < 1)
								throw new FormatterInitializationException("A non valid number(" + param.getValue() + ") was specified as '"+paramName+"'. The given value must be at least 1.\n");
							
							state.setContextParameter(paramName, Integer.parseInt(param.getValue()));
							
							if(paramName.equalsIgnoreCase("rows")) {
								Integer.parseInt(param.getValue());
							}
							else {
								Integer.parseInt(param.getValue());
							}
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
			
			for (InputParameter param : paramValues) {
				String paramName=param.getName();
				if (optionalParameters.remove(paramName)) {

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
				if(s.equalsIgnoreCase("output-interval")) {
					state.setContextParameter(s, new Integer(0)); //output-interval context parameter
					state.setContextParameter("last-output-timestamp", new Long(0));
				}
			}
			if (!errorMessage.isEmpty()) {
				System.out.println(errorMessage);
				throw new FormatterInitializationException(errorMessage);
			}

			//Add context parameter to "lock" the respective contextor
			state.setContextParameter("locked", new Boolean(false));
			
			/**
			 * Create ArrayList that will hold the non accounted input data due to the timer
			 */
			ArrayList<IDataElement> dataElementsList = new ArrayList<IDataElement>();
			state.setContextParameter("nonAccountedElements", dataElementsList); // At the start, this list will be empty
		}
	}
	
	private String createAsciiRaster (String id, Integer rows, Integer columns, ArrayList<AccountedArea> area) {
		
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
		otherSymbols.setDecimalSeparator('.');
		DecimalFormat decimalFormat = new DecimalFormat("#.##", otherSymbols);
		
		String asciiRasterProbabilistic = null;
		String asciiRasterBinary = null;
		String concatenated = null;
		
		Double [][] asciiProbabilisticArray = new Double[rows][columns];
		Double [][] asciiBinaryArray = new Double[rows][columns];
		
		for(int i=0 ; i<rows ; i++) {
			Arrays.fill(asciiProbabilisticArray[i], Double.valueOf(decimalFormat.format(new Double(0.00))));
			Arrays.fill(asciiBinaryArray[i], Double.valueOf(decimalFormat.format(new Double(0.00))));
		}
		
		for(AccountedArea a : area) {
			for(AreaPoint point : a.getAreaTileIDs()) {
				asciiProbabilisticArray[point.getRow()][point.getColumn()] = Double.valueOf(decimalFormat.format(a.getPropValue()));
				asciiBinaryArray[point.getRow()][point.getColumn()] = Double.valueOf(decimalFormat.format(a.getThresholdedPropValue()));
			}
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
					asciiRasterProbabilistic = asciiRasterProbabilistic + String.format(Locale.ENGLISH, "%1$,.2f", asciiProbabilisticArray[i][j]) + "\n";
					asciiRasterBinary = asciiRasterBinary + Math.round(asciiBinaryArray[i][j].floatValue()) + "\n";
				}
				else {
					if( (j == columns - 1) && (i == rows - 1) ) { //Last row && Last column
						asciiRasterProbabilistic = asciiRasterProbabilistic + String.format(Locale.ENGLISH, "%1$,.2f", asciiProbabilisticArray[i][j]);
						asciiRasterBinary = asciiRasterBinary + Math.round(asciiBinaryArray[i][j].floatValue());
					}
					else {
						asciiRasterProbabilistic = asciiRasterProbabilistic + String.format(Locale.ENGLISH, "%1$,.2f", asciiProbabilisticArray[i][j]) + " ";
						asciiRasterBinary = asciiRasterBinary + Math.round(asciiBinaryArray[i][j].floatValue()) + " ";
					}
				}
			}
		}
		
		concatenated = asciiRasterBinary + "|" + asciiRasterProbabilistic;
		
		return concatenated;
	}
	
	@Override
	public String format(IContext state, IDataElement... dataArray) throws FormatterException {
		
		String formattedMessage=null;
		String id = (String)state.getContextParameter("id");
		
		ArrayList<IDataElement> dataElementsList = new ArrayList<IDataElement>();
		Integer row = -1, column = -1, rows = -1, columns = -1, interval;
		Double threshold = 0.0;
		Long lastOutputTimestamp, currentTimestamp;
		Boolean locked = false;
		
		threshold = Double.parseDouble(state.getContextParameter("threshold").toString());
		rows = (Integer)state.getContextParameter("rows");
		columns = (Integer)state.getContextParameter("columns");
		interval = (Integer)state.getContextParameter("output-interval") * (new Integer(1000)); //Convert seconds to milliseconds
		lastOutputTimestamp = (Long)state.getContextParameter("last-output-timestamp");
		locked = (Boolean)state.getContextParameter("locked");
		
		dataElementsList = (ArrayList<IDataElement>) state.getContextParameter("nonAccountedElements");
		
		currentTimestamp = (new Date()).getTime();
		
		// Check if the interval since the last output has passed and the bean is not locked due to the execution of another request
		if( ((currentTimestamp - lastOutputTimestamp) >= new Long(interval)) && (!locked) ) {

			System.out.println("[asciiRasterDualMISO-"+id+"] Diff: "+(currentTimestamp - lastOutputTimestamp)+"   Interval: "+interval+"   Locked: "+locked);
			System.out.println("[asciiRasterDualMISO-"+id+"] Locked Contextor!!!");
			state.setContextParameter("locked", new Boolean(true));

			ArrayList<IDataElement> retrieveElements = new ArrayList<IDataElement>();
			ArrayList<AccountedArea> tilesList = new ArrayList<AccountedArea>();
			
			retrieveElements.addAll(dataElementsList); // Add all the non-accounted dataElements till now due to the timer
			
			for(IDataElement argData : dataArray) {
				if(argData != null && argData.getValue() != null) {
					retrieveElements.add(argData);
				}
			}
						
			boolean generateNonZeroOutput = false;
			
			//Check values that come inside the formatter
			for(IDataElement argData : retrieveElements) {
				if(argData != null && argData.getValue() != null) {
					
					String tileIDs = null, number = null;
					Double propValue = null;
					
					try {
						StringTokenizer st = new StringTokenizer(argData.getValue().toString(),",");
						tileIDs = st.nextToken();
						number = st.nextToken();
						
						propValue = Double.parseDouble(number);
						
						if(propValue < 0.0 || propValue > 1.0) {
							state.setContextParameter("locked", new Boolean(true)); // Unlock contextor for further usage
							throw new FormatterException("The value for 'propValue' field in the input stream must be a probability that belongs to the range [0,1].\n");
						}
					}
					catch (NoSuchElementException e1) {
						state.setContextParameter("locked", new Boolean(true)); // Unlock contextor for further usage
						throw new FormatterException("The input of the \"" + this.getType() + "\" formatter must be of \"area_tile_x_y,propValue\" type.\n");
					}
					catch (NumberFormatException e3){
						state.setContextParameter("locked", new Boolean(true)); // Unlock contextor for further usage
						throw new FormatterException("A non valid number(" + number + ") was specified as 'propValue' field in the input stream.\n");
					}
					
					if(!propValue.equals(new Double(0.0))) { // Found non-zero value, generate output!!
						generateNonZeroOutput = true;
					}
					
					// Create the AccountedArea object and insert it in the respective list...
					AccountedArea area = new AccountedArea();
					
					// Retrieve the areaTileIDs...
					StringTokenizer st2 = new StringTokenizer(tileIDs, "|");
					
					while(st2.hasMoreElements()) { //Check the consistency of the "rows" and "columns" values with the given tileIDs
						
						try {
							StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "_"); // area_tile_x_y
		
							st3.nextToken(); //area
							st3.nextToken(); //tile
		
							row = Integer.parseInt(st3.nextToken()); //x
							column = Integer.parseInt(st3.nextToken()); //y
		
							if( (row >= rows) || (column >= columns) ) {
								state.setContextParameter("locked", new Boolean(true)); // Unlock contextor for further usage
								throw new FormatterException("The given tileIDs as input must have a row value in the [0,"+ (rows-1) +"] range and a column value in the [0,"+ (columns-1) +"] range respectively.\n");
							}
						}
						catch (NoSuchElementException e2) {
							state.setContextParameter("locked", new Boolean(true)); // Unlock contextor for further usage
							throw new FormatterException("The provided tileID \"" + tileIDs + "\" as input to the \""+ this.getType() +"\" formatter must be of \"area_tile_x_y\" format.\n");
						}
						
						area.addAreaTileID(new AreaPoint(row, column));
					}
					
					if(propValue < threshold) {
						area.setThresholdedPropValue(new Double(0.0));
					}
					else {
						area.setThresholdedPropValue(new Double(1.0));
					}
					
					area.setPropValue(propValue);
					
					tilesList.add(area); // Add the account area to the list with the points...
				}
			}
			
			if(generateNonZeroOutput) {
				formattedMessage = createAsciiRaster (id, rows, columns, tilesList);
				
//				currentTimestamp = (new Date()).getTime(); //Since the processing stage lasts over 1s (Without it, we eat the delay caused by the processing)
				state.setContextParameter("last-output-timestamp", currentTimestamp);
			}
			else {
				System.out.println("[asciiRasterDualMISO-"+id+"] OUTPUT: Zero-values...");
			}
			
			// Un-Lock the contextor
			state.setContextParameter("locked", new Boolean(false));
			
			// Update the historic values...
			state.setContextParameter("nonAccountedElements", retrieveElements);
		}
		else { // In this occasion, we have to store the input value in order not to lose the values...
			for(IDataElement argData : dataArray) {
				if(argData != null && argData.getValue() != null) {
					dataElementsList.add(argData);
				}
			}
			
			// Save the non-accessed data to the contextMemory..
			state.setContextParameter("nonAccountedElements", dataElementsList);
		
			System.out.println("[asciiRasterDualMISO-"+id+"] Diff: "+(currentTimestamp - lastOutputTimestamp)+"   Interval: "+interval+"   Locked: "+locked);
		}
		
		if(formattedMessage == null) {
			System.out.println("[asciiRasterDualMISO-"+id+"] OUTPUT: null");
		}
		else {
			System.out.println("[asciiRasterDualMISO-"+id+"] OUTPUT: PROPABILITY_MAPS "+rows+"x"+columns);
		}
		
		//System.out.println("[asciiRasterDualMISO-"+id+"] GENERAL OUTPUT: "+formattedMessage);
		
		return formattedMessage;
	}
	
	@Override
	public String[] getRequiredParameters() {
		String[] params={"id","rows","columns","threshold"};//"binary"};
		return params;
	}
	
	//Only if "binary = true"
	@Override
	public String[] getOptionalParameters() {
		String[] params={"output-interval"};
		return params;
	}

	@Override
	public String getType() {
		return "asciiRasterDualMISO";
	}

	@Override
	public boolean allowsMultipleInputs() {
		return true;
	}
	
	//Test implemented formatter
    public static void main(String args[]) {
    	
		AsciiRasterDualMISOFormatter asciiRasterDualMISO = new AsciiRasterDualMISOFormatter();

		InputParameter id = new InputParameter("id","FusionRaster");
		InputParameter rows = new InputParameter("rows","201");
		InputParameter columns = new InputParameter("columns","201");
		InputParameter threshold = new InputParameter("threshold","0.6");
		InputParameter interval = new InputParameter("output-interval", "12");
		
		HashMap<String, InputParameter> iparams=new HashMap<String, InputParameter>();
		iparams.put(rows.getName(), rows);
		iparams.put(columns.getName(), columns);
		iparams.put(id.getName(), id);
		iparams.put(threshold.getName(), threshold);
		iparams.put(interval.getName(), interval);
		
		IAlgorithmContext context = new AlgorithmContext("1", "asciiRasterDualMISO");
		
		try {
			asciiRasterDualMISO.initialize(context, iparams);
		} catch (FormatterInitializationException e1) {
			e1.printStackTrace();
		}
		
		/**
		 * Multiple AreaTileIDs per input
		 */
		IDataElement[] data=new DataElement[5];
		
		data[0]=new DataElement("test1");
		data[0].setValue("area_tile_141_74|area_tile_141_75|area_tile_141_76|area_tile_141_77|area_tile_141_78|area_tile_141_79|area_tile_141_80|area_tile_141_81|area_tile_141_82|area_tile_141_83|area_tile_141_84|area_tile_141_85|area_tile_141_86|area_tile_141_87|area_tile_141_88|area_tile_141_89|area_tile_141_90|area_tile_141_91,0.8");	
		data[1]=new DataElement("test2");
		data[1].setValue("area_tile_141_92|area_tile_141_93|area_tile_141_94|area_tile_141_95|area_tile_141_96|area_tile_141_97|area_tile_141_98|area_tile_141_99|area_tile_142_60|area_tile_142_61|area_tile_142_62|area_tile_142_63|area_tile_142_64|area_tile_142_65|area_tile_142_66|area_tile_142_67|area_tile_142_68|area_tile_142_69,0.8");
		data[2]=new DataElement("test3");
		data[2].setValue("area_tile_142_70|area_tile_142_71|area_tile_142_72|area_tile_142_73|area_tile_142_74|area_tile_142_75|area_tile_142_76|area_tile_142_77|area_tile_142_78|area_tile_142_79|area_tile_142_80|area_tile_142_81|area_tile_142_82|area_tile_142_83|area_tile_142_84|area_tile_142_85|area_tile_142_86|area_tile_142_87,0.8");
		data[3]=new DataElement("test4");
		data[3].setValue("area_tile_142_88|area_tile_142_89|area_tile_142_90|area_tile_142_91|area_tile_142_92|area_tile_142_93|area_tile_142_94|area_tile_142_95|area_tile_142_96|area_tile_142_97|area_tile_142_98|area_tile_142_99|area_tile_143_60|area_tile_143_61|area_tile_143_62|area_tile_143_63|area_tile_143_64|area_tile_143_65,0.8");
		data[4]=new DataElement("test5");
		data[4].setValue("area_tile_143_66|area_tile_143_67|area_tile_143_68|area_tile_143_69|area_tile_143_70|area_tile_143_71|area_tile_143_72|area_tile_143_73|area_tile_143_74|area_tile_143_75|area_tile_143_76|area_tile_143_77|area_tile_143_78|area_tile_143_79|area_tile_143_80|area_tile_143_81|area_tile_143_82|area_tile_143_83,0.8");
						
		for(int i=0 ; i<30 ; i++) {
			try {
				
				for( IDataElement de : data) {
					Date start = new Date();
					String output = (String)asciiRasterDualMISO.format(context, de);
					Date end = new Date();
					System.out.println(output);
					System.out.println("Contextor time: "+(end.getTime() - start.getTime())+" Current time: "+(new Date()).getTime());
				}
				
				Thread.sleep(5000);
			} catch (FormatterException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}