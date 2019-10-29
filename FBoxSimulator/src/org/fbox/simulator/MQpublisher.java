package org.fbox.simulator;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * @author grad1100
 */
public class MQpublisher implements ExceptionListener {
    
    private boolean topic;
    private boolean transacted;
    private String url;
    private String subject;
    
    private String username;
    private String password;
    
    private Session session;
    private MessageProducer publisher;
    private Destination destination;
    private Connection connection;
    
    public MQpublisher (String user, String pass, String url, String queueName) throws Exception {
        
        this.topic = false;
        this.transacted = false;
        this.url = url;
        this.subject = queueName;
        
        this.username = user;
        this.password = pass;
        
        // Getting JMS connection from the server and starting it
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(this.username, this.password, this.url);
        this.connection = connectionFactory.createConnection();
        
        // Create a session
        this.session = this.connection.createSession(this.transacted, Session.AUTO_ACKNOWLEDGE);
        
        // Create the destination (Topic or Queue)
        if(this.topic) {
            this.destination = this.session.createTopic(this.subject);
        }
        else {
            this.destination = this.session.createQueue(this.subject);
        }
        
        // Create a MessageProducer for the scenario
        this.publisher = this.session.createProducer(this.destination);
        
        // Set asynchronous exception listener on the connection
        this.connection.setExceptionListener(this);
        
        this.connection.start();
    }
    
    public void publishString (String msg) throws Exception {
        
        TextMessage message = this.session.createTextMessage(msg);
        this.publisher.send(message);
    }
    
    public void closeMQpublisher () throws Exception {
        
        this.connection.close();
    }
    
    public void onException (JMSException exception) {
        System.err.println("Something bad happened: " + exception);
    }
}
