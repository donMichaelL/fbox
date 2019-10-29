package org.fbox.fusion.algorithms.siso.ejb.impl;

import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.fbox.common.IStructure;
import org.fbox.common.algorithms.IAlgorithm;
import org.fbox.common.application.data.DataElement;
import org.fbox.common.application.data.MultiDataVector;
import org.fbox.common.data.AlgorithmContext;
import org.fbox.common.data.IAlgorithmContext;
import org.fbox.common.data.IDataElement;
import org.fbox.common.data.InputParameter;
import org.fbox.common.exception.AlgorithmExecutionException;
import org.fbox.common.exception.AlgorithmInitializationException;
import org.fbox.fusion.algorithms.siso.AbstractSISOAlgorithm;

@Stateless(name="BayesianSingle")
@Remote( { IAlgorithm.class, IStructure.class })
public class BayesianSingle extends AbstractSISOAlgorithm {
		
	public BayesianSingle(){
	}//construct
	
	
	@Override
    public void initialize(IAlgorithmContext state, HashMap<String, InputParameter> iparams) throws AlgorithmInitializationException {
		
		String[] hypothesisList=null;
		//String[] evidenceList=null;
		Double[][] evidenceSets=null;
		Double[][] probabilityMatrix=null;
		String outputType = null;
		
		//used temporary to store the matrix in string format as defined in application script
		String[][] tmpProbabilityMatrix=null;
		
		//initialize execution params
		Set<String> requiredParameters=new HashSet<String>(Arrays.asList(getRequiredParameters()));
		Collection<InputParameter> paramValues=iparams.values();
		for (InputParameter param : paramValues) {
			switch (param.getName()) { 
			case "hypothesis":	
				hypothesisList=((String)param.getValue()).split(";");	// the final list of hypothesis				
				Set<String> checkSet=new HashSet<String>();																			
				//check validity of format 
				if (hypothesisList.length<=0) {
					throw new AlgorithmInitializationException("Wrong format for parameter:" + param.getName()+". Acceptable hypothesis must be of the following format h1;h2;h3");
				}				
				//check validity of values
				for (String hypothesis: hypothesisList) {
					if (hypothesis.isEmpty()) {
						throw new AlgorithmInitializationException("Ivalid hypothesis value. A hypothesis cannot be empty or null");
					} else {
						if (checkSet.contains(hypothesis))  {
							throw new AlgorithmInitializationException("Duplicate hypothesis value found. Hypothesis "+ hypothesis +" has been specified more than once");	
						}
					}
				}
				//remove it from params list
				requiredParameters.remove(param.getName());				
				break;
			case "evidence": 
				String evidenceStr[]=param.getValue().split(";");
				if (evidenceStr.length<2 || evidenceStr.length>3) {
					throw new AlgorithmInitializationException("Ivalid evidence specificied. Acceptable values should be of the following format low;high or low;high;N");					
				}
				
				Double lowBound;
				try {
					lowBound=Double.parseDouble(evidenceStr[0]);
				} catch (NumberFormatException e) {
					throw new AlgorithmInitializationException("Low bound of evidence should be double");
				}
				
				Double highBound;
				try {
					highBound=Double.parseDouble(evidenceStr[1]);
				} catch (NumberFormatException e) {
					throw new AlgorithmInitializationException("High bound of evidence should be double");
				}
				
				Integer m=1;
				try {
					//System.out.println(evidenceStr[2]);
					if (evidenceStr.length>2) {
						m=Integer.parseInt(evidenceStr[2]);
						//System.out.println("Parameter M set to "+ m);
						if (m<1) {
							throw new AlgorithmInitializationException("Number of evidences (M) should be a positive integer greater or equal to 1 (>=1)");		
						}
					}
				} catch (NumberFormatException e) {
					throw new AlgorithmInitializationException("Number of evidences (M) should be integer");
				}	
				
				if (highBound<lowBound) {
					throw new AlgorithmInitializationException("High Evidence Bound ("+ highBound +") cannot be lower than Low Evidence Bound ("+lowBound+")");
				}
				
				Double step=(highBound-lowBound)/(double) m;				
				//evidenceList=new String[m];
				evidenceSets=new Double[m][2];
				for (int i=0;i<m;i++) {
					evidenceSets[i][0]=lowBound+i*step;
					evidenceSets[i][1]=lowBound+(i+1)*step;
					
					//System.out.println("Field ["+(i+1)+"]: {"+evidenceSets[i][0]+","+evidenceSets[i][1]+"}");
					//evidenceList[i]=Double.toString(lowBound+i*step)+" - "+Double.toString(lowBound+(i+1)*step);
				}
				
				//remove it from params list
				requiredParameters.remove(param.getName());
				break;
			case "probability-matrix":
				String[] rows=param.getValue().split(";");
				tmpProbabilityMatrix=new String[rows.length][];
				int i=0;
				for (String row:rows) {
					tmpProbabilityMatrix[i++]=row.split(",");
				}
				//remove it from params list
				requiredParameters.remove(param.getName());
				break;		
			case "output-type":
				outputType = (String)param.getValue();
				
				if(!outputType.equalsIgnoreCase("single") && !outputType.equalsIgnoreCase("multi"))
					throw new AlgorithmInitializationException("Output-type parameter's value can only be \"single\" (provide the winner hypothesis) or \"multi\" (provide all the calculated PDF).");
				
				requiredParameters.remove(param.getName());
				break;		
			default:
				System.out.println("WARNING --> Input Parameter '"+ param.getName() + "' not applicable for Algorithm " + getType() +". Will be Ignored.");
			}
		}				
		
		//check missing parameters
		String errorMessage="";
		for (String s : requiredParameters) {
			errorMessage+="ERROR --> Input Parameter '"+ s + "' is needed but has not been specified.\n";
		}
		if (!errorMessage.isEmpty()) {
			//System.out.println(errorMessage);
			throw new AlgorithmInitializationException(errorMessage);
		}		

		int M=evidenceSets.length;
		int N=hypothesisList.length;

		if (N!=(tmpProbabilityMatrix.length) || M!=tmpProbabilityMatrix[0].length) {
			throw new AlgorithmInitializationException("The probability matrix does not comply with the evidence and hypothesis parameters. Matrix should be if size " +N+"x"+M);
		}
		
		probabilityMatrix=new Double[N][M];
		for (int j=0;j<M;j++) {
			BigDecimal sumOfProbabilitiesPerHypothesis=new BigDecimal(0.0);
			for (int i=0;i<N;i++) {
				try {
					//System.out.println("("+i+","+j+")="+tmpProbabilityMatrix[i][j]);
					probabilityMatrix[i][j]=Double.parseDouble(tmpProbabilityMatrix[i][j]);
				} catch (NumberFormatException e) {
					throw new AlgorithmInitializationException("Invalid value in probability matrix in position ("+i+","+j+"). Value should be double");
				}
				//System.out.println(probabilityMatrix[i][j]);
				sumOfProbabilitiesPerHypothesis=sumOfProbabilitiesPerHypothesis.add(new BigDecimal(probabilityMatrix[i][j]));
				//System.out.println("sum="+sumOfProbabilitiesPerHypothesis.doubleValue());
			}
			if (sumOfProbabilitiesPerHypothesis.doubleValue()!=1.0) {
				throw new AlgorithmInitializationException("Error in probability matrix. Sum of probabilities for all hypothesis per evidence should sum up to 1");
			}
		}			
		
		//add to context
		state.setContextParameter("evidence", evidenceSets);
		state.setContextParameter("output-type", outputType);
		state.setContextParameter("hypothesis", hypothesisList);		
		state.setContextParameter("probability-matrix", probabilityMatrix);
		
		Double[] posteriorProbabilties=createPosteriorProbabilityVector(N);
		MultiDataVector posteriorVector=new MultiDataVector(N, hypothesisList);
		posteriorVector.setData(posteriorProbabilties);
		state.setContextParameter("posterior-vector",posteriorVector); //initialize posterior probability vector		
	}
		
	private int determineEvidenceIndex(Double[][] evidenceSets, Double value) {
		int evidenceIndex=0;
		
		if (value<evidenceSets[0][0] || value>evidenceSets[evidenceSets.length-1][1]) {
			evidenceIndex=-1;
		} else {			
			for (int i=0;i<evidenceSets.length;i++) {
				//System.out.print("value="+value +" checking if in ("+evidenceSets[i][0]+","+evidenceSets[i][1] +"]");
				//The only non covered case is if value==evidenceSets[0][0] where it will return 0 as required
				if (value>evidenceSets[i][0] && value<=evidenceSets[i][1])  {//if it is within the specified limits
					evidenceIndex=i;
					//System.out.print("--->success");					
					break;
				}
				//System.out.print("--->failure");
			}
		}
		
		//System.out.println(":Evidence determined===>"+evidenceIndex);
		return evidenceIndex; 
	}
	
	private Double[] createPosteriorProbabilityVector(int numOfHypothesis){				
		Double[] posteriorProbabilityVector=new Double[numOfHypothesis];
		Double initialProbabilityPerHypothesis=1.0/numOfHypothesis; 
		
		for(int i=0;i<numOfHypothesis;i++){
			posteriorProbabilityVector[i] = initialProbabilityPerHypothesis;
			//System.out.println("Posterior["+i+"]="+posteriorProbabilityVector[i]);
		}//loop for all hypotheses
		
		return posteriorProbabilityVector;
	}//initialize the posterior probability
		
	protected Double[] getProbabilityDistribution(Double[][] matrix, int evidenceIndex){
		int N=matrix.length;

		Double[] distribution = new Double[N];
		for(int i=0;i<N;i++){
			distribution[i] = matrix[i][evidenceIndex];
		}//fill the distribution
		return distribution;
	}//returns the probability distribution related to the evidence index {1,...,M}
	
	protected Double[] product(Double[] posteriorProbabilityVector, Double[] observation){
		Double[] distribution = new Double[posteriorProbabilityVector.length];
		for(int i=0;i<posteriorProbabilityVector.length;i++){
			distribution[i] = posteriorProbabilityVector[i] * observation[i];
		}//fill the distribution
		return distribution;		
	}//calculate the product of two vectors
	
	public void calculatePosteriorProbabilityDistribution(Double[][] matrix, int evidenceIndex, Double[] posteriorProbabilityVector){
		Double[] obervedDistribution = this.getProbabilityDistribution(matrix, evidenceIndex);
		Double[] mediate = this.product(posteriorProbabilityVector, obervedDistribution);
		Double sum = 0.0;
		for(int i=0;i<posteriorProbabilityVector.length; i++){
			sum += mediate[i];
		}//create the denominator
		//normalizing...
		for(int i=0;i<posteriorProbabilityVector.length; i++){
			posteriorProbabilityVector[i] = mediate[i]/sum;
		}//create the denominator
	}//return the posterior probability distribution, i.e., fusion
	
	
	//for debugging purposes
	public void printPosteriorDistribution(IAlgorithmContext state){
		if (state!=null) {
			MultiDataVector posteriorProbability=(MultiDataVector)state.getContextParameter("posterior-vector");
			if (posteriorProbability!=null) {
				System.out.println("Posterior-Vector="+posteriorProbability.toString());
			}
			
		}
	}//print the posterior distribution

	public String getType() {
		return "BayesianSingle";
	}

	public String[] getRequiredParameters() {
		String[] params={"hypothesis", "evidence", "probability-matrix", "output-type"};
		return params;
	}

	//This won't be needed in a SISO implementation
	private IDataElement[] getNewValuesOnly(IAlgorithmContext state, IDataElement[] measurements) {
		
		IDataElement[] dataToReturn=null;
		ArrayList<IDataElement> newValues=new ArrayList<>();
		HashMap<String, IDataElement> bufferedData;
		
		//get buffered values
		Object obj=state.getContextParameter("OldValuesBuffer");
		
		//if buffered values exist then compare with new values to find which are newer
		if (obj!=null) {			
			
			bufferedData=(HashMap<String,IDataElement>)obj;
			for (IDataElement data : measurements) {
				IDataElement oldValue=bufferedData.put(data.getId(),data); //add new data element to buffer of old values
				
				//if an old value exists
				if (oldValue!=null) {
					//TODO check if comparison based on timestamps is preferable
					if (data.getTimestamp().after(oldValue.getTimestamp())) { //if it is older
						newValues.add(data); //add data to the list of dataElements that will be returned
					}
				}
			}
			dataToReturn=newValues.toArray(new IDataElement[newValues.size()]);
		} else { //if no buffered values exist (first iteration of algorithm) then ...
			
			//create the OldValuesBuffer entries
			bufferedData=new HashMap<String, IDataElement>();
			state.setContextParameter("OldValuesBuffer", bufferedData);
			for (IDataElement measurement : measurements) {
				if (measurement!=null) {
					bufferedData.put(measurement.getId(), measurement);
				}
			}
			
			//return all elements
			dataToReturn=measurements; 
		}
		
		return dataToReturn;
	}
	
	@Override
	protected Comparable<?> _update(IAlgorithmContext state, IDataElement measurement) throws AlgorithmExecutionException {
		
		//printPosteriorDistribution(state);
		
		MultiDataVector posteriorVector = (MultiDataVector)state.getContextParameter("posterior-vector");
		Double[] posteriorProbabilityVector=(Double[])posteriorVector.getData();
		
		String outputType = (String)state.getContextParameter("output-type");

		if (measurement==null || !(measurement.getValue() instanceof Double)) {
			System.out.println("ERROR in provided data! Unable to run algorithm for non Double or null values. Value will be ignored");
		} else {
			Double value=(Double)measurement.getValue();
			Double[][] evidence=(Double[][])state.getContextParameter("evidence");
			Double[][] probMatrix=(Double[][])state.getContextParameter("probability-matrix");

			//first determine evidenceIndex for specific observation;
			int evidenceIndex = determineEvidenceIndex(evidence,value);

			if (evidenceIndex >= 0) {
				calculatePosteriorProbabilityDistribution(probMatrix, evidenceIndex, posteriorProbabilityVector);
			}
			else
				throw new AlgorithmExecutionException("Input value {"+value+"} isn't inside the pre-specified limits of evidence table");
			//System.out.print("EvidenceIndex = {"+evidenceIndex +"}");
		}
		
		//Return the winner from the hypothesis
		//The output must contain also the probability of the winner, along with the position of the hypothesis
		//printPosteriorDistribution(state);
		//System.out.println(winnerHypothesis(state, posteriorVector).toString());
		
		if(outputType.equalsIgnoreCase("single"))
			return winnerHypothesis(state, posteriorVector);
		else
			return posteriorVector;
	}
	
	//This method is for locating the "winner" of the available hypothesis
	protected MultiDataVector winnerHypothesis(IAlgorithmContext state, MultiDataVector posteriorVector){
		
		int attributes = 3;
		String[] metaDataList = {"hypothesis", "index", "probability"};
		String[] hypothesisList = (String [])state.getContextParameter("hypothesis");
		MultiDataVector winner = new MultiDataVector(attributes, metaDataList);
		
		Entry<Integer, Comparable> maxElement = posteriorVector.getMaxValueElement();
		
		winner.setDataElement("hypothesis", hypothesisList[maxElement.getKey()]); //Thewroume pws hypothesisList kai posteriorVector einai sygronismena!!!
		winner.setDataElement("index", maxElement.getKey());
		winner.setDataElement("probability", maxElement.getValue());
		
		return winner;		
	}//calculate the product of two vectors

	public static void main(String[] argv) {
		
		BayesianSingle bayesian = new BayesianSingle();
		//set some trial values
		//InputParameter evidence=new InputParameter("evidence","0;30;3");
		//InputParameter hypothesis=new InputParameter("hypothesis","fire;perhaps-fire;no-fire");
		//InputParameter probMatrix=new InputParameter("probability-matrix", "0.1,0.2,0.6;0.3,0.3,0.3;0.6,0.5,0.1");
		
		InputParameter evidence=new InputParameter("evidence","0;1;3");
		InputParameter hypothesis=new InputParameter("hypothesis","no-fire;fire");
		InputParameter probMatrix=new InputParameter("probability-matrix", "0.8,0.1,0.6;0.2,0.9,0.4");
		InputParameter outputType=new InputParameter("output-type","single");
		
		HashMap<String, InputParameter> iparams=new HashMap<String, InputParameter>();
		iparams.put(evidence.getName(), evidence);
		iparams.put(hypothesis.getName(),hypothesis);
		iparams.put(probMatrix.getName(), probMatrix);
		iparams.put(outputType.getName(), outputType);
		
		IAlgorithmContext context = new AlgorithmContext("1", "Bayesian");
		IDataElement data = new DataElement("test");
		
		try {
			bayesian.initialize(context, iparams);
		} catch (AlgorithmInitializationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//a loop for 100 sensor readings. Each reading corresponds to a piece of evidence
		Random generator = new Random(19580427);
		for(int i = 0; i < 100; i++){
			
			int evidenceIndex = generator.nextInt(3);
			Double value = evidenceIndex * 0.4;
			
			System.out.print(" observation: evidence = {"+evidenceIndex+"} , value = {"+value+"}   |   ");
			data.setValue(value);
			
			MultiDataVector posteriorProbability = null;
			
			try {
				posteriorProbability = (MultiDataVector) bayesian.update(context, data).getValue();
				//posteriorProbability = bayesian.update(context, data);
			} catch (AlgorithmExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//System.out.println(context.getData());
			//System.out.println("P{H0}="+posteriorProbability.getDataElement("no-fire")+"  P{H1}="+posteriorProbability.getDataElement("fire"));
			System.out.println(posteriorProbability.toString());
			//System.out.println("\n---------Iteration "+ i+" END");
			
		}//fusion for all sensors
		
	}//a trial 

	
}
