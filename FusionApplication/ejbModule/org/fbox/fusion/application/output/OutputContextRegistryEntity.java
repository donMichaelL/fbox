package org.fbox.fusion.application.output;

import org.fbox.common.data.Context;
import org.fbox.common.data.IContext;
import org.fbox.common.data.IDataElement;
import org.fbox.common.data.IOutputContext;
import org.fbox.common.exception.FormatterException;
import org.fbox.common.exception.OutputAdapterException;
import org.fbox.common.output.IAdapter;
import org.fbox.common.output.IFormatter;

public class OutputContextRegistryEntity implements IOutputContext {

	String id;
	IFormatter<?> formatter;
	IAdapter adapter;
	IContext formatContext;
	IContext adapterContext;
	Object lastKnownDispatchedData;
	
	/** NEW **/
	//Hold the appID that this contextor belongs
	String appID;
	
	public OutputContextRegistryEntity(String id, String appID) {
		this.id=id;
		this.appID=appID;
	}
	
	@Override
	public IFormatter<?> getFormater() {
		return formatter;
	}

	@Override
	public void setFormatter(IFormatter<?> formatter) {
		this.formatter=formatter;		
		this.formatContext=new Context();
	}
	
	@Override
	public void setAdapter(IAdapter adapter) {
		this.adapter=adapter;
		this.adapterContext=new Context();
	}

	@Override
	public IAdapter getAdapter() {
		return this.adapter;
	}
	
	/** NEW **/
	@Override
	public String getAppID() {
		return this.appID;
	}

	@Override
	public void setData(IDataElement... data) throws OutputAdapterException, FormatterException {
		
		/** SWeFS specific - START **/
		// If the is "null" output from a contextor that communicates with a formatter then do not execute the formatter
		if(data == null || data[0].getValue() == null) {
			System.out.println("[SWeFS specific] Null Value Detected -> No need to execute '"+this.formatter.getType()+"'");
			return;
		}
		/** SWeFS specific - END **/
		Object formattedData=null;
		
		if (this.formatter!=null) {
			formattedData=formatter.format(this.getformatContext(), data);
		}
		
		if (this.adapterContext!=null && formattedData!=null) {
			adapter.dispatch(this.getAdapterContext(), formattedData);
			lastKnownDispatchedData=formattedData;
			//System.out.println("####################Dispatching data:"+ formattedData);
			
			//Here we can update the lastReportTime timestamp for this specific applicationID
			
		}
	}

	@Override
	public IContext getformatContext() {
		return formatContext;
	}

	@Override
	public IContext getAdapterContext() {
		return adapterContext;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String toString() {
		String result="id:"+this.id;
		result+=", lastKnownDispatchedData:"+this.lastKnownDispatchedData;
		result+=", formatter="+this.formatter;
		result+=", adapter="+this.adapter;
		
		return result;
	}
}
