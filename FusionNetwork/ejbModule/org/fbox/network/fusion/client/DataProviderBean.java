package org.fbox.network.fusion.client;

import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.fbox.common.data.IDataElement;
import org.fbox.common.network.IDataProvider;

/**
 * Session Bean implementation class MessageProviderBean
 */
@Stateless
@Remote (IDataProvider.class)
public class DataProviderBean implements IDataProvider{

	   private static HashMap<String, Integer> messagesPerDataStream;
	
	   @Resource(name = "java:/ConnectionFactory")
       ConnectionFactory connectionFactory;
/*      
	   @Resource(name = "java:/queue/fbox/MeasurementsQueue")
       private Queue destination2;
*/
	   @Resource(name = "java:/topic/DataStreamTopic")
       private Topic destination;	   
	   
    /**
     * Default constructor. 
     */
    public DataProviderBean() {
    }
    
    @PostConstruct
    private void init() {
    	if(messagesPerDataStream==null)
    		messagesPerDataStream=new HashMap<String, Integer>();
    }

    public HashMap<String, Integer> getMessagesPerDataStream() {
    	return messagesPerDataStream;
    }
    
    
    @Asynchronous
    public void addDataInQueue(String dataStreamId, IDataElement data) {
    // System.out.println("Handling message for sensor"+sensorID);
    	Connection connection=null;
    try {  
    	 
    	
    	
    	
    	//get reference to JMS destination  
       	connection = connectionFactory.createConnection();  
    	Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);  
    	              
    	ObjectMessage myMessage = session.createObjectMessage();  
    	myMessage.setStringProperty("DataStreamID", dataStreamId);  
    	myMessage.setObject(data);
    	
    	synchronized (messagesPerDataStream) {
   		Integer numberOfMeasurements=messagesPerDataStream.get(dataStreamId);
	   		if (numberOfMeasurements!=null) {
	   			messagesPerDataStream.put(dataStreamId, ++numberOfMeasurements);
	   		} else {
	   			messagesPerDataStream.put(dataStreamId, new Integer(1));
	    	}
    	}
    	
    	//System.out.println("[MessageProvider]" + myMessage.toString());
    	
  //  	MessageProducer producer2 = session.createProducer(destination2);    	
  //  	producer.send(myMessage);  
    	
    	MessageProducer producer = session.createProducer(destination);  
    	producer.send(myMessage); 
    	
    	 //System.out.println("Message sent!");  
    	  
        } catch (JMSException e) {  
        	e.printStackTrace();  
        } finally {
        	if (connection!=null)
				try {
					connection.close();
				} catch (JMSException e) {
					e.printStackTrace();
				}
        }
    }
}
