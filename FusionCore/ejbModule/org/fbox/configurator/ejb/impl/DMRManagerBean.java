package org.fbox.configurator.ejb.impl;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.ejb.LocalBean;
import javax.ejb.Remove;
import javax.ejb.Stateful;

import org.fbox.configurator.exceptions.DMRManagerException;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;

/**
 * Session Bean implementation class DMRManagerBean
 */
@Stateful
@LocalBean
public class DMRManagerBean {

    private ModelControllerClient client;
    private Integer port=9999;
    private String host="localhost";
    
    /**
     * Default constructor. 
     */
    public DMRManagerBean() {
    }

    public void disconnect() throws DMRManagerException {
    	if (client!=null) {
    		try {
				client.close();
			} catch (IOException e) {
				throw new DMRManagerException(e.getMessage());
			}
    	}
    }    
    
    @Remove
    public void remove() {
    	try {
			disconnect();
		} catch (DMRManagerException e) {
			e.printStackTrace();
		}
    }
    
	public void connect() throws DMRManagerException {
      	try {      		
			client = ModelControllerClient.Factory.create(host, port);
		} catch (UnknownHostException e) {					
			throw new DMRManagerException(e.getMessage());
		}        	
    }
	
	public ModelNode runOperation(String jsonFormattedOperation) throws IOException {
		ModelNode op=ModelNode.fromJSONString(jsonFormattedOperation);
		ModelNode result=client.execute(op);
		return result;
	}
	
	
}
