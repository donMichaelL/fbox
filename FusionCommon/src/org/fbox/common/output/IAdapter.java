package org.fbox.common.output;

import java.util.HashMap;

import org.fbox.common.data.IContext;
import org.fbox.common.data.InputParameter;
import org.fbox.common.exception.AdapterInitializationException;
import org.fbox.common.exception.OutputAdapterException;

public interface IAdapter {
	
	public void initialize(IContext state, HashMap<String, InputParameter> iparams) throws AdapterInitializationException;
	public void dispatch(IContext state, Object data) throws OutputAdapterException;

}
