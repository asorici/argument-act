package agents;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import argproto.ArgumentationMessage;
import behaviors.InitiatorBehavior;
import behaviors.ResponderBehavior;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class ArgumentationAgent extends Agent {
	AID pro = new AID("pro", AID.ISLOCALNAME);
	AID con = new AID("con", AID.ISLOCALNAME);
	
	// Put agent initializations here
	@Override
	protected void setup() {
		System.out.println("Hello! Argumentation-agent " + getAID().getName() + " is ready.");
		
		String agentType = "con";
		
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			agentType = (String)args[0];
		}
		
		System.out.println(agentType);
		
		if (agentType.equalsIgnoreCase("pro")) {
			System.out.println("[agent " + getLocalName() + "]" + " sending first ASSERT message A.");
			ACLMessage argument = new ACLMessage(ArgumentationMessage.ARG_ASSERT);
			argument.setContent("A");
			argument.addReceiver(con);
			
			InitiatorBehavior ib = new InitiatorBehavior(this, argument);
			addBehaviour(ib);
		}
		
		ResponderBehavior rb = new ResponderBehavior(this);
		addBehaviour(rb);
	}
	
	@Override
	protected void takeDown() {
		System.out.println("Argumentation-agent " + getAID().getName() + " terminating.");
	}
}
