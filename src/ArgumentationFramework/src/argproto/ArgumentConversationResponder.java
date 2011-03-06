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


public abstract class ArgumentConversationResponder extends FSMBehaviour {
	private static final long serialVersionUID = 1L;
	
	protected ArgumentationAgent argAgent;
	protected String conversationId;
	
	protected final String INIT_CONV_K = "__init_conv" + hashCode();
	protected final String RECV_ARG_K = "__recv_argument" + hashCode();
	protected final String SEND_ARG_K = "__send_argument" + hashCode();
	
	// FSM state names
	protected static final String RECV_INIT_CONV = "receive-initialize-conversation-request";
	protected static final String HANDLE_INIT_CONV = "handle-initialize-conversation-request";
	protected static final String SEND_ACK = "Send-conversation-ack";
	
	protected static final String PREPARE_NEW_ARG = "Prepare-new-argument";
	protected static final String SEND_ARGUMENT = "Send-argument";
	protected static final String RECEIVE_ARG = "Receive-reply";
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
	
	
	// The MessageTemplate used by the replyReceiver
	protected MessageTemplate replyTemplate = null;
	
	protected boolean endConversationSent = false;
	protected boolean endConversationReceived = false;
	
	protected boolean replyTimeout = false;
	protected boolean replyNotUnderStood = false;
	protected boolean replyFailure = false;
	protected boolean replyOffTopic = false;
	protected boolean replyOutOfSequence = false;
	
	// The MsgReceiver behavior used to receive replies 
	protected MsgReceiver argReceiver = null;
	protected MsgReceiver initReceiver = null;
	
	/**
	 * Constructs an <code>ArgumentConversationResponder</code> behavior
	 **/
	public ArgumentConversationResponder(ArgumentationAgent argAgent) {
		this(argAgent, new DataStore());
	}
	
	/**
	 * Constructs an <code>ArgumentConversationResponder</code> behavior which handles arguments of class type T
	 * @param a The agent performing the protocol
	 * @param argument The message that must be used to initiate the protocol.
	 * @param s The <code>DataStore</code> that will be used by this 
	 * <code>ArgumentConversationResponder</code>
	 */
	public ArgumentConversationResponder(ArgumentationAgent argAgent, DataStore ds) {
		super(argAgent);
		this.argAgent = argAgent;
		setDataStore(ds);
		
		// set FSM transitions
		// first establish a new conversation
		registerDefaultTransition(RECV_INIT_CONV, HANDLE_INIT_CONV);
		registerTransition(HANDLE_INIT_CONV, RECV_INIT_CONV, 0);
		registerDefaultTransition(HANDLE_INIT_CONV, RECEIVE_ARG);
		
		// exchange arguments
		registerTransition(RECEIVE_ARG, HANDLE_TIMEOUT_EXPIRED, MsgReceiver.TIMEOUT_EXPIRED);
		registerTransition(RECEIVE_ARG, HANDLE_NOT_UNDERSTOOD, ACLMessage.NOT_UNDERSTOOD);		
		registerTransition(RECEIVE_ARG, HANDLE_FAILURE, ACLMessage.FAILURE);	// exit the conversation if a failure occurs
		registerDefaultTransition(RECEIVE_ARG, CHECK_ARG_SEQUENCE);
		
		registerTransition(CHECK_ARG_SEQUENCE, HANDLE_OUT_OF_SEQUENCE, 0); // Exit the conversation if sequence of messages is broken 
		registerDefaultTransition(CHECK_ARG_SEQUENCE, CHECK_ARG_TOPIC);
		
		registerDefaultTransition(HANDLE_NOT_UNDERSTOOD, PREPARE_NEW_ARG);
		
		registerTransition(CHECK_ARG_TOPIC, HANDLE_OFF_TOPIC, 0);
		registerDefaultTransition(HANDLE_OFF_TOPIC, RECV_INIT_CONV);
		registerDefaultTransition(CHECK_ARG_TOPIC, PREPARE_NEW_ARG);
		
		registerTransition(PREPARE_NEW_ARG, RECV_INIT_CONV, ArgumentationMessage.ARG_ACCEPT_END_CONV);
		registerDefaultTransition(PREPARE_NEW_ARG, SEND_ARGUMENT, new String[] {});
		
		registerTransition(SEND_ARGUMENT, END_CONV, 0);
		registerDefaultTransition(SEND_ARGUMENT, RECEIVE_ARG);
		
		// Create and register the states that make up the FSM
		Behaviour b = null;
		
		// RECV_INIT_CONV
		initReceiver = new MsgReceiver(myAgent, MessageTemplate.MatchPerformative(ArgumentationMessage.ARG_START_CONV), MsgReceiver.INFINITE, getDataStore(), INIT_CONV_K);
		registerState(initReceiver, RECV_INIT_CONV);
		
		
		// HANDLE_INIT_CONV
		b = new OneShotBehaviour(myAgent) {
			private static final long     serialVersionUID = 15L;
			private int ret = 0;
			
			public void action() {
				DataStore ds = getDataStore();
				ACLMessage convInit = (ACLMessage)ds.get(INIT_CONV_K);
				
				if (allowConversation()) {
					conversationId = convInit.getConversationId();
					if (conversationId != null) {
						ret = 1;
						Map<String, ArgumentConversation> conversationRecords = ArgumentConversationResponder.this.argAgent.getConversationRecords();
						conversationRecords.put(conversationId, new ArgumentConversation());
					}
					//TODO !!! aici mai poate veni un apel care mai creeaza un astfel de behavior !!!
				}
			}

			public int onEnd() {
				return ret;
			}
		};
		b.setDataStore(getDataStore());	
		registerState(b, HANDLE_INIT_CONV);
		
		
		// SEND_ARGUMENT
		b = new OneShotBehaviour(myAgent) {
			private static final long serialVersionUID = 4L;
			
			public void action() {
				
				DataStore ds = getDataStore();
				ACLMessage arg = (ACLMessage) ds.get(SEND_ARG_K);
				long currentTime = System.currentTimeMillis();
				long minTimeout = -1;
				long deadline = -1;
				
				replyTemplate = MessageTemplate.MatchConversationId(conversationId);
				
				// reset notUnderstood and Failure flags
				replyTimeout = false;
				replyNotUnderStood = false;
				replyFailure = false;
				replyOffTopic = false;
				replyOutOfSequence = false;
				
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
					argReceiver.setTemplate(replyTemplate);
					argReceiver.setDeadline(deadline);
				}
			}
			
			public int onEnd() {
				
				DataStore ds = getDataStore();
				ACLMessage arg = (ACLMessage) ds.get(SEND_ARG_K);
				
				if (arg == null || !arg.getAllReceiver().hasNext()) {
					return 0;
				}
				return 1;
			}
		};
		b.setDataStore(getDataStore());		
		registerState(b, SEND_ARGUMENT);
		
		
		// RECEIVE_ARG
		argReceiver = new MsgReceiver(myAgent, MessageTemplate.MatchConversationId(conversationId), MsgReceiver.INFINITE, getDataStore(), RECV_ARG_K);
		registerState(argReceiver, RECEIVE_ARG);
		
		
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
				handleNotUnderstood((ACLMessage) getDataStore().get(SEND_ARG_K), 
									(ACLMessage) getDataStore().get(RECV_ARG_K));
			}
		};
		b.setDataStore(getDataStore());		
		registerState(b, HANDLE_NOT_UNDERSTOOD);
		
		
		// HANDLE_FAILURE
		b = new OneShotBehaviour(myAgent) {
			private static final long     serialVersionUID = 6L;
			
			public void action() {
				replyFailure = true;
				handleFailure((ACLMessage) getDataStore().get(SEND_ARG_K),
							 (ACLMessage) getDataStore().get(RECV_ARG_K));
				//myAgent.removeBehaviour(ArgumentConversationResponder.this);
				Map<String, ArgumentConversation> conversationRecords = ArgumentConversationResponder.this.argAgent.getConversationRecords();
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
				ACLMessage sentArgMsg = (ACLMessage)getDataStore().get(SEND_ARG_K);
				ACLMessage recvArgMsg = (ACLMessage)getDataStore().get(RECV_ARG_K);
				
				SimpleArgumentStructure sentArg = null;
				SimpleArgumentStructure recvArg = null;
				
				if (sentArgMsg == null) {	// if no message has yet been sent by the responder
					ret = 1;				// then required sequence is respected
					return;
				}
				
				try {
					sentArg = (SimpleArgumentStructure)sentArgMsg.getContentObject();
					recvArg = (SimpleArgumentStructure)recvArgMsg.getContentObject();
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
					//TODO - la fel ca la initiator
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
				ACLMessage sentArg = (ACLMessage)getDataStore().get(SEND_ARG_K);
				ACLMessage recvArg = (ACLMessage)getDataStore().get(RECV_ARG_K);
				ret = argumentOnTopic(sentArg, recvArg); 
			}
			
			public int onEnd() {
				if (ret) {
					return 1;
				}
				else {
					return 0;
				}
			}
		};
		b.setDataStore(getDataStore());		
		registerState(b, CHECK_ARG_TOPIC);
		
		
		// HANDLE_OUT_OF_SEQUENCE
		b = new OneShotBehaviour(myAgent) {
			private static final long     serialVersionUID = 13L;
			
			public void action() {
				ACLMessage recvMsg = (ACLMessage) getDataStore().get(RECV_ARG_K);
				ACLMessage msg = recvMsg.createReply();
				msg.setPerformative(ArgumentationMessage.ARG_OUT_OF_SEQ);
				msg.setConversationId(conversationId);
				
				myAgent.send(msg);
				//myAgent.removeBehaviour(ArgumentConversationResponder.this);
				Map<String, ArgumentConversation> conversationRecords = ArgumentConversationResponder.this.argAgent.getConversationRecords();
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
				handleOffTopic((ACLMessage) getDataStore().get(SEND_ARG_K), 
								(ACLMessage) getDataStore().get(RECV_ARG_K));
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
				ACLMessage sentArg = (ACLMessage)getDataStore().get(SEND_ARG_K);
				ACLMessage recvArg = (ACLMessage)getDataStore().get(RECV_ARG_K);
				
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
				
				getDataStore().put(SEND_ARG_K, new_arg);
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
				//myAgent.removeBehaviour(ArgumentConversationResponder.this);
				Map<String, ArgumentConversation> conversationRecords = ArgumentConversationResponder.this.argAgent.getConversationRecords();
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
	 * handles the case in which the received message is a conversation end request.
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
	 * determines if the argument <code>recvArg</code> is on the topic of discussion or not.
	 * the default implementation returns true.
	 * programmers may wish to override this method to react to this event.
	 * @param sentArg the argument sent previously
	 * @param recvArg the argument received
	 * @return returns true if the received argument is on the topic of discussion and false otherwise
	 */
	protected boolean argumentOnTopic(ACLMessage sentArg, ACLMessage recvArg) {
		return true;
	}
	
	/**
	 * determines if this conversation is to be engaged or not.
	 * the default implementation returns true.
	 * programmers may wish to override this method to react to this event.
	 * @return returns true if the received conversation request is to be honored and false otherwise
	 */
	protected boolean allowConversation() {
		return true;
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
}
