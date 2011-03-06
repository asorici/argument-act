package argproto;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.states.MsgReceiver;
import jade.util.leap.Iterator;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import onto.impl.DefaultConclusion;
import agents.ArgumentationAgent;
import argutils.ArgumentConversation;
import argutils.ArgumentationMessage;

public abstract class ArgumentConversationInitiator extends FSMBehaviour {

	private static final long serialVersionUID = 1L;
	public static final int ALL_TOPICS_CLOSED = 1;
	public static final int OFF_TOPIC = 0;
	public static final int ON_TOPIC = 1;
	
	protected final String INIT_CONV_KEY = "__init_conv" + hashCode();
	protected final String ARGUMENT_K = "__argument" + hashCode();
	protected final String REPLY_K = "__reply" + hashCode();
	
	// FSM state names
	protected static final String PREPARE_INIT_CONV = "Prepare-initialize-conversation";
	protected static final String SEND_INIT = "Send-init";
	protected static final String RECEIVE_ACK = "Receive-conversation-ack";
	protected static final String HANDLE_ACK = "Handle-conversation-ack";
	
	protected static final String PREPARE_NEW_ARG = "Prepare-new-argument";
	protected static final String SEND_ARGUMENT = "Send-argument";
	protected static final String RECEIVE_REPLY = "Receive-reply";
	protected static final String HANDLE_TIMEOUT_EXPIRED = "Handle-timeout-expired";
	protected static final String HANDLE_NOT_UNDERSTOOD = "Handle-not-understood";
	protected static final String HANDLE_FAILURE = "Handle-failure";
	protected static final String CHECK_CONVERSATION = "check-conversation";
	
	protected static final String HANDLE_OFF_TOPIC = "Handle-off-topic";
	protected static final String CHECK_ARG_TOPIC = "Check-argument-topic";
	protected static final String CHECK_ARG_SEQUENCE = "Check-argument-sequence";
	protected static final String HANDLE_OUT_OF_SEQUENCE = "Handle-out-of-sequence";
	protected static final String END_CONV = "End-conversation";
	protected static final String INIT_CONV_EXPIRED = "Init-conv-expired";
	
	protected ArgumentationAgent argAgent;
	protected final ACLMessage initialArgument;
	protected String conversationId;
	
	//protected ArrayList<T> argumentsListed;
	//protected HashMap<T, Boolean> argumentsFinished;
	
	protected String[] toBeReset = null;
	protected boolean endConversationSent = false;
	protected boolean endConversationReceived = false;
	
	protected boolean replyTimeout = false;
	protected boolean replyNotUnderStood = false;
	protected boolean replyFailure = false;
	protected boolean replyOffTopic = false;
	protected boolean replyOutOfSequence = false;
	
	// The MessageTemplate used by the replyReceiver
	protected MessageTemplate replyTemplate = null;
	
	// The MsgReceiver behavior used to receive replies 
	protected MsgReceiver replyReceiver = null;
	protected MsgReceiver ackReceiver = null;
	
	private int callsAttempted = 0;
	

	/**
	 * Constructs an <code>ArgumentConversation</code> behavior
	 **/
	public ArgumentConversationInitiator(ArgumentationAgent argAgent, ACLMessage startingArgument) {
		this(argAgent, startingArgument, new DataStore());
	}
	
	/**
	 * Constructs an <code>ArgumentConversationInitiator</code> behavior which handles arguments of class type T
	 * @param a The agent performing the protocol
	 * @param argument The message that must be used to initiate the protocol.
	 * @param s The <code>DataStore</code> that will be used by this 
	 * <code>ArgumentConversationInitiator</code>
	 */
	public ArgumentConversationInitiator(ArgumentationAgent argAgent, final ACLMessage initialArgument, DataStore ds) {
		super(argAgent);
		this.argAgent = argAgent;
		this.initialArgument = initialArgument;
		initialArgument.setPerformative(ArgumentationMessage.ARG_ASSERT);
		
		setDataStore(ds);
		ds.put(ARGUMENT_K, initialArgument);
		//conversationId = createConvId(initialArgument);
		
		// set FSM transitions
		// first establish a new conversation
		registerDefaultTransition(PREPARE_INIT_CONV, SEND_INIT);
		registerDefaultTransition(SEND_INIT, RECEIVE_ACK);
		registerTransition(RECEIVE_ACK, INIT_CONV_EXPIRED, MsgReceiver.TIMEOUT_EXPIRED);
		registerDefaultTransition(RECEIVE_ACK, HANDLE_ACK);
		
		registerTransition(INIT_CONV_EXPIRED, END_CONV, 0);
		registerDefaultTransition(INIT_CONV_EXPIRED, SEND_INIT);
		
		registerTransition(HANDLE_ACK, END_CONV, 0);	// exit protocol if conversation refused by partner
		registerDefaultTransition(HANDLE_ACK, SEND_ARGUMENT);
		
		// exchange arguments
		registerTransition(SEND_ARGUMENT, END_CONV, 0); // Exit the protocol if no initiation message is sent
		registerDefaultTransition(SEND_ARGUMENT, RECEIVE_REPLY);
		
		registerTransition(RECEIVE_REPLY, HANDLE_TIMEOUT_EXPIRED, MsgReceiver.TIMEOUT_EXPIRED);
		registerTransition(RECEIVE_REPLY, HANDLE_NOT_UNDERSTOOD, ACLMessage.NOT_UNDERSTOOD);		
		registerTransition(RECEIVE_REPLY, HANDLE_FAILURE, ACLMessage.FAILURE);	// exit the protocol if a failure occurs
		registerDefaultTransition(RECEIVE_REPLY, CHECK_ARG_SEQUENCE);
		
		registerTransition(CHECK_ARG_SEQUENCE, HANDLE_OUT_OF_SEQUENCE, 0); // Exit the protocol if sequence of messages is broken 
		registerDefaultTransition(CHECK_ARG_SEQUENCE, CHECK_ARG_TOPIC);
		
		registerDefaultTransition(HANDLE_TIMEOUT_EXPIRED, PREPARE_NEW_ARG);
		registerDefaultTransition(HANDLE_NOT_UNDERSTOOD, PREPARE_NEW_ARG);
		
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
		
		// RECEIVE_ACK
		ackReceiver = new MsgReceiver(myAgent, null, MsgReceiver.INFINITE, getDataStore(), REPLY_K);
		registerState(ackReceiver, RECEIVE_ACK);
		
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
					ackReceiver.setTemplate(replyTemplate);
					ackReceiver.setDeadline(deadline);
				}
			}
			
		};
		b.setDataStore(getDataStore());		
		registerState(b, SEND_INIT);
		
		
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
						
						Map<String, ArgumentConversation> conversationRecords = ArgumentConversationInitiator.this.argAgent.getConversationRecords();
						conversationRecords.get(conversationId).setStatus(ArgumentConversation.RUNNING);
					}
				}
			}
			
			public int onEnd() {
				return ret;
			}
		};
		b.setDataStore(getDataStore());		
		registerState(b, HANDLE_ACK);
		
		
		// INIT_CONV_EXPIRED
		b = new OneShotBehaviour(myAgent) {
			private static final long     serialVersionUID = 14L;
			private int ret = 0;
			
			public void action() {
				ACLMessage reply_ack = (ACLMessage)getDataStore().get(REPLY_K);
				if (reply_ack != null) {
					int ack = reply_ack.getPerformative();
					if (ack == ArgumentationMessage.ARG_ACK_CONV) {
						ret = 1;
					}
					else {
						int attempted = getCallsAttempted();
						if (attempted < 3) {
							ret = 1;
							setCallsAttempted(attempted + 1);
						}
					}
				}
			}
			
			public int onEnd() {
				return ret;
			}
		};
		b.setDataStore(getDataStore());		
		registerState(b, INIT_CONV_EXPIRED);
		
		// SEND_ARGUMENT
		b = new OneShotBehaviour(myAgent) {
			private static final long serialVersionUID = 4L;
			
			public void action() {
				DataStore ds = getDataStore();
				ACLMessage arg = (ACLMessage) ds.get(ARGUMENT_K);
				long currentTime = System.currentTimeMillis();
				long minTimeout = -1;
				long deadline = -1;
				
				// reset notUnderstood and Failure flags
				replyTimeout = false;
				replyNotUnderStood = false;
				replyFailure = false;
				replyOffTopic = false;
				replyOutOfSequence = false;
				
				replyTemplate = MessageTemplate.MatchConversationId(conversationId);
				
				if (arg != null && arg.getAllReceiver().hasNext()) {
					int performative = arg.getPerformative();
					if (performative == ArgumentationMessage.ARG_END_CONV) {
						endConversationSent = true;
					}
					
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
		
		
		// HANDLE_TIMEOUT_EXPIRED
		b = new OneShotBehaviour(myAgent) {
			private static final long     serialVersionUID = 14L;
			
			public void action() {
				replyTimeout = true;
			}
		};
		b.setDataStore(getDataStore());		
		registerState(b, HANDLE_TIMEOUT_EXPIRED);
		
		
		// HANDLE_NOT_UNDERSTOOD
		b = new OneShotBehaviour(myAgent) {
			private static final long     serialVersionUID = 5L;
			
			public void action() {
				replyFailure = true;
				handleNotUnderstood((ACLMessage) getDataStore().get(ARGUMENT_K), 
									(ACLMessage) getDataStore().get(REPLY_K));
			}
		};
		b.setDataStore(getDataStore());		
		registerState(b, HANDLE_NOT_UNDERSTOOD);
		
		
		// HANDLE_FAILURE
		b = new OneShotBehaviour(myAgent) {
			private static final long     serialVersionUID = 6L;
			
			public void action() {
				replyFailure = true;
				handleFailure((ACLMessage) getDataStore().get(ARGUMENT_K),
							 (ACLMessage) getDataStore().get(REPLY_K));
				
				//myAgent.removeBehaviour(ArgumentConversationInitiator.this);
				Map<String, ArgumentConversation> conversationRecords = ArgumentConversationInitiator.this.argAgent.getConversationRecords();
				conversationRecords.get(conversationId).setStatus(ArgumentConversation.BROKEN);
			}
		};
		b.setDataStore(getDataStore());		
		registerLastState(b, HANDLE_FAILURE);
		
		
		// CHECK_OUT_OF_SEQUENCE
		b = new OneShotBehaviour(argAgent) {
			private static final long     serialVersionUID = 11L;
			private int ret = 0;
			
			public void action() {
				ACLMessage sentArgMsg = (ACLMessage)getDataStore().get(ARGUMENT_K);
				ACLMessage recvArgMsg = (ACLMessage)getDataStore().get(REPLY_K);
				
				SimpleArgumentStructure sentArg = null;
				SimpleArgumentStructure recvArg = null;
				
				try {
					sentArg = (SimpleArgumentStructure) sentArgMsg.getContentObject();
					recvArg = (SimpleArgumentStructure) recvArgMsg.getContentObject();
				} catch (UnreadableException e) {
					e.printStackTrace();
					ret = 0;
					return;
				}
				catch (Exception ex) {
					ex.printStackTrace();
					ret = 0;
					return;
				}
				
				if (sentArg == null || recvArg == null) {
					ret = 0;
					return;
				}
				
				int sentPerformative = sentArgMsg.getPerformative();
				int recvPerformative = recvArgMsg.getPerformative();
				
				switch(sentPerformative) {
				case ArgumentationMessage.ARG_ASSERT:
				case ArgumentationMessage.ARG_CHALLENGE:
					if (recvPerformative == ArgumentationMessage.ARG_ACCEPT) {
						ret = checkAcceptMessage(sentArg, recvArg);
					}
					else if (recvPerformative == ArgumentationMessage.ARG_CHALLENGE) {
						ret = checkChallengeMessage(sentArg, recvArg);
					}
					break;
				case ArgumentationMessage.ARG_ACCEPT:
					if (recvPerformative == ArgumentationMessage.ARG_ASSERT || 
						recvPerformative == ArgumentationMessage.ARG_CHALLENGE) {
						ret = 1;
					}
					break;
				default: 
					ret = 1;
					break; 
				}
			}
			
			public int onEnd() {
				return ret;
			}
			
			private int checkAcceptMessage(SimpleArgumentStructure sentArg, SimpleArgumentStructure recvArg) {
				try {
					DefaultConclusion recvConclusion = recvArg.getConclusion();
					DefaultConclusion sentConclusion = sentArg.getConclusion();
					//TODO - verifica modul in care doua noduri de informatie I_Node sunt egale
					if (recvConclusion.equals(sentConclusion)) {
						return 1;
					}
					
					return 0;
				}
				catch(Exception ex) {
					return 0;
				}
			}
			
			private int checkChallengeMessage(SimpleArgumentStructure sentArg, SimpleArgumentStructure recvArg) {
				try {
					if (sentArg.isChallenged(recvArg)) {
						return 1;
					}
					
					return 0;
				}
				catch(Exception ex) {
					return 0;
				}
			}
			
		};
		b.setDataStore(getDataStore());		
		registerState(b, CHECK_ARG_SEQUENCE);
		
		
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
		b.setDataStore(getDataStore());		
		registerState(b, CHECK_ARG_TOPIC);
		
		
		// HANDLE_OUT_OF_SEQUENCE
		b = new OneShotBehaviour(myAgent) {
			private static final long     serialVersionUID = 13L;
			
			public void action() {
				ACLMessage recvMsg = (ACLMessage) getDataStore().get(REPLY_K);
				ACLMessage msg = recvMsg.createReply();
				msg.setPerformative(ArgumentationMessage.ARG_OUT_OF_SEQ);
				msg.setConversationId(conversationId);
				
				myAgent.send(msg);
				
				//myAgent.removeBehaviour(ArgumentConversationInitiator.this);
				Map<String, ArgumentConversation> conversationRecords = ArgumentConversationInitiator.this.argAgent.getConversationRecords();
				conversationRecords.get(conversationId).setStatus(ArgumentConversation.BROKEN);
			}
		};
		b.setDataStore(getDataStore());		
		registerLastState(b, HANDLE_OUT_OF_SEQUENCE);
		
		
		// HANDLE_OFF_TOPIC
		b = new OneShotBehaviour(myAgent) {
			private static final long     serialVersionUID = 9L;
			
			public void action() {
				replyOffTopic = true;
				handleOffTopic((ACLMessage) getDataStore().get(ARGUMENT_K), 
								(ACLMessage) getDataStore().get(REPLY_K));
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
				
				if (!isArgumentationMessage(recvArg)) {
					new_arg = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);
					new_arg.setConversationId(conversationId);
				}
				else if (replyTimeout) {
					endConversationSent = false;
					new_arg = handleReplyTimeout(sentArg);
				}
				else {
					int recvPerformative = recvArg.getPerformative();
					
					switch(recvPerformative) {
						case ArgumentationMessage.ARG_ASSERT:
							endConversationSent = false;				// it doesn't matter if i requested a
							new_arg = handleAssert(sentArg, recvArg);	// conv-end if the counterpart still
							break;										// has things to say
							
						case ArgumentationMessage.ARG_CHALLENGE:
							endConversationSent = false;
							new_arg = handleChallenge(sentArg, recvArg);
							break;
							
						case ArgumentationMessage.ARG_ACCEPT:
							endConversationSent = false;
							new_arg = handleAccept(sentArg, recvArg);
							break;
							
						case ArgumentationMessage.ARG_ACCEPT_END_CONV:
							if (endConversationSent) {
								ret = recvPerformative;
							}
							break;
							
						case ArgumentationMessage.ARG_END_CONV:
							new_arg = handleEndConversation(sentArg, recvArg);
							break;
						
						case ArgumentationMessage.ARG_OUT_OF_SEQ:
							new_arg = null;
							break;
					}
				}
				
				getDataStore().put(ARGUMENT_K, new_arg);
			}
			
			public int onEnd() {
				return ret;
			}
		};
		b.setDataStore(getDataStore());		
		registerState(b, PREPARE_NEW_ARG);
		
		
		// END_CONV - remove this behavior from the agent's stack
		b = new OneShotBehaviour(myAgent) {
			private static final long     serialVersionUID = 12L;
			
			public void action() {
				//myAgent.removeBehaviour(ArgumentConversationInitiator.this);
				Map<String, ArgumentConversation> conversationRecords = ArgumentConversationInitiator.this.argAgent.getConversationRecords();
				conversationRecords.get(conversationId).setStatus(ArgumentConversation.ENDED);
			}
		};
		b.setDataStore(getDataStore());		
		registerLastState(b, END_CONV);
	}
	
	/**
	 * handles the case in which the received message is an <code>accept</code> message
	 * @param sentArg the previously sent argument
	 * @param recvArg the freshly received argument
	 * @return returns the new ACLMessage that is to be sent to the counterpart or null if no message is to be sent 
	 */
	protected abstract ACLMessage handleAccept(ACLMessage sentArg, ACLMessage recvArg);

	/**
	 * handles the case in which the received message is a <code>challenge</code> message
	 * @param sentArg the previously sent argument
	 * @param recvArg the freshly received argument
	 * @return returns the new ACLMessage that is to be sent to the counterpart or null if no message is to be sent 
	 */
	protected abstract ACLMessage handleChallenge(ACLMessage sentArg, ACLMessage recvArg);

	/**
	 * handles the case in which the received message is an <code>assert</code> message
	 * @param sentArg the previously sent argument
	 * @param recvArg the freshly received argument
	 * @return returns the new ACLMessage that is to be sent to the counterpart or null if no message is to be sent 
	 */
	protected abstract ACLMessage handleAssert(ACLMessage sentArg, ACLMessage recvArg);

	/**
	 * handles the case in which the reply times-out
	 * @param sentArg the previously sent argument
	 * @return returns the new ACLMessage that is to be sent to the counterpart or null if no message is to be sent 
	 */
	protected abstract ACLMessage handleReplyTimeout(ACLMessage sentArg);
	
	/**
	 * handles the case in which the received message is a conversation end request
	 * the method defaults to sending an <code>ArgumentationMessage.ARG_ACCEPT_END_CONV</code> reply
	 * @param sentArg the previously sent argument
	 * @param recvArg the freshly received argument
	 * @return returns the new ACLMessage that is to be sent to the counterpart or null if no message is to be sent 
	 */
	protected ACLMessage handleEndConversation(ACLMessage sentArg, ACLMessage recvArg) {
		ACLMessage endConvAccept = new ACLMessage(ArgumentationMessage.ARG_ACCEPT_END_CONV);
		endConvAccept.setConversationId(conversationId);
		return endConvAccept;
	}
	
	/**
	 * This method is called every time a <code>not-understood</code>
	 * message is received.
	 * This default implementation does nothing; programmers might
	 * wish to override the method in case they need to react to this event.
	 * @param notUnderstood the received not-understood message
	 **/
	protected void handleNotUnderstood(ACLMessage sentArg, ACLMessage notUnderstood) {
	}
	
	/**
	 * This method is called every time a <code>failure</code>
	 * message is received. The protocol will exit as an effect. This method should implement any action
	 * that must be taken before exiting the protocol.
	 * This default implementation does nothing.
	 * programmers might wish to override the method in case they need to react differently to this event.
	 * @param sentArg the argument sent
	 * @param failure the received failure message
	 **/
	protected void handleFailure(ACLMessage sentArg, ACLMessage failure) {
	}
	
	/**
	 * This method is called every time an <code>off topic</code>
	 * message is received.
	 * This default implementation does nothing; programmers might
	 * wish to override the method in case they need to react to this event.
	 * @param offTopic the received off-topic message
	 **/
	protected void handleOffTopic(ACLMessage sentArg, ACLMessage offTopic) {
	}
	
	/**
	 * determines if the argument <code>recvArg</code> is on the topic of discussion or not
	 * the default implementation returns true
	 * programmers may wish to override this method to react to this event
	 * @param sentArg the argument sent previously
	 * @param recvArg the argument received
	 * @return returns true if the received argument is on the topic of discussion and false otherwise
	 */
	protected boolean argumentOnTopic(ACLMessage sentArg, ACLMessage recvArg) {
		return true;
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
					//HANDLE_NOT_UNDERSTOOD
			};
		}
		return toBeReset;
	}
	
	protected boolean isArgumentationMessage(ACLMessage recv) {
		try {
			Serializable recvData = recv.getContentObject();
			if ( (recvData instanceof ArgumentStructure<?>) ) {
				return true;
			}
			
			return false;
		} catch (UnreadableException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public int getCallsAttempted() {
		return callsAttempted;
	}

	public void setCallsAttempted(int callsAttempted) {
		this.callsAttempted = callsAttempted;
	}
	
	/*
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
	*/
}
