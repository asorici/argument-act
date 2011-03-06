package behaviors;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import argproto.ArgumentResponder;
import argutils.ArgumentationMessage;

public class ResponderBehavior extends ArgumentResponder {
	public ResponderBehavior(Agent a) {
		super(a);
	}
	
	public ACLMessage handleFormReply(ACLMessage recvMsg) {
		if (recvMsg != null) {
			//System.out.println("responding to received message from agent " + recvMsg.getSender().getLocalName());
			ACLMessage replyMsg = recvMsg.createReply();
			
			if (recvMsg.getContent().equals("A")) {
				replyMsg.setContent("A");
				replyMsg.setPerformative(ArgumentationMessage.ARG_ACCEPT);
				System.out.println("[agent " + myAgent.getLocalName() + "]" + " sending ACCEPT message A");
			}
			
			if (recvMsg.getContent().equals("B")) {
				replyMsg.setContent("B");
				replyMsg.setPerformative(ArgumentationMessage.ARG_CHALLENGE);
				System.out.println("[agent " + myAgent.getLocalName() + "]" + " sending CHALLENGE message B");
			}
			
			if (recvMsg.getContent().equals("C")) {
				replyMsg.setContent("C");
				replyMsg.setPerformative(ArgumentationMessage.ARG_ACCEPT);
				System.out.println("[agent " + myAgent.getLocalName() + "]" + " sending ACCEPT message C");
			}
			
			return replyMsg;
		}
		else {
			return null;
		}
	}
}
