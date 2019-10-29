package org.fbox.fusion.output.adapter.impl;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.fbox.common.data.IContext;
import org.fbox.common.exception.OutputAdapterException;
import org.fbox.common.output.IAdapter;
import org.fbox.fusion.output.adapter.AbstractAdapter;

@Stateless(name="mqAdapter")
@Remote ({IAdapter.class})
public class MQAdapter extends AbstractAdapter {
	
	@Override
	public String[] getRequiredParameters() {
		String[] params={"mqurl","mqid","topic","transacted","mqusername","mqpassword"};
		return params; //No parameters for this adapter for now
	}
	
	@Override
	public String getType() {
		return "mqAdapter";
	}
	
	@Override
	public void dispatch(IContext state, Object data) throws OutputAdapterException {
		
		//To define if we have a queue or a topic to use for posting information
		boolean topic = Boolean.parseBoolean((String)state.getContextParameter("topic"));
		boolean transacted = Boolean.parseBoolean((String)state.getContextParameter("transacted"));
		String MQurl = (String)state.getContextParameter("mqurl");
		String MQsubject = (String)state.getContextParameter("mqid");
		String MQusername = (String)state.getContextParameter("mqusername");
		String MQpassword = (String)state.getContextParameter("mqpassword");
		
		//Handle "null" output from a formatter
		if(data.toString() == null) {//if(data == null) {
			throw new OutputAdapterException("ActiveMqExporter::Send: Warning: No need to give an output");
		}
		
		// Getting JMS connection from the server and starting it
		try {
	        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(MQusername, MQpassword, MQurl);
	        Connection connection = connectionFactory.createConnection();
	        
	        // Create a session
	        Session session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
		
	        // Create the destination (Topic or Queue)
	        Destination destination;
	        if(topic) {
	            destination = session.createTopic(MQsubject);
	        }
	        else {
	            destination = session.createQueue(MQsubject);
	        }
	        
	        // Create a MessageProducer for the scenario
	        MessageProducer publisher = session.createProducer(destination);
	        
	        // Set asynchronous exception listener on the connection
	        //  connection.setExceptionListener(this);
	        
	        connection.start();
	        
	        String output = data.toString();
	        
	        TextMessage message = session.createTextMessage(output);
	        publisher.send(message);
	        
	        // Close connection for this output
	        connection.close();
		} catch (JMSException jmsException) {
				jmsException.printStackTrace();
				throw new OutputAdapterException("ActiveMQExporter::Send: Error: " + jmsException.getMessage());
		}
	}
}
