package argproto;

import java.util.Date;
import java.util.Vector;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.states.MsgReceiver;
import jade.util.leap.Iterator;

public class ArgumentInitiator extends FSMBehaviour {
	//#APIDOC_EXCLUDE_BEGIN
	//protected final String INITIATION_K = "__initiation" + hashCode();
	//protected final String ALL_INITIATIONS_K = "__all-initiations" +hashCode();
	protected final String ARGUMENT_K = "__argument" + hashCode();
	protected final String REPLY_K = "__reply" + hashCode();
	
	private static final int ASSERT = 0;
	private static final int CHALLENGE = 1;
	private static final int ACCEPT = 2;
	
	// FSM states names
	//private static final String PREPARE_ARGUMENT = "Prepare-argument";
	private static final String SEND_ARGUMENT = "Send-argument";
	private static final String RECEIVE_REPLY = "Receive-reply";
	private static final String CHECK_IN_SEQ = "Check-in-seq";
	
	private static final String HANDLE_ACCEPT = "Handle-accept";
	private static final String HANDLE_ASSERT = "Handle-assert";
	private static final String HANDLE_CHALLENGE = "Handle-challenge";
	private static final String DUMMY_FINAL = "Dummy-final";
	
	// The MsgReceiver behaviour used to receive replies 
	protected MsgReceiver replyReceiver = null;
	
	// The MessageTemplate used by the replyReceiver
	protected MessageTemplate replyTemplate = null;
	
	protected ACLMessage argument;
	
	/**
	 * Constructs an <code>ArgumentInitiator</code> behaviour
	 **/
	public ArgumentInitiator(Agent a, ACLMessage argument) {
		this(a, argument, new DataStore());
	}
	
	/**
	 * Constructs an <code>ArgumentInitiator</code> behaviour
	 * @param a The agent performing the protocol
	 * @param argument The message that must be used to initiate the protocol.
	 * @param s The <code>DataStore</code> that will be used by this 
	 * <code>ArgumentInitiator</code>
	 */
	public ArgumentInitiator(Agent a, ACLMessage argument, DataStore ds) {
		super(a);
		setDataStore(ds);
		this.argument = argument;
		ds.put(ARGUMENT_K, argument);
		
		registerTransition(SEND_ARGUMENT, DUMMY_FINAL, 0);	// exit protocol if no argument is sent
		registerDefaultTransition(SEND_ARGUMENT, RECEIVE_REPLY);
		
		registerTransition(RECEIVE_REPLY, DUMMY_FINAL, MsgReceiver.TIMEOUT_EXPIRED); 
		registerTransition(RECEIVE_REPLY, DUMMY_FINAL, MsgReceiver.INTERRUPTED);
		registerTransition(RECEIVE_REPLY, DUMMY_FINAL, ACLMessage.NOT_UNDERSTOOD);
		registerDefaultTransition(RECEIVE_REPLY, CHECK_IN_SEQ);
		
		registerTransition(CHECK_IN_SEQ, HANDLE_ASSERT, ASSERT);
		registerTransition(CHECK_IN_SEQ, HANDLE_ACCEPT, ACCEPT);
		registerTransition(CHECK_IN_SEQ, HANDLE_CHALLENGE, CHALLENGE);
		
		// Create and register the states that make up the FSM
		Behaviour b = null;
		// SEND_ARGUMENT
		b = new OneShotBehaviour(myAgent) {
			private static final long     serialVersionUID = 3487495895818001L;
			
			public void action() {
				DataStore ds = getDataStore();
				ACLMessage arg = (ACLMessage) ds.get(ARGUMENT_K);
				long currentTime = System.currentTimeMillis();
				long minTimeout = -1;
				long deadline = -1;
				
				String conversationID = createConvId(arg);
				replyTemplate = MessageTemplate.MatchConversationId(conversationID);
				
				if (arg != null && arg.getAllReceiver().hasNext()) {
					for (Iterator receivers = arg.getAllReceiver(); receivers.hasNext(); ) {
						ACLMessage toSend = (ACLMessage)arg.clone();
						toSend.setConversationId(conversationID);
						toSend.clearAllReceiver();
						AID r = (AID)receivers.next();
						toSend.addReceiver(r);
					
						myAgent.send(toSend);
					}
					
					// Update the timeout (if any) used to wait for replies according
					// to the reply-by field: get the miminum.  
					Date d = arg.getReplyByDate();
					if (d != null) {
						long timeout = d.getTime() - currentTime;
						if (timeout > 0 && (timeout < minTimeout || minTimeout <= 0)) {
							minTimeout = timeout;
							deadline = d.getTime();
						}
					}
					
					// Finally set the MessageTemplate and timeout used in the RECEIVE_REPLY 
					// state to accept replies
					replyReceiver.setTemplate(replyTemplate);
					replyReceiver.setDeadline(deadline);
				}
			}
			
			public int onEnd() {
				DataStore ds = getDataStore();
				ACLMessage arg = (ACLMessage) ds.get(ARGUMENT_K);
				
				if (arg == null || !arg.getAllReceiver().hasNext()) {
					return 0;
				}
				return 1;
			}
		};
		b.setDataStore(getDataStore());		
		registerFirstState(b, SEND_ARGUMENT);
		
		// RECEIVE_REPLY
		replyReceiver = new MsgReceiver(myAgent, null, MsgReceiver.INFINITE, getDataStore(), REPLY_K);
		registerState(replyReceiver, RECEIVE_REPLY);
		
		// CHECK_IN_SEQ
		b = new OneShotBehaviour(myAgent) {
			int ret;
			private static final long     serialVersionUID = 3487495895818002L;
			
			public void action() {
				ACLMessage reply = (ACLMessage) getDataStore().get(REPLY_K);
				ret = checkInSequence(reply);
			}
			public int onEnd() {
				return ret;
			}
		};
		b.setDataStore(getDataStore());		
		registerState(b, CHECK_IN_SEQ);
		
		
		// HANDLE ASSERT
		b = new OneShotBehaviour(myAgent) {
			private static final long     serialVersionUID = 3487495895818003L;
			
			public void action() {
				handleAssert((ACLMessage) getDataStore().get(REPLY_K));
			}
		};
		b.setDataStore(getDataStore());		
		registerLastState(b, HANDLE_ASSERT);
		
		// HANDLE ACCEPT
		b = new OneShotBehaviour(myAgent) {
			private static final long     serialVersionUID = 3487495895818004L;
			
			public void action() {
				handleAccept((ACLMessage) getDataStore().get(REPLY_K));
			}
		};
		b.setDataStore(getDataStore());		
		registerLastState(b, HANDLE_ACCEPT);
		
		// HANDLE CHALLENGE
		b = new OneShotBehaviour(myAgent) {
			private static final long     serialVersionUID = 3487495895818005L;
			
			public void action() {
				handleChallenge((ACLMessage) getDataStore().get(REPLY_K));
			}
		};
		b.setDataStore(getDataStore());		
		registerLastState(b, HANDLE_CHALLENGE);
		
		// DUMMY_FINAL
		b = new OneShotBehaviour(myAgent) {
			private static final long     serialVersionUID = 3487495895818006L;
			
			public void action() {
			}
		};
		registerLastState(b, DUMMY_FINAL);
	}
	
	protected String createConvId(ACLMessage arg) {
		// If the conversation-id of the first message is set --> 
		// use it. Otherwise create a default one
		String convId = null;
		if ((arg == null) || (arg.getConversationId() == null)) {
			convId = "C" + hashCode() + "_" + System.currentTimeMillis();
		} 
		else {
			convId = arg.getConversationId();
		}
		
		return convId;
	}
	
	protected int checkInSequence(ACLMessage reply) {
		int type = getReplyType(reply);
		return type;
	}
	
	protected int getReplyType(ACLMessage argReply) {
		
		String type = argReply.getUserDefinedParameter("ARG_PERFORMATIVE");
		if (type != null) {
			if (type.equalsIgnoreCase("assert")) {
				return ASSERT;
			}
			
			if (type.equalsIgnoreCase("accept")) {
				return ACCEPT;
			}
			
			if (type.equalsIgnoreCase("challenge")) {
				return CHALLENGE;
			}
		}
		
		return -1;
	}
	
	/**
	 * This method is called every time an <code>assert</code>
	 * message is received, which is not out-of-sequence according
	 * to the protocol rules.
	 * This default implementation does nothing; programmers might
	 * wish to override the method in case they need to react to this event.
	 * @param assertMsg the received assert message
	 **/
	protected void handleAssert(ACLMessage assertMsg) {
	}
	
	/**
	 * This method is called every time an <code>accept</code>
	 * message is received, which is not out-of-sequence according
	 * to the protocol rules.
	 * This default implementation does nothing; programmers might
	 * wish to override the method in case they need to react to this event.
	 * @param acceptMsg the received accept message
	 **/
	protected void handleAccept(ACLMessage acceptMsg) {
	}
	
	/**
	 * This method is called every time an <code>challenge</code>
	 * message is received, which is not out-of-sequence according
	 * to the protocol rules.
	 * This default implementation does nothing; programmers might
	 * wish to override the method in case they need to react to this event.
	 * @param challengeMsg the received challenge message
	 **/
	protected void handleChallenge(ACLMessage challengeMsg) {
	}
}
