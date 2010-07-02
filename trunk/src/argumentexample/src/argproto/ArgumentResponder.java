package argproto;

import java.util.Date;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.states.MsgReceiver;
import jade.proto.states.ReplySender;

public class ArgumentResponder extends FSMBehaviour {
	
	/**
	 Key to retrieve from the DataStore of the behaviour the last received
	 ACLMessage
	 */
	public final String RECEIVED_KEY = "__received_key" + hashCode();
	
	
	/**
	 Key to set into the DataStore of the behaviour the new ACLMessage 
	 to be sent back to the initiator as a reply.
	 */
	public final String REPLY_KEY = "__reply_key" + hashCode();
	
	
	//#APIDOC_EXCLUDE_BEGIN
	// FSM states names
	protected static final String RECEIVE_NEXT = "Receive-Next";
	protected static final String FORM_REPLY = "Form-Reply";
	protected static final String SEND_REPLY = "Send-Reply";
	protected static final String DUMMY_FINAL = "Dummy-Final";
	
	public ArgumentResponder(Agent a) {
		this(a, new DataStore());
	}
	
	/**
	 * Constructs an <code>ArgumentResponder</code> behaviour
	 * @param a The agent performing the protocol
	 * @param store The <code>DataStore</code> that will be used by this <code>ArgumentResponder</code>
	 */
	public ArgumentResponder(Agent a, DataStore store) {
		super(a);
		setDataStore(store);
		
		//registerTransition(RECEIVE_NEXT, FORM_REPLY, ArgumentationMessage.ARG_ASSERT, new String[]{RECEIVE_NEXT});
		registerDefaultTransition(RECEIVE_NEXT, FORM_REPLY, new String[]{RECEIVE_NEXT});
		registerDefaultTransition(FORM_REPLY, SEND_REPLY);
		//registerDefaultTransition(SEND_REPLY, DUMMY_FINAL);
		registerDefaultTransition(SEND_REPLY, RECEIVE_NEXT);
		
		Behaviour b;
		
		// RECEIVE_NEXT 
		b = new NextMsgReceiver(myAgent, getDataStore(), RECEIVED_KEY);
		registerFirstState(b, RECEIVE_NEXT);
		
		// FORM_REPLY
		b = new OneShotBehaviour(myAgent) {
			private static final long     serialVersionUID = 3487495895818004L;
			
			public void action() {
				ACLMessage reply = handleFormReply((ACLMessage) getDataStore().get(RECEIVED_KEY));
				getDataStore().put(REPLY_KEY, reply);
			}
		};
		registerDSState(b, FORM_REPLY);
		
		// SEND_REPLY
		b = new NextReplySender(myAgent, REPLY_KEY, RECEIVED_KEY);
		registerDSState(b, SEND_REPLY);
		
		// DUMMY_FINAL
		b = new DummyFinal(myAgent);
		registerLastState(b, DUMMY_FINAL);
		b.setDataStore(getDataStore());
	}
	
	private void setMessageToReplyKey(String key) {
		ReplySender rs = (ReplySender) getState(SEND_REPLY);
		rs.setMsgKey(key);
	}
	
	/**
	 Utility method to register a behaviour in a state of the 
	 protocol and set the DataStore appropriately
	 */
	protected void registerDSState(Behaviour b, String name) {
		b.setDataStore(getDataStore());
		registerState(b,name);
	}
	
	/**
	 * form the reply message to be sent to the initiator
	 * if left default, this method returns null leading to a
	 * ACLMessage.NOTUNDERSTOOD response to the initiator
	 * Note that the message you return must specify the argument performative
	 * (ACCEPT, ASSERT or CHALLENGE) using the setUserDefinedParameter function
	 * @param recvMsg - the argument received from the initiator
	 * @return
	 */
	protected ACLMessage handleFormReply(ACLMessage recvMsg) {
		return null;
	}
	
	/**
	 This method can be redefined by protocol specific implementations
	 to customize a reply that is going to be sent back to the initiator.
	 This default implementation does nothing.
	 */
	protected void beforeReply(ACLMessage reply) {
	}
	
	/**
	 This method can be redefined by protocol specific implementations
	 to update the status of the protocol just after a reply has been sent.
	 This default implementation does nothing.
	 */
	protected void afterReply(ACLMessage reply) {
	}
	
	/**
	 This method can be redefined by protocol specific implementations
	 to take proper actions after the completion of the current protocol
	 session.
	 */
	protected void sessionTerminated() {
	}
	
	/**
	 Inner class NextMsgReceiver
	 */
	private static class NextMsgReceiver extends MsgReceiver {
		private static final long     serialVersionUID = 4487495895818001L;
		private static MessageTemplate mt = MessageTemplate.MatchPerformative(ArgumentationMessage.ARG_ASSERT);
		
		public NextMsgReceiver(Agent a, DataStore ds, String key) {
			super(a, mt, INFINITE, ds, key);
		}
		
		public int onEnd() {
			// The next reply (if any) will be a reply to the received message 
			ArgumentResponder parent = (ArgumentResponder) getParent();
			parent.setMessageToReplyKey((String) receivedMsgKey);
			
			//System.out.println("received message: " + ((ACLMessage)this.getDataStore().get(receivedMsgKey)).getContent());
			
			return super.onEnd();
		}
	} // End of inner class NextMsgReceiver
	
	/**
	 Inner class NextReplySender
	 */
	private static class NextReplySender extends ReplySender {
		private static final long     serialVersionUID = 4487495895818002L;
		
		public NextReplySender(Agent a, String replyKey, String msgKey) {
			super(a, replyKey, msgKey);
		}
		
		public void onStart() {
			ArgumentResponder parent = (ArgumentResponder) getParent();
			ACLMessage reply = (ACLMessage)getDataStore().get(parent.REPLY_KEY);
			ACLMessage recvMsg = (ACLMessage)getDataStore().get(parent.RECEIVED_KEY);
			if (reply == null) {
				reply = recvMsg.createReply();
				reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
				getDataStore().put(parent.REPLY_KEY, reply);
			}
			parent.beforeReply(reply);
		}
		
		public int onEnd() {
			int ret = super.onEnd();
			ArgumentResponder parent = (ArgumentResponder) getParent();
			ACLMessage reply = (ACLMessage)getDataStore().get(parent.REPLY_KEY);
			parent.afterReply(reply);
			return ret;
		}
		
	} // End of inner class NextReplySender
	
	
	/**
	 Inner class DummyFinal
	 */
	private static class DummyFinal extends OneShotBehaviour {
		private static final long     serialVersionUID = 4487495895818003L;
		
		public DummyFinal(Agent a) {
			super(a);
		}
		
		public void action() {
			ArgumentResponder parent = (ArgumentResponder) getParent();
			parent.sessionTerminated();
		}
	} // End of inner class DummyFinal 
}
