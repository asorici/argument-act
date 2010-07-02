package argproto;

import java.util.Vector;

import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

public class ArgumentInfoRequester extends AchieveREInitiator {

	/**
	 * Constructs an <code>ArgumentInfoRequester</code> behaviour
	 * Use this behavior to ask other agents for their knowledge about an argument p
	 * @param a Agent performing the behavior
	 * @param msg The message containing the argument you inquire about
	 */
	public ArgumentInfoRequester(Agent a, ACLMessage msg) {
		this(a, msg, new DataStore());
	}
	
	public ArgumentInfoRequester(Agent a, ACLMessage msg, DataStore store) {
		super(a, msg, store);
		msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		msg.setPerformative(ACLMessage.REQUEST);
		msg.addUserDefinedParameter("ARG_PERFORMATIVE", "QUESTION");
		msg.setConversationId("ARG_QUESTION");
	}
	
	protected final void handleInform(ACLMessage inform) {
		// handle inform message
		handleArgumentResponse(inform);
	}
	
	protected final void handleRefuse(ACLMessage refuse) {
		System.out.println("Agent "+refuse.getSender().getName()+" refused to answer to the request");
	}
	
	protected void handleFailure(ACLMessage failure) {
		if (failure.getSender().equals(myAgent.getAMS())) {
			// FAILURE notification from the JADE runtime: the receiver
			// does not exist
			System.out.println("Responder does not exist");
		}
		else {
			System.out.println("Agent "+failure.getSender().getName()+" failed to perform the requested action");
		}
	}
	
	protected final void handleAllResultNotifications(Vector notifications) {
		// handle all responses to current argument request
		handleAllArgumentResponses(notifications);
	}
	
	/**
	 * handles an incoming response about the inquired argument
	 * the default implementation does nothing
	 * programmers will have to override this method with the proper implementation
	 * @param argInfo the info message received about the inquired argument
	 */
	public void handleArgumentResponse(ACLMessage argInfo) {
		
	}
	
	/**
	 * handles the list of all received responses to the inquired argument
	 * the default implementation does nothing
	 * programmers will have to override this method with the proper implementation
	 * @param notifications the Vector of argument responses to the given argument inquiry
	 */
	public void handleAllArgumentResponses(Vector notifications) {
		
	}
}
