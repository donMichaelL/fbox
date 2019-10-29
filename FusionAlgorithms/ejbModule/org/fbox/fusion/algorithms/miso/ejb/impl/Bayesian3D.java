package org.fbox.fusion.algorithms.miso.ejb.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

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
import org.fbox.fusion.algorithms.miso.AbstractMISOAlgorithm;

@Stateless(name="Bayesian3D")
@Remote( { IAlgorithm.class, IStructure.class })
public class Bayesian3D extends AbstractMISOAlgorithm {
		
	public Bayesian3D(){
	}//construct
	
	@Override
    public void initialize(IAlgorithmContext state, HashMap<String, InputParameter> iparams) throws AlgorithmInitializationException {
		
		String[] hypothesisList=null;
		String hypoToMonitor=null;
		//String[] evidenceList=null;
		Double[][] evidenceSets1=null;
		Double[][] evidenceSets2=null;
		Double[][][] probabilityMatrix=null;
		
		//used temporary to store the matrix in string format as defined in application script
		LinkedList<String[][]> initMatrix = new LinkedList<String[][]>();
		String[][] tmpProbabilityMatrix=null; //Each node of the linked list will contain such a matrix
		
		//initialize execution params
		Set<String> requiredParameters=new HashSet<String>(Arrays.asList(getRequiredParameters()));
		Collection<InputParameter> paramValues=iparams.values();
		for (InputParameter param : paramValues) {
			switch (param.getName()) { 
			case "hypothesis":	//OK
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
			case "evidence1":
				String evidenceStr1[]=param.getValue().split(";");
				if (evidenceStr1.length<2 || evidenceStr1.length>3) {
					throw new AlgorithmInitializationException("Ivalid evidence specificied. Acceptable values should be of the following format low;high or low;high;N");					
				}
				
				Double lowBound1;
				try {
					lowBound1=Double.parseDouble(evidenceStr1[0]);
				} catch (NumberFormatException e) {
					throw new AlgorithmInitializationException("Low bound of evidence should be double");
				}
				
				Double highBound1;
				try {
					highBound1=Double.parseDouble(evidenceStr1[1]);
				} catch (NumberFormatException e) {
					throw new AlgorithmInitializationException("High bound of evidence should be double");
				}
				
				Integer m=1;
				try {
					//System.out.println(evidenceStr[2]);
					if (evidenceStr1.length>2) {
						m=Integer.parseInt(evidenceStr1[2]);
						//System.out.println("Parameter M set to "+ m);
						if (m<1) {
							throw new AlgorithmInitializationException("Number of evidences (M) should be a positive integer greater or equal to 1 (>=1)");		
						}
					}
				} catch (NumberFormatException e) {
					throw new AlgorithmInitializationException("Number of evidences (M) should be integer");
				}	
				
				if (highBound1<lowBound1) {
					throw new AlgorithmInitializationException("High Evidence Bound ("+ highBound1 +") cannot be lower than Low Evidence Bound ("+lowBound1+")");
				}
				
				Double step1=(highBound1-lowBound1)/m;				
				//evidenceList=new String[m];
				evidenceSets1=new Double[m][2];
				for (int i=0;i<m;i++) {
					evidenceSets1[i][0]=lowBound1+i*step1;
					evidenceSets1[i][1]=lowBound1+(i+1)*step1;
					//evidenceList[i]=Double.toString(lowBound+i*step)+" - "+Double.toString(lowBound+(i+1)*step);
				}
				//remove it from params list
				requiredParameters.remove(param.getName());
				break;
			case "evidence2":
				String evidenceStr2[]=param.getValue().split(";");
				if (evidenceStr2.length<2 || evidenceStr2.length>3) {
					throw new AlgorithmInitializationException("Ivalid evidence specificied. Acceptable values should be of the following format low;high or low;high;N");					
				}
				
				Double lowBound2;
				try {
					lowBound2=Double.parseDouble(evidenceStr2[0]);
				} catch (NumberFormatException e) {
					throw new AlgorithmInitializationException("Low bound of evidence should be double");
				}
				
				Double highBound2;
				try {
					highBound2=Double.parseDouble(evidenceStr2[1]);
				} catch (NumberFormatException e) {
					throw new AlgorithmInitializationException("High bound of evidence should be double");
				}
				
				Integer k=1;
				try {
					//System.out.println(evidenceStr[2]);
					if (evidenceStr2.length>2) {
						k=Integer.parseInt(evidenceStr2[2]);
						//System.out.println("Parameter M set to "+ m);
						if (k<1) {
							throw new AlgorithmInitializationException("Number of evidences (M) should be a positive integer greater or equal to 1 (>=1)");		
						}
					}
				} catch (NumberFormatException e) {
					throw new AlgorithmInitializationException("Number of evidences (M) should be integer");
				}	
				
				if (highBound2<lowBound2) {
					throw new AlgorithmInitializationException("High Evidence Bound ("+ highBound2 +") cannot be lower than Low Evidence Bound ("+lowBound2+")");
				}
				
				Double step2=(highBound2-lowBound2)/k;				
				//evidenceList=new String[m];
				evidenceSets2=new Double[k][2];
				for (int i=0;i<k;i++) {
					evidenceSets2[i][0]=lowBound2+i*step2;
					evidenceSets2[i][1]=lowBound2+(i+1)*step2;
					//evidenceList[i]=Double.toString(lowBound+i*step)+" - "+Double.toString(lowBound+(i+1)*step);
				}
				//remove it from params list
				requiredParameters.remove(param.getName());
				break;
			case "probability-matrix":
				String[] hypoMatrix = param.getValue().split("\\|");
				
				//for(int j=0 ; j<hypoMatrix.length ; j++)
				//	System.out.println(hypoMatrix[j]);
				
				String[] rows = null;
				for(int j=0 ; j<hypoMatrix.length ; j++)
				{
					rows = hypoMatrix[j].split(";");
					tmpProbabilityMatrix = new String[rows.length][];
					int i=0;
					for (String row:rows) {
						//System.out.println(row);
						tmpProbabilityMatrix[i++]=row.split(",");
					}
					
					initMatrix.add(tmpProbabilityMatrix);
					
					rows = null;
					tmpProbabilityMatrix = null;
				}
				
				//remove it from params list
				requiredParameters.remove(param.getName());
				break;				
			case "hypoToMonitor":
				hypoToMonitor = (String)param.getValue();
				
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

		int M=evidenceSets1.length;
		int K=evidenceSets2.length;
		int N=hypothesisList.length;
		//System.out.println("M="+M+"\tK="+K+"\tN="+N);
		//System.out.println("M="+initMatrix.getFirst().length+"\tK="+initMatrix.getFirst()[0].length+"\tN="+initMatrix.size());
		
		//      z (hypotheses)                    y (rows)                            x (columns)           
		if (N!=(initMatrix.size()) || M!=initMatrix.getFirst().length || K!=initMatrix.getFirst()[0].length) {
			throw new AlgorithmInitializationException("The probability matrix does not comply with the evidences and hypothesis parameters. Matrix should be of size ["+M+"]x["+K+"]x["+N+"]");
		}
		
		//Check the validity of the given hypothesis to monitor
		if(!hypoToMonitor.equalsIgnoreCase("all")) {
			boolean exists = false;
			for (String hypothesis: hypothesisList) {
				if(hypothesis.equalsIgnoreCase(hypoToMonitor)) {
					exists = true;
					break;
				}
			}
			
			if(!exists)
				throw new AlgorithmInitializationException("The given hypothesis matrix does not contain the hypothesis {"+hypoToMonitor+"} that you want to monitor");
		}
		
		probabilityMatrix=new Double[M][K][N];

		for (int r=0 ; r<M ; r++) {
			for (int q=0 ; q<K ; q++) {
				
				BigDecimal sumOfProbabilitiesPerHypothesis=new BigDecimal(0.0);
				
				for (int j=0;j<initMatrix.size();j++) {
					try {
						//System.out.println("("+i+","+j+")="+tmpProbabilityMatrix[i][j]);
						probabilityMatrix[r][q][j]=Double.parseDouble(initMatrix.get(j)[r][q]);
					} catch (NumberFormatException e) {
						throw new AlgorithmInitializationException("Invalid value in probability matrix in position ("+r+","+q+","+j+"). Value should be double");
					}
					
					sumOfProbabilitiesPerHypothesis=sumOfProbabilitiesPerHypothesis.add(new BigDecimal(probabilityMatrix[r][q][j]));
				}
				
				if (sumOfProbabilitiesPerHypothesis.doubleValue()!=1.0) {
					throw new AlgorithmInitializationException("Error in ("+r+","+q+") location of probability matrix. Sum of probabilities for all hypothesis per joint evidences should sum up to 1, but their sum is {"+sumOfProbabilitiesPerHypothesis.doubleValue()+"}");
				}
			}
		}			
		
		//add to context
		state.setContextParameter("evidence1", evidenceSets1);
		state.setContextParameter("evidence2", evidenceSets2);
		state.setContextParameter("hypothesis", hypothesisList);
		state.setContextParameter("hypoToMonitor", hypoToMonitor);
		state.setContextParameter("probability-matrix", probabilityMatrix);
		
		/*
		//Print probmatrix:
		String toPrint = null;
		for(int i=0 ; i<N ; i++) { //For each hypothesis 
			toPrint = "Hypothesis ["+(i+1)+"]: \n";
			for(int j=0; j<M ; j++) { //For each row (evidence2)
				for(int k=0; k<K ; k++) { //For each column (evidence1)
					toPrint = toPrint + probabilityMatrix[j][k][i] + "\t";
				}
				toPrint = toPrint + "\n";
			}
			System.out.println(toPrint);
			toPrint = null;
		}*/
		
		Double[] posteriorProbabilties=createPosteriorProbabilityVector(N);
		MultiDataVector posteriorVector=new MultiDataVector(N, hypothesisList);
		posteriorVector.setData(posteriorProbabilties);
		state.setContextParameter("posterior-vector",posteriorVector); //initialize posterior probability vector		
    }
		
	//Use it twice to determine the evidenceIndex's location for both inputs!!
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

	//																		 (columns)				(rows)
	protected Double[] getProbabilityDistribution(Double[][][] matrix, int evidenceIndex1, int evidenceIndex2){
		int N=matrix.length;

		Double[] distribution = new Double[N];
		for(int i=0;i<N;i++){
			distribution[i] = matrix[evidenceIndex2][evidenceIndex1][i];
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
	
	public void calculatePosteriorProbabilityDistribution(Double[][][] matrix, int evidenceIndexA, int evidenceIndexB, Double[] posteriorProbabilityVector){
		Double[] obervedDistribution = this.getProbabilityDistribution(matrix, evidenceIndexA, evidenceIndexB);
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

	@Override
	public String getType() {
		return "Bayesian3D";
	}

	@Override
	public String[] getRequiredParameters() {
		String[] params={"hypothesis", "evidence1", "evidence2", "probability-matrix", "hypoToMonitor"};
		return params;
	}

	//This method is for locating the "winner" of the available hypotheses
	protected MultiDataVector winnerHypothesis(IAlgorithmContext state, MultiDataVector posteriorVector){

		int attributes = 2;
		String[] metaDataList = {"hypothesis", "probability"};
		String[] hypothesisList = (String [])state.getContextParameter("hypothesis");
		MultiDataVector winner = new MultiDataVector(attributes, metaDataList);

		Entry<Integer, Comparable> maxElement = posteriorVector.getMaxValueElement();

		winner.setDataElement("hypothesis", hypothesisList[maxElement.getKey()]); //Thewroume pws hypothesisList kai posteriorVector einai sygxronismena!!!
		winner.setDataElement("probability", maxElement.getValue());

		return winner;		
	}//calculate the product of two vectors

	@Override
	protected Comparable<?> _update(IAlgorithmContext state, IDataElement[] measurements) throws AlgorithmExecutionException {
		
		String hypoToMonitor = null;
		//System.out.println("\nFound "+ newMeasurements.length + " new measurements of "+ measurements.length  + " total measurements!!");

		MultiDataVector posteriorVector = (MultiDataVector)state.getContextParameter("posterior-vector");
		Double[] posteriorProbabilityVector = (Double[])posteriorVector.getData();
		
		Double[][] evidence1=(Double[][])state.getContextParameter("evidence1"); //The first evidence is the one that defines the columns
		Double[][] evidence2=(Double[][])state.getContextParameter("evidence2"); //The second evidence is the one that defines the rows
		Double[][][] probMatrix=(Double[][][])state.getContextParameter("probability-matrix");

		int i = 0, evidenceIndexA = -1, evidenceIndexB = -1;

		for (IDataElement measurement : measurements) {
			
			if (measurement==null || !(measurement.getValue() instanceof Double)) {
				System.out.println("ERROR in provided data! Unable to run algorithm for non Double or null values. Value will be ignored");
				return null;
			} else {
				Double value=(Double)measurement.getValue();
				
				//first determine evidenceIndeces for the inputs
				if(i == 0)
					evidenceIndexA=determineEvidenceIndex(evidence1,value);
				else
					evidenceIndexB=determineEvidenceIndex(evidence2,value);
				//System.out.print(", evidenceIndex="+evidenceIndex +"  ");
				
				i++;
			}
		}		
		
		//Check validity of Evidence Indexes
		if (evidenceIndexA>=0 && evidenceIndexB>=0) {
			//System.out.println("[Baysian 3D] Input1 ---> {Value: "+measurements[0].getValue()+"| Index: "+evidenceIndexA+"}");
			//System.out.println("[Baysian 3D] Input2 ---> {Value: "+measurements[1].getValue()+"| Index: "+evidenceIndexB+"}");
			calculatePosteriorProbabilityDistribution(probMatrix, evidenceIndexA, evidenceIndexB, posteriorProbabilityVector);
		}
		
		hypoToMonitor = (String)state.getContextParameter("hypoToMonitor");
		
		if(hypoToMonitor.equalsIgnoreCase("all")) {
			//System.out.println("[Bayesian 3D] -----> "+posteriorVector.toString());
			return posteriorVector; //Return the current pdf in a MultiDataVectorFormat
		}
		else { 
			//System.out.println("[Bayesian 3D] -----> {"+hypoToMonitor+":"+posteriorVector.getDataElement(hypoToMonitor).toString()+"}");
			return posteriorVector.getDataElement(hypoToMonitor); //Return the probability of the given hypothesis to be monitored
		}
	}

	public static void main(String[] argv){
		Bayesian3D bayesian = new Bayesian3D();
		//set some trial values
		InputParameter evidence1=new InputParameter("evidence1","0;1;3");
		InputParameter evidence2=new InputParameter("evidence2","0;1;3");
		InputParameter hypoToMonitor=new InputParameter("hypoToMonitor","all");
		InputParameter hypothesis=new InputParameter("hypothesis","fire;perhaps-fire;no-fire");
		InputParameter probMatrix=new InputParameter("probability-matrix", "0.1,0.2,0.6;0.3,0.3,0.3;0.6,0.5,0.1|0.2,0.3,0.3;0.4,0.4,0.5;0.3,0.2,0.5|0.7,0.5,0.1;0.3,0.3,0.2;0.1,0.3,0.4");
		
		HashMap<String, InputParameter> iparams=new HashMap<String, InputParameter>();
		iparams.put(evidence1.getName(), evidence1);
		iparams.put(evidence2.getName(), evidence2);
		iparams.put(hypothesis.getName(),hypothesis);
		iparams.put(probMatrix.getName(), probMatrix);
		iparams.put(hypoToMonitor.getName(), hypoToMonitor);
		
		IAlgorithmContext context=new AlgorithmContext("1", "Bayesian");
		IDataElement[] data=new DataElement[2];
		try {
			bayesian.initialize(context, iparams);
		} catch (AlgorithmInitializationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//a loop for 100 sensor readings. Each reading corresponds to a piece of evidence
		Random generator = new Random(19580427);
		for(int i = 0; i < 100; i++){
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("\n---------Iteration "+ (i+1) +" START");
			
			int measurementsToSet=2;
			System.out.print(measurementsToSet + " new observations : ");
			for (int j=0;j<measurementsToSet;j++) {
				double measurement = generator.nextDouble();
				data[j]=new DataElement(""+j);
				data[j].setValue(measurement);				
			}
			System.out.println(new ArrayList<IDataElement>(Arrays.asList(data)));
			
			//System.out.print(" observation: {"+data[0].getValue()+"} ");
			try {
				bayesian.update(context, data);
			} catch (AlgorithmExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println("!!!"+context.getData());
			System.out.println("---------Iteration "+ (i+1) +" END");
			
		}//fusion for all sensors
	}//a trial
	
}
