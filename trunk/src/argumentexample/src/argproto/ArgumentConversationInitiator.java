package argproto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import agents.ArgumentationAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.states.MsgReceiver;
import jade.util.leap.Iterator;

public class ArgumentConversationInitiator<T> extends FSMBehaviour {

	private static final long serialVersionUID = 1L;
	public static final int ALL_TOPICS_CLOSED = 1;
	public static final int OFF_TOPIC = 0;
	public static final int ON_TOPIC = 1;
	
	protected final String INIT_CONV_KEY = "__init_conv" + hashCode();
	protected final String ARGUMENT_K = "__argument" + hashCode();
	protected final String REPLY_K = "__reply" + hashCode();
	
	// FSM states names
	protected static final String PREPARE_INIT_CONV = "Prepare-initialize-conversation";
	protected static final String SEND_INIT = "Send-init";
	protected static final String RECEIVE_ACK = "Receive-conversation-ack";
	protected static final String HANDLE_ACK = "Handle-conversation-ack";
	
	protected static final String PREPARE_NEW_ARG = "Prepare-new-argument";
	protected static final String SEND_ARGUMENT = "Send-argument";
	protected static final String RECEIVE_REPLY = "Receive-reply";
	protected static final String HANDLE_NOT_UNDERSTOOD = "Handle-not-understood";
	protected static final String HANDLE_FAILURE = "Handle-failure";
	protected static final String CHECK_ARG_SEQ = "check-argument-sequence";
	protected static final String CHECK_CONVERSATION = "check-conversation";
	
	protected static final String HANDLE_OFF_TOPIC = "Handle-off-topic";
	protected static final String CHECK_ARG_TOPIC = "Check-argument-topic";
	protected static final String END_CONV = "End-conversation";
	protected static final String INIT_CONV_EXPIRED = "Init-conv-expired";
	
	protected ArgumentationAgent argAgent;
	protected final ACLMessage initialArgument;
	protected String conversationId;
	
	protected ArrayList<T> argumentsListed;
	protected HashMap<T, Boolean> argumentsFinished;
	
	protected String[] toBeReset = null;
	protected boolean endConversationSent = false;
	protected boolean endConversationReceived = false;
	
	// The MessageTemplate used by the replyReceiver
	protected MessageTemplate replyTemplate = null;
	
	// The MsgReceiver behavior used to receive replies 
	protected MsgReceiver replyReceiver = null;
	protected MsgReceiver ackReceiver = null;
	
	/**
	 * Constructs an <code>ArgumentConversation</code> behavior
	 **/
	public ArgumentConversationInitiator(ArgumentationAgent argAgent, ACLMessage startingArgument) {
		this(argAgent, startingArgument, new DataStore());
	}
	
	/**
	 * Constructs an <code>ArgumentConversation</code> behavior which handles arguments of class type T
	 * @param a The agent performing the protocol
	 * @param argument The message that must be used to initiate the protocol.
	 * @param s The <code>DataStore</code> that will be used by this 
	 * <code>ArgumentConversation</code>
	 */
	public ArgumentConversationInitiator(ArgumentationAgent argAgent, final ACLMessage initialArgument, DataStore ds) {
		super(argAgent);
		this.argAgent = argAgent;
		this.initialArgument = initialArgument;
		initialArgument.setPerformative(ArgumentationMessage.ARG_ASSERT);
		
		setDataStore(ds);
		ds.put(ARGUMENT_K, initialArgument);
		
		argumentsListed = new ArrayList<T>();
		argumentsFinished = new HashMap<T, Boolean>();
		
		try {
			ArgumentStructure<T> argTopic = (ArgumentStructure<T>)initialArgument.getContentObject();
			appendArgument(argTopic);
			//argumentsListed.add(argTopic);
			//argumentsFinished.put(argTopic, false);
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
		
		// set FSM transitions
		registerDefaultTransition(PREPARE_INIT_CONV, SEND_INIT);
		registerDefaultTransition(SEND_INIT, RECEIVE_ACK);
		registerTransition(RECEIVE_ACK, INIT_CONV_EXPIRED, MsgReceiver.TIMEOUT_EXPIRED);
		registerDefaultTransition(RECEIVE_ACK, HANDLE_ACK);
		registerTransition(HANDLE_ACK, END_CONV, 0);	// exit protocol if conversation refused by partner
		registerDefaultTransition(HANDLE_ACK, SEND_ARGUMENT);
		
		registerTransition(SEND_ARGUMENT, END_CONV, 0); // Exit the protocol if no initiation message is sent
		registerDefaultTransition(SEND_ARGUMENT, RECEIVE_REPLY);
		
		registerTransition(RECEIVE_REPLY, HANDLE_NOT_UNDERSTOOD, ACLMessage.NOT_UNDERSTOOD);		
		registerTransition(RECEIVE_REPLY, HANDLE_FAILURE, ACLMessage.FAILURE);
		registerDefaultTransition(RECEIVE_REPLY, CHECK_ARG_TOPIC);
		
		registerDefaultTransition(HANDLE_NOT_UNDERSTOOD, PREPARE_NEW_ARG);
		registerDefaultTransition(HANDLE_FAILURE, PREPARE_NEW_ARG);
		
		registerTransition(CHECK_ARG_TOPIC, HANDLE_OFF_TOPIC, OFF_TOPIC);
		registerDefaultTransition(HANDLE_OFF_TOPIC, RECEIVE_REPLY);
		registerDefaultTransition(CHECK_ARG_TOPIC, PREPARE_NEW_ARG);
		
		registerTransition(PREPARE_NEW_ARG, END_CONV, ArgumentationMessage.ARG_ACCEPT_END_CONV);
		registerDefaultTransition(PREPARE_NEW_ARG, SEND_ARGUMENT, getToBeReset());
		
		// Create and register the states that make up the FSM
		Behaviour b = null;
		
		// PREPARE_INIT_ARG
		b = new OneShotBehaviour(myAgent) {
			private static final long serialVersionUID = 2L;
			public void action() {
				ACLMessage initArgConv = new ACLMessage(ArgumentationMessage.ARG_START_CONV);
				conversationId = createConvId(initialArgument);
				initArgConv.setConversationId(conversationId);
				
				getDataStore().put(INIT_CONV_KEY, initArgConv);
			}
		};
		b.setDataStore(getDataStore());
		registerFirstState(b, PREPARE_INIT_CONV);
		
		
		// SEND_INIT
		b = new OneShotBehaviour(myAgent) {
			private static final long serialVersionUID = 3L;
			
			public void action() {
				DataStore ds = getDataStore();
				ACLMessage arg = (ACLMessage) ds.get(INIT_CONV_KEY);
				long currentTime = System.currentTimeMillis();
				long deadline = currentTime + 5000;
				
				replyTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId(conversationId), 
									MessageTemplate.MatchPerformative(ArgumentationMessage.ARG_ACK_CONV));
				
				if (arg != null && arg.getAllReceiver().hasNext()) {
					for (Iterator receivers = arg.getAllReceiver(); receivers.hasNext(); ) {
						ACLMessage toSend = (ACLMessage)arg.clone();
						toSend.setConversationId(conversationId);
						toSend.clearAllReceiver();
						AID r = (AID)receivers.next();
						toSend.addReceiver(r);
					
						myAgent.send(toSend);
					}
					
					// Finally set the MessageTemplate and timeout used in the RECEIVE_REPLY 
					// state to accept replies
					replyReceiver.setTemplate(replyTemplate);
					replyReceiver.setDeadline(deadline);
				}
			}
			
		};
		b.setDataStore(getDataStore());		
		registerState(b, SEND_INIT);
		
		
		// RECEIVE_ACK
		ackReceiver = new MsgReceiver(myAgent, null, MsgReceiver.INFINITE, getDataStore(), REPLY_K);
		registerState(ackReceiver, RECEIVE_ACK);
		
		
		// HANDLE_ACK
		b = new OneShotBehaviour(myAgent) {
			private static final long     serialVersionUID = 8L;
			private int ret = 0;
			
			public void action() {
				ACLMessage reply_ack = (ACLMessage)getDataStore().get(REPLY_K);
				if (reply_ack != null) {
					int ack = reply_ack.getPerformative();
					if (ack == ArgumentationMessage.ARG_ACK_CONV) {
						ret = 1;
					}
				}
			}
			
			public int onEnd() {
				return ret;
			}
		};
		b.setDataStore(getDataStore());		
		registerState(b, HANDLE_ACK);
		
		
		// SEND_ARGUMENT
		b = new OneShotBehaviour(myAgent) {
			private static final long serialVersionUID = 4L;
			
			public void action() {
				DataStore ds = getDataStore();
				ACLMessage arg = (ACLMessage) ds.get(ARGUMENT_K);
				long currentTime = System.currentTimeMillis();
				long minTimeout = -1;
				long deadline = -1;
				
				replyTemplate = MessageTemplate.MatchConversationId(conversationId);
				
				if (arg != null && arg.getAllReceiver().hasNext()) {
					for (Iterator receivers = arg.getAllReceiver(); receivers.hasNext(); ) {
						ACLMessage toSend = (ACLMessage)arg.clone();
						toSend.setConversationId(conversationId);
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
		registerState(b, SEND_ARGUMENT);
		
		
		// RECEIVE_REPLY
		replyReceiver = new MsgReceiver(myAgent, null, MsgReceiver.INFINITE, getDataStore(), REPLY_K);
		registerState(replyReceiver, RECEIVE_REPLY);
		
		// HANDLE_NOT_UNDERSTOOD
		b = new OneShotBehaviour(myAgent) {
			private static final long     serialVersionUID = 5L;
			
			public void action() {
				handleNotUnderstood((ACLMessage) getDataStore().get(REPLY_K));
			}
		};
		b.setDataStore(getDataStore());		
		registerState(b, HANDLE_NOT_UNDERSTOOD);
		
		
		// HANDLE_FAILURE
		b = new OneShotBehaviour(myAgent) {
			private static final long     serialVersionUID = 6L;
			
			public void action() {
				handleFailure((ACLMessage) getDataStore().get(REPLY_K));
			}
		};
		b.setDataStore(getDataStore());		
		registerState(b, HANDLE_FAILURE);
		
		
		// CHECK_ARG_TOPIC
		b = new OneShotBehaviour(argAgent) {
			private static final long     serialVersionUID = 7L;
			private boolean ret = true;
			
			public void action() {
				ACLMessage sentArg = (ACLMessage)getDataStore().get(ARGUMENT_K);
				ACLMessage recvArg = (ACLMessage)getDataStore().get(REPLY_K);
				ret = argumentOnTopic(sentArg, recvArg); 
			}
			
			public int onEnd() {
				if (ret) {
					return ON_TOPIC;
				}
				else {
					return OFF_TOPIC;
				}
			}
		};
		
		
		// HANDLE_FAILURE
		b = new OneShotBehaviour(myAgent) {
			private static final long     serialVersionUID = 9L;
			
			public void action() {
				handleOffTopic((ACLMessage) getDataStore().get(REPLY_K));
			}
		};
		b.setDataStore(getDataStore());		
		registerState(b, HANDLE_OFF_TOPIC);
		
		
		// PREPARE_NEW_ARG
		b = new OneShotBehaviour(myAgent) {
			private static final long     serialVersionUID = 10L;
			private int ret = -1;
			private ACLMessage new_arg = null;
			
			public void action() {
				ACLMessage sentArg = (ACLMessage)getDataStore().get(ARGUMENT_K);
				ACLMessage recvArg = (ACLMessage)getDataStore().get(REPLY_K);
				
				int recvPerformative = recvArg.getPerformative();
				
				switch(recvPerformative) {
					case ArgumentationMessage.ARG_ASSERT:
						try {
							ArgumentStructure<T> argStruct = (ArgumentStructure<T>)recvArg.getContentObject();
							appendArgument(argStruct);
						} catch (UnreadableException e) {
							e.printStackTrace();
						}
						new_arg = handleAssert(sentArg, recvArg);
						break;
						
					case ArgumentationMessage.ARG_CHALLENGE:
						new_arg = handleChallenge(sentArg, recvArg);
						break;
						
					case ArgumentationMessage.ARG_ACCEPT:
						new_arg = handleAccept(sentArg, recvArg);
						break;
						
					case ArgumentationMessage.ARG_ACCEPT_END_CONV:
						if (endConversationSent) {
							ret = recvPerformative;
						}
						break;
				}
				
				if (new_arg != null) {
					ret = new_arg.getPerformative();
					if (ret == ArgumentationMessage.ARG_END_CONV) {
						endConversationSent = true;
					}
				}
			}
			
			public int onEnd() {
				return ret;
			}
		};
		b.setDataStore(getDataStore());		
		registerState(b, HANDLE_OFF_TOPIC);
	}
	
	/**
	 * handles the case in which the received message is an <code>accept</code> message
	 * @param sentArg the previously sent argument
	 * @param recvArg the freshly received argument
	 * @return returns the new ACLMessage that is to be sent to the counterpart or null if no message is to be sent 
	 */
	protected ACLMessage handleAccept(ACLMessage sentArg, ACLMessage recvArg) {
		return null;
	}

	/**
	 * handles the case in which the received message is a <code>challenge</code> message
	 * @param sentArg the previously sent argument
	 * @param recvArg the freshly received argument
	 * @return returns the new ACLMessage that is to be sent to the counterpart or null if no message is to be sent 
	 */
	protected ACLMessage handleChallenge(ACLMessage sentArg, ACLMessage recvArg) {
		return null;
	}

	/**
	 * handles the case in which the received message is an <code>assert</code> message
	 * @param sentArg the previously sent argument
	 * @param recvArg the freshly received argument
	 * @return returns the new ACLMessage that is to be sent to the counterpart or null if no message is to be sent 
	 */
	protected ACLMessage handleAssert(ACLMessage sentArg, ACLMessage recvArg) {
		return null;
	}

	/**
	 * This method is called every time a <code>not-understood</code>
	 * message is received.
	 * This default implementation does nothing; programmers might
	 * wish to override the method in case they need to react to this event.
	 * @param notUnderstood the received not-understood message
	 **/
	protected void handleNotUnderstood(ACLMessage notUnderstood) {
	}
	
	/**
	 * This method is called every time a <code>failure</code>
	 * message is received.
	 * This default implementation does nothing; programmers might
	 * wish to override the method in case they need to react to this event.
	 * @param failure the received failure message
	 **/
	protected void handleFailure(ACLMessage failure) {
	}
	
	/**
	 * This method is called every time an <code>off topic</code>
	 * message is received.
	 * This default implementation does nothing; programmers might
	 * wish to override the method in case they need to react to this event.
	 * @param offTopic the received off-topic message
	 **/
	protected void handleOffTopic(ACLMessage offTopic) {
	}
	
	/**
	 * determines if the argument <code>recvArg</code> is on the topic of discussion or not
	 * @param sentArg the argument sent previously
	 * @param recvArg the argument received
	 * @return returns true if the received argument is on the topic of discussion and false otherwise
	 */
	protected boolean argumentOnTopic(ACLMessage sentArg, ACLMessage recvArg) {
		if (recvArg == null) {
			return false;
		}
		
		try {
			T arg = (T)recvArg.getContentObject();
			if (arg == null) {
				return false;
			}
			
			if (argumentsListed.contains(arg)) {
				return true;
			}
		} catch (UnreadableException e) {
			e.printStackTrace();
			return false;
		}
		
		return false;
	}
	
	protected String createConvId(ACLMessage arg) {
		String convId = null;
		if ((arg == null) || (arg.getConversationId() == null)) { 
			convId = "ArgC" + hashCode() + "_" + System.currentTimeMillis();
		}
		else {
			convId = arg.getConversationId();
		}
		
		return convId;
	}
	
	protected String[] getToBeReset() {
		if (toBeReset == null) {
			toBeReset = new String[] {
					HANDLE_NOT_UNDERSTOOD,
					HANDLE_FAILURE
			};
		}
		return toBeReset;
	}
	
	public ArrayList<T> getArgumentsListed() {
		return argumentsListed;
	}

	public HashMap<T, Boolean> getArgumentsFinished() {
		return argumentsFinished;
	}

	public void appendArgument(ArgumentStructure<T> argStruct) {
		T conclusion = argStruct.getConclusion();
		if (!argumentsListed.contains(conclusion)) {
			argumentsListed.add(conclusion);
			argumentsFinished.put(conclusion, true);
		}
		
		ArrayList<ArgumentStructure<T>> premises = argStruct.getPremises();
		if (premises != null) {
			for (ArgumentStructure<T> premise : premises) {
				if (premise != null) {
					appendArgument(premise);
				}
			}
		}
	}
}
