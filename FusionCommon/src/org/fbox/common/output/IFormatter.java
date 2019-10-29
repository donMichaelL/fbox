package org.fbox.common.output;

import java.util.HashMap;

import org.fbox.common.IStructure;
import org.fbox.common.data.IContext;
import org.fbox.common.data.IDataElement;
import org.fbox.common.data.InputParameter;
import org.fbox.common.exception.FormatterException;
import org.fbox.common.exception.FormatterInitializationException;

public interface IFormatter<T extends Object> extends IStructure {

	public T format(IContext state, IDataElement... data) throws FormatterException;	
	public void initialize(IContext state, HashMap<String, InputParameter> iparams) throws FormatterInitializationException;
}
