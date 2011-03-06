package argproto;

import java.io.Serializable;
import java.util.ArrayList;

import onto.Premise;
import onto.impl.DefaultConclusion;
import onto.impl.DefaultI_Node;
import onto.impl.DefaultRA_Node;

@SuppressWarnings("serial")
public class SimpleArgumentStructure implements Serializable {
	protected DefaultConclusion conclusion;
	protected DefaultRA_Node inferenceRule;
	protected ArrayList<Premise> premises;
	
	public SimpleArgumentStructure(DefaultConclusion conclusion, DefaultRA_Node inferenceRule, ArrayList<Premise> premises) {
		this.conclusion = conclusion;
		this.inferenceRule = inferenceRule;
		this.premises = premises;
	}
	
	public DefaultConclusion getConclusion() {
		return conclusion;
	}

	public void setConclusion(DefaultConclusion conclusion) {
		this.conclusion = conclusion;
	}

	public DefaultRA_Node getInferenceRule() {
		return inferenceRule;
	}

	public void setInferenceRule(DefaultRA_Node inferenceRule) {
		this.inferenceRule = inferenceRule;
	}
	
	public ArrayList<Premise> getPremises() {
		return premises;
	}

	public void setPremises(ArrayList<Premise> premises) {
		this.premises = premises;
	}
	
	public boolean isChallenged(SimpleArgumentStructure arg) {
		return true;
	}

	
}
