package org.fbox.fusion.application.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

import org.fbox.common.data.IDataElement;
import org.fbox.common.exception.AlgorithmExecutionException;
import org.fbox.common.exception.FormatterException;
import org.fbox.common.exception.OutputAdapterException;
import org.fbox.common.registry.RegistryInsertionError;
import org.fbox.fusion.application.algorithms.invoker.IContextorExecute;
import org.fbox.fusion.application.exception.ContextNotFoundException;

/**
 * Session Bean implementation class SensorDataConsumerBean
 */
@LocalBean
@Singleton
public class DataStreamConsumerBean {
	
	@Resource(name = "java:/ConnectionFactory")
    ConnectionFactory connectionFactory;
     	
	@Resource(name = "java:/topic/DataStreamTopic")
    private Topic dataStreamPublisher;
		
	@EJB
	IContextorExecute executor;
		
	private List<TopicSubscriber> listOfSubscribers;
	
	private Connection connection;
	private Session session;	
	
	int maxStreamsPerFilter = 25;

    /**
     * Default constructor. 
     */
    public DataStreamConsumerBean() {
    	listOfSubscribers = null;
    }
    
    public void subscribeForData(Set<String> dataStreamIds) throws JMSException {
    	
		try {
			if (listOfSubscribers==null) {
				connection = connectionFactory.createConnection();	
				listOfSubscribers = new ArrayList<TopicSubscriber>();
			} 
			
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			int counter = 0;
			String selectStr = "";
			for(String id : dataStreamIds) {
				
				System.out.println("DataStreamUniqueID ["+counter+"]: "+id);
				
				if (!selectStr.equals(""))
					selectStr+=" OR ";
				
				selectStr+="DataStreamID='"+id+"'";
				
				counter++;

				if(counter == maxStreamsPerFilter) {
					
					TopicSubscriber subscriber = (TopicSubscriber)session.createConsumer(dataStreamPublisher, selectStr, true);
					subscriber.setMessageListener(new SensorDataListener());
					
					listOfSubscribers.add(subscriber);
					
					System.out.println("Message Selector set to \"" + selectStr + "\"");
					
					counter = 0;
					selectStr = "";
				}
			}
			
			if(!selectStr.equals("")) { // Some of them left...
				TopicSubscriber subscriber = (TopicSubscriber)session.createConsumer(dataStreamPublisher, selectStr, true);
				subscriber.setMessageListener(new SensorDataListener());
				
				listOfSubscribers.add(subscriber);
				
				System.out.println("Message Selector set to \"" + selectStr + "\"");
			}
			
	    	connection.start();	

		} catch (JMSException e) {
			e.printStackTrace();
			throw new JMSException(e.getMessage());
		} 
		
//		System.out.println("Message Selector set to \"" + MessageSelector + "\"");
    }
    
    @PreDestroy
    public void unsubscribe() {
    	System.out.println("Unsubscribing from topic ...");
		try {
			if (listOfSubscribers!=null) {
				for(TopicSubscriber sb : listOfSubscribers) {
					sb.close();
					sb = null;
				}
				listOfSubscribers=null;
			}				
			if (session!=null) {
				session.close();
				session=null;
			}
	    	if (connection!=null) {
	    		connection.close();
	    		connection=null;
	    	}
		} catch (JMSException e) {
			e.printStackTrace();
		}       			
    }
    
    protected class SensorDataListener implements MessageListener {

		@Override
		public void onMessage(Message message) {

			//System.out.println("Consuming new Message...");
			if (message instanceof ObjectMessage) {
				try {

					IDataElement data=((IDataElement)((ObjectMessage)message).getObject());					
					String dataStreamId=message.getStringProperty("DataStreamID");
					
					//invoke contextor for DataStream
					executor.update(dataStreamId,data);
									
				} catch (JMSException | AlgorithmExecutionException | ContextNotFoundException | OutputAdapterException | FormatterException | RegistryInsertionError e) {
					e.printStackTrace();
					System.out.println("ERROR--->DataStreamConsumerBean:onMessage: "+e.getMessage());
				}
			}
		}
    	
    }
    
}
