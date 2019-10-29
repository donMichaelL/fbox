package org.fbox.fusion.output.formatter.impl;

import java.io.IOException;
import java.util.HashMap;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.fbox.common.data.IContext;
import org.fbox.common.data.IDataElement;
import org.fbox.common.data.InputParameter;
import org.fbox.common.exception.FormatterException;
import org.fbox.common.exception.FormatterInitializationException;
import org.fbox.common.output.IFormatter;
import org.fbox.fusion.output.formatter.AbstractFormatter;
import org.fbox.fusion.output.formatter.data.FusionOutput;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Stateless (name="expoForm")
@Remote ({IFormatter.class})
public class ExpoFormatter extends AbstractFormatter<String> {
	
	@Override
	public String format(IContext state, IDataElement... data) throws FormatterException {
		
		
		String percentage=null;
		
		//Check the value that comes inside the formatter
		if(data != null) {

			if (data[0].getValue() instanceof Number) {
				// if(Double.parseDouble(data[0].getValue().toString()) != 0.0)
					percentage = data[0].getValue().toString(); //"Someone has to do something in here!!\n";
					
					 
					ObjectMapper mapper = new ObjectMapper(); 
					try {
						percentage = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(new FusionOutput(percentage));
					} catch (JsonGenerationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonMappingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			} else
				throw new FormatterException("Invalid data value detected("+data[0].getValue()+"). Value must be Double");
		}
		
		return percentage;
	}
	
	@Override
	public void initialize(IContext state, HashMap<String, InputParameter> iparams) throws FormatterInitializationException {
		super.initialize(state, iparams);
	}
	
	@Override
	public String[] getRequiredParameters() {
		return null;
	}
	
	@Override
	public String getType() {
		return "expoForm";
	}

	@Override
	public boolean allowsMultipleInputs() {
		return false;
	}
}
