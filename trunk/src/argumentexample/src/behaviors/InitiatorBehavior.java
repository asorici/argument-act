package behaviors;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import argproto.ArgumentInitiator;
import argproto.ArgumentationMessage;

public class InitiatorBehavior extends ArgumentInitiator {
	public InitiatorBehavior(Agent a, ACLMessage argument) {
		super(a, argument);
	}
	
	public void handleAccept(ACLMessage acceptMsg) {
		if (acceptMsg != null) {
			if (acceptMsg.getContent().equals("A")) {
				ACLMessage newmsg = new ACLMessage(ArgumentationMessage.ARG_ASSERT);
				newmsg.setContent("B");
				newmsg.addReceiver(new AID("con", AID.ISLOCALNAME));
				
				System.out.println("[agent " + myAgent.getLocalName() + "]" + " receiving ACCEPT message A " +
						"- sending new ASSERT B");
				
				
				myAgent.addBehaviour(new InitiatorBehavior(myAgent, newmsg));
				myAgent.removeBehaviour(this);
			}
			else {
				if (acceptMsg.getContent().equals("C")) {
					System.out.println("[agent " + myAgent.getLocalName() + "]" + " receiving ACCEPT message C " +
					"- ending conversation");
					System.out.println("conversation ended.");
					myAgent.doDelete();
				}
			}
		}
	}
	
	public void handleAssert(ACLMessage assertMsg) {
		
	}
	
	public void handleChallenge(ACLMessage challengeMsg) {
		if (challengeMsg != null) {
			if (challengeMsg.getContent().equals("B")) {
				ACLMessage newmsg = new ACLMessage(ArgumentationMessage.ARG_ASSERT);
				newmsg.setContent("C");
				newmsg.addReceiver(new AID("con", AID.ISLOCALNAME));
				
				System.out.println("[agent " + myAgent.getLocalName() + "]" + " receiving CHALLENGE message B " +
				"- sending new ASSERT C");
				
				myAgent.addBehaviour(new InitiatorBehavior(myAgent, newmsg));
				myAgent.removeBehaviour(this);
			}
		}
	}
}
