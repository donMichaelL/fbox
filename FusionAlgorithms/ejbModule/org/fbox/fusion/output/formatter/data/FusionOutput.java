package org.fbox.fusion.output.formatter.data;

public class FusionOutput {
	
	private String value;
	
	
	public FusionOutput() {
		
	}
	
	
	
	public FusionOutput(String value) {
		this.value = value;
	}



	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "FusionOutput [value=" + value + "]";
	}
	
	
	
	

}
