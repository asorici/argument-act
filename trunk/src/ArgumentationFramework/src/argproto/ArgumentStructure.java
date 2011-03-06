package argproto;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class ArgumentStructure<T> implements Serializable {
	protected T conclusion;
	protected ArrayList<ArgumentStructure<T>> premises;
	
	public ArgumentStructure(T conclusion, ArrayList<ArgumentStructure<T>> premises) {
		this.conclusion = conclusion;
		this.premises = premises;
	}
	
	public T getConclusion() {
		return conclusion;
	}

	public void setConclusion(T conclusion) {
		this.conclusion = conclusion;
	}

	public ArrayList<ArgumentStructure<T>> getPremises() {
		return premises;
	}

	public void setPremises(ArrayList<ArgumentStructure<T>> premises) {
		this.premises = premises;
	}
	
	public boolean isChallenged(T arg) {
		return true;
	}
}
