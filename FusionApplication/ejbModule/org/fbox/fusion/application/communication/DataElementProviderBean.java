package org.fbox.fusion.application.communication;


import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import org.fbox.common.data.IDataElement;


/**
 * Session Bean implementation class MessageProviderBean
 */
@Stateless
@LocalBean
public class DataElementProviderBean {
	
	@Resource(name = "java:/ConnectionFactory")
    ConnectionFactory connectionFactory;
     	
	@Resource (name= "DataQueue")
    private Queue dataQueueDestination;
	
	/*@Resource(name="DataQueue")
	private String destinationName;*/
	
    public DataElementProviderBean() {
    }

    @Asynchronous
    public void addDataInQueue(String srcID, IDataElement value) {
    // System.out.println("Handling message for sensor"+sensorID);
    try {      	
    	
    	//get reference to JMS destination
       	Connection connection = connectionFactory.createConnection();       	
    	Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);  
    	              
    	ObjectMessage myMessage = session.createObjectMessage();  
    	myMessage.setStringProperty("GUID", srcID);  
    	myMessage.setObject(value);
    	
    	//System.out.println("[MessageProvider]" + myMessage.toString());
    	//destination=session.createQueue (destinationName);
    	//System.out.println(dataQueueDestination);
    	MessageProducer producer = session.createProducer (dataQueueDestination);    	
    	producer.send(myMessage);  
    	// System.out.println("Message sent!");  
    	  
    	connection.close();
        } catch (JMSException e) {  
        	e.printStackTrace();  
        }
    }
}
