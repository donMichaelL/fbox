package org.fbox.common.data;

import org.fbox.common.exception.FormatterException;
import org.fbox.common.exception.OutputAdapterException;
import org.fbox.common.output.IAdapter;
import org.fbox.common.output.IFormatter;

public interface IOutputContext {

	public String getId();
	public void setFormatter(IFormatter<?> formater);
	public IFormatter<?> getFormater();
	public void setAdapter(IAdapter adapter);
	public IAdapter getAdapter();
	public void setData(IDataElement... data) throws OutputAdapterException, FormatterException;
	public IContext getformatContext();
	public IContext getAdapterContext();
	
	/** NEW **/
	public String getAppID();
}
