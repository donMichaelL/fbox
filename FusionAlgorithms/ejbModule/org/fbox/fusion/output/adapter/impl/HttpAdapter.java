package org.fbox.fusion.output.adapter.impl;
import java.io.IOException;

import javax.ejb.Asynchronous;
import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.fbox.common.data.IContext;
import org.fbox.common.exception.OutputAdapterException;
import org.fbox.common.output.IAdapter;
import org.fbox.fusion.output.adapter.AbstractAdapter;
import org.fbox.util.SimpleHttpClient;

@Stateless (name="http")
@Remote ({IAdapter.class})
public class HttpAdapter extends AbstractAdapter {

	public static final String HTTP_URL = "url";	
	public static final String HTTP_PARAM = "parameter-name";	
	
	@Override
	public String[] getRequiredParameters() {
		String[] params={HTTP_URL};
		return params;
	}

	@Override
	public String getType() {
		return "http";
	}

	@Override
	public void dispatch(IContext state, Object data) throws OutputAdapterException {
		
		String url = (String)state.getContextParameter(HTTP_URL);
		Object parameterName = state.getContextParameter(HTTP_PARAM);
		
		SimpleHttpClient client = new SimpleHttpClient();
		
		try {			
			client.postData(data!=null?data.toString():"", url, parameterName!=null?(String)parameterName:null);
		} catch (IOException e) {
			throw new OutputAdapterException("HttpExporter::Send: Error in posting xml to destination.", e);
		}	
		
	}


}

