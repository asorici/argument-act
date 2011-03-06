package argutils;

import java.util.ArrayList;
import java.util.List;

import onto.Conclusion;
import onto.I_Node;
import onto.S_Node;
import argproto.SimpleArgumentStructure;

public class ArgumentationGraph {
	private Conclusion mainClaim;
	private List<I_Node> informationNodes = new ArrayList<I_Node>();
	private List<S_Node> inferenceNodes = new ArrayList<S_Node>();
	
	public ArgumentationGraph() {
	}
	
	public ArgumentationGraph(SimpleArgumentStructure initialClaim) {
		
	}

	public Conclusion getMainClaim() {
		return mainClaim;
	}

	public void setInitialClaim(SimpleArgumentStructure initialClaim) {
		this.mainClaim = initialClaim.getConclusion();
		informationNodes.add(mainClaim);
		
		inferenceNodes.add(initialClaim.getInferenceRule());
		
		for (I_Node n : initialClaim.getPremises()) {
			informationNodes.add(n);
		}
	}
	
	//TODO add attack or support relations between nodes as the conversation progresses
	
}