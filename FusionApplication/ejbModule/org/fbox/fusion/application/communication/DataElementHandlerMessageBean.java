package org.fbox.fusion.application.communication;


import java.util.ArrayList;

import javax.ejb.EJB;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.fbox.common.exception.AlgorithmExecutionException;
import org.fbox.common.exception.FormatterException;
import org.fbox.common.exception.OutputAdapterException;
import org.fbox.common.registry.RegistryInsertionError;
import org.fbox.fusion.application.algorithms.invoker.IContextorExecute;
import org.fbox.fusion.application.exception.ContextNotFoundException;
import org.fbox.fusion.application.registry.ContextorRegistry;
import org.fbox.fusion.application.registry.InputOutputMapRegistry;

/**
 * Message-Driven Bean implementation class for: MeasurementHandler
 *
 */
/*@MessageDriven(
		activationConfig = { 
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"), 
		@ActivationConfigProperty(propertyName="destination", propertyValue="queue/fbox/MeasurementsQueue")	
		})
*/
public class DataElementHandlerMessageBean implements MessageListener {

	@EJB
	InputOutputMapRegistry ioRegistry;
	@EJB
	ContextorRegistry cRegistry;
	@EJB
	IContextorExecute target;

	
	/**
     * Default constructor. 
     */
    public DataElementHandlerMessageBean() {
    	System.out.println("MDB created to handle message...");
    }
	
	/**g
     * @see MessageListener#onMessage(Message)
     */
    @Override
    public void onMessage(Message message) {

    	//System.out.println(" -----> message arrived. Start Parsing");	
		if (message instanceof ObjectMessage) {		
			try {
				String sourceGuid=message.getStringProperty("GUID");
				//System.out.println("Data found. Source is GUID:"+ sourceGuid);
				
				//check registry to find mapping of source to target
				ArrayList<String> targetGuids=ioRegistry.getDestinations(sourceGuid);
					
				
				if (targetGuids!=null) {
					//IDataElement valueTopass=(IDataElement)((ObjectMessage) message).getObject();					
					//System.out.println("Found "+ targetGuids.size() + " destinations for source "+sourceGuid);
					//pass data to each target Contextor
					for (String targetGuid : targetGuids) {						
						//System.out.println("Destination found for data(" + sourceGuid + "). TargetGuid is "+ targetGuid);
//						System.out.println("Start Retrieving input data for TargetGuid"+ targetGuid);
						try {
							target.update(targetGuid);
						} catch (AlgorithmExecutionException | ContextNotFoundException | OutputAdapterException | FormatterException | RegistryInsertionError e) {
							e.printStackTrace();
						}							
					}
				} else {
					System.out.println("No Destination found for data.");
				}
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
    }

}
