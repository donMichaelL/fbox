package org.fbox.common.application.data;

import java.util.Date;

import org.fbox.common.algorithms.IAlgorithm;
import org.fbox.common.data.AlgorithmContext;
import org.fbox.common.data.IAlgorithmContext;
import org.fbox.common.data.IContextorContext;
import org.fbox.common.data.IDataElement;
import org.fbox.common.exception.AlgorithmExecutionException;

public class ContextRegistryEntity implements IContextorContext {

	private String id;  //the id of the Contextor
	private IAlgorithmContext baseAlgorithmContext;
	private IAlgorithmContext missingAlgorithmContext;
	protected IAlgorithm algorithm;
	protected IAlgorithm missingValueAlgorithm;

	public ContextRegistryEntity(String id) {
		this.id=id;
		baseAlgorithmContext=new AlgorithmContext(id,null);
	}

	public void setId(String id) {
		this.id=id;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public IAlgorithm getBaseAlgorithm() {
		return algorithm;
	}

	@Override
	public void setBaseAlgorithm(IAlgorithm algorithm) {
		this.algorithm = algorithm;
		baseAlgorithmContext=new AlgorithmContext(id, algorithm.getType());
	}
	
	@Override
	public IAlgorithm getMissingAlgorithm() {
		return missingValueAlgorithm;
	}

	@Override
	public void setMissingAlgorithm(IAlgorithm algorithm) {
		this.missingValueAlgorithm = algorithm;
		this.missingAlgorithmContext=new AlgorithmContext(id,algorithm.getType());
	}

	@Override
	public synchronized IDataElement updateContext(IDataElement... inputData) throws AlgorithmExecutionException {
		//System.out.println("Contextor("+this.id+")---> Updating contextor ...");
		//System.out.println("Contextor("+this.id+")---> Old Values=(B:["+(baseAlgorithmContext!=null?baseAlgorithmContext.getData():"-")+"], M:["+(missingAlgorithmContext!=null?missingAlgorithmContext.getData():"-")+"])");
		
		//base algorithms operations
		if (algorithm!=null) { //update data object
			algorithm.update(baseAlgorithmContext, inputData);
		} else {
			if (inputData.length==1) {
				baseAlgorithmContext.getData().setValue(inputData[0].getValue());
			} else
				throw new AlgorithmExecutionException("Runtime exception: Cannot use identity on MISO algorithm!!!");
		}
		
		//missing algorithm operations
		if (missingValueAlgorithm!=null) { //update missingValue data object
			//System.out.println("@@@@@@@@@@@@@@@@@@@@@@@Ready to Invoke missing#######################");
			missingValueAlgorithm.update(missingAlgorithmContext, baseAlgorithmContext.getData());
		}		
		
		//System.out.println("Contextor("+this.id+")---> New Value=(B:["+baseAlgorithmContext.getData()+"], M:["+(missingAlgorithmContext!=null?missingAlgorithmContext.getData():"NULL")+"])");
		//System.out.println("... Contextor("+this.id+") Update complete");	
		
		return baseAlgorithmContext.getData();
	}


	@Override
	public IDataElement getValidData(long timeLimit) {
		IDataElement validData;
		// TODO possibly add an OR data.getTimestamp() !=null &
		
		IDataElement data=baseAlgorithmContext.getData();
		
		//System.out.println("ID="+this.id+", algorithm="+ missingValueAlgorithm +", timeLimit="+timeLimit+", difference"+(new Date().getTime() - data.getTimestamp().getTime()));
		if (timeLimit==0 | missingValueAlgorithm==null | (new Date().getTime() - data.getTimestamp().getTime())<=timeLimit)
			validData=data;
		else {
			//System.out.println("*****************Missing Value criteria met. Retrieving missing Value data...");
			validData=missingAlgorithmContext.getData();
		}
			
		return validData;
	}

	@Override
	public String toString() {
		String result="ID: "+id;
		result+=", Data(B: "+(baseAlgorithmContext+", M: "+missingAlgorithmContext) +")";
		return result;
	}
	
	@Override
	public IAlgorithmContext getBaseAlgorithmContext() {
		return this.baseAlgorithmContext;
	}
	
	@Override
	public IAlgorithmContext getMissingAlgorithmContext() {
		return this.missingAlgorithmContext;
	}

	
}
