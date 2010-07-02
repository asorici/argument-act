package argproto;

import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

public class ArgumentInfoResponder extends AchieveREResponder {

	
	public static MessageTemplate template = MessageTemplate.and(
			MessageTemplate.and(
					MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST)),
	  		MessageTemplate.MatchConversationId("ARG_QUESTION"));
	
	/**
	 * Constructs an <code>ArgumentInfoResponder</code> behavior
	 * Use this behavior to respond to the inquiries of other agents about an argument p
	 * @param a Agent performing the behavior
	 */
	public ArgumentInfoResponder(Agent a) {
		this(a, template, new DataStore());
	}
	
	/**
	 * Constructs an <code>ArgumentInfoResponder</code> behavior
	 * Use this behavior to respond to the inquiries of other agents about an argument p
	 * @param a Agent performing the behavior
	 * @param mt the message template used to filter corresponding ArgumentInfoRequester messages
	 * @param store the <code>DataStore</code> used to retain the data for this behavior
	 */
	public ArgumentInfoResponder(Agent a, MessageTemplate mt, DataStore store) {
		super(a, mt, store);
	}
	
	protected final ACLMessage prepareResponse(ACLMessage request) throws NotUnderstoodException, RefuseException {
		ACLMessage agree = request.createReply();
		agree.setPerformative(ACLMessage.AGREE);
		return agree;
	}
	
	protected final ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
		ACLMessage argResponse = prepareArgumentResponse(request);
		if (argResponse != null) {
			argResponse.setPerformative(ACLMessage.INFORM);
			argResponse.addUserDefinedParameter("ARG_PERFORMATIVE", "INFORM");
			argResponse.setConversationId("ARG_QUESTION");
			return argResponse;
		}
		else {
			System.out.println("Agent "+myAgent.getLocalName()+": Action failed.");
			throw new FailureException("unexpected-error");
		}	
	}
	
	/**
	 * prepare the answer (if possible) to the inquired argument given in the request parameter
	 * @param request the request message containing the argument that is inquired
	 * @return the ACLMessage containing the answer (if any) to the inquired argument
	 */
	public ACLMessage prepareArgumentResponse(ACLMessage request) {
		return null;
	}
}
