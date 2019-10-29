package org.fbox.fusion.output.adapter.impl;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.fbox.common.data.IContext;
import org.fbox.common.exception.OutputAdapterException;
import org.fbox.common.output.IAdapter;
import org.fbox.fusion.output.adapter.AbstractAdapter;

@Stateless (name="file")
@Local ({IAdapter.class})
@LocalBean
public class FileAdapter extends AbstractAdapter {

	// User defined parameters
	public static final String FILE_PARAM_FILENAME = "filename";
	

	@Override
	public String[] getRequiredParameters() {
		String[] params={FILE_PARAM_FILENAME};
		return params;
	}

	@Override
	public String getType() {
		return "file";
	}

	@Override
	public void dispatch(IContext state, Object data) throws OutputAdapterException {
		
		String url = (String)state.getContextParameter(FILE_PARAM_FILENAME);
		
		try {
			this.writeTo(url, (String)data);
		} 
		catch (Throwable e) {
			throw new OutputAdapterException("FileExporter::Send: Error writing file", e);
		}
		
	}
	
	private void writeTo(String fileName, String fileContents) throws FileNotFoundException, IOException {
		File exportFile = new File(fileName);
		
		if (!exportFile.exists()) {
			exportFile.createNewFile();
		}
		if (!exportFile.isFile()) {
			throw new IllegalArgumentException("Should not be a directory: " + exportFile);
		}
		if (!exportFile.canWrite()) {
			throw new IllegalArgumentException("File cannot be written: " + exportFile);
		}
		
		Writer output = new BufferedWriter(new FileWriter(exportFile));
		
		try {
			output.write(fileContents);
		} finally {
			output.close();
		}
	}
}
