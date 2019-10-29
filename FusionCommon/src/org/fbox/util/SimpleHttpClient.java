package org.fbox.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class SimpleHttpClient {
	
	private static final  String defaultParameterName="request";
	
	public HttpClient httpclient = new DefaultHttpClient();

	public SimpleHttpClient() {
	}
	
	public void postData(String data, String serviceURL, String parameterName) throws IOException {
		
		HttpPost httppost = new HttpPost(serviceURL);
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair(parameterName!=null?parameterName:defaultParameterName, data));
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        
		HttpResponse response = httpclient.execute(httppost);
		
		HttpEntity entity = response.getEntity();

		if (entity==null)
			throw new IOException("Empty response.");
	}
	
	public void postFromFile(String pathToFile, String serviceURL) throws IOException {
		String xmlToPost = readFileAsString(pathToFile);
		
		postData(xmlToPost, serviceURL, defaultParameterName);
	}

	public String getHttpResponse(String serviceURL) throws IOException {
		
		HttpPost httppost = new HttpPost(serviceURL);
		
		HttpResponse response = httpclient.execute(httppost);
		
		HttpEntity entity = response.getEntity();
		if (entity==null)
			throw new IOException("Empty response.");
		
		BufferedInputStream bReader=new BufferedInputStream(entity.getContent());
		int c;
		String result="";
		while ((c=bReader.read())!=-1) {
			result+=(char)c;
		}

		return result;
	}
	
	
	private String readFileAsString(String filePath) throws java.io.IOException{
	    byte[] buffer = new byte[(int) new File(filePath).length()];
	    BufferedInputStream f = null;
	    try {
	        f = new BufferedInputStream(new FileInputStream(filePath));
	        f.read(buffer);
	    } finally {
	        if (f != null) try { f.close(); } catch (IOException ignored) { }
	    }
	    return new String(buffer);
	}
}