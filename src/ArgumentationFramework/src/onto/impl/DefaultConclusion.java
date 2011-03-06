package onto.impl;

import jade.util.leap.*;
import onto.*;

/**
 * Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#Conclusion
 * 
 * @author OntologyBeanGenerator v4.1
 * @version 2011/02/8, 19:01:12
 */
public class DefaultConclusion implements Conclusion {

	private static final long serialVersionUID = -3637768939741971919L;

	private String _internalInstanceName = null;

	public DefaultConclusion() {
		this._internalInstanceName = "";
	}

	public DefaultConclusion(String instance_name) {
		this._internalInstanceName = instance_name;
	}

	public String toString() {
		return _internalInstanceName;
	}

	/**
	 * Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#
	 * fulfilsConclusionDesc
	 */
	private ConclusionDesc fulfilsConclusionDesc;

	public void setFulfilsConclusionDesc(ConclusionDesc value) {
		this.fulfilsConclusionDesc = value;
	}

	public ConclusionDesc getFulfilsConclusionDesc() {
		return this.fulfilsConclusionDesc;
	}

	/**
	 * Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#text
	 */
	private String text;

	public void setText(String value) {
		this.text = value;
	}

	public String getText() {
		return this.text;
	}

	/**
	 * Protege name:
	 * http://www.owl-ontologies.com/Ontology1295096279.owl#edgeFromINode
	 */
	private List edgeFromINode = new ArrayList();

	public void addEdgeFromINode(S_Node elem) {
		edgeFromINode.add(elem);
	}

	public boolean removeEdgeFromINode(S_Node elem) {
		boolean result = edgeFromINode.remove(elem);
		return result;
	}

	public void clearAllEdgeFromINode() {
		edgeFromINode.clear();
	}

	public Iterator getAllEdgeFromINode() {
		return edgeFromINode.iterator();
	}

	public List getEdgeFromINode() {
		return edgeFromINode;
	}

	public void setEdgeFromINode(List l) {
		edgeFromINode = l;
	}

	/**
	 * Protege name:
	 * http://www.owl-ontologies.com/Ontology1295096279.owl#attacks
	 */
	private List attacks = new ArrayList();

	public void addAttacks(CA_Node elem) {
		attacks.add(elem);
	}

	public boolean removeAttacks(CA_Node elem) {
		boolean result = attacks.remove(elem);
		return result;
	}

	public void clearAllAttacks() {
		attacks.clear();
	}

	public Iterator getAllAttacks() {
		return attacks.iterator();
	}

	public List getAttacks() {
		return attacks;
	}

	public void setAttacks(List l) {
		attacks = l;
	}

	/**
	 * Protege name:
	 * http://www.owl-ontologies.com/Ontology1295096279.owl#caNode_isAttacked
	 */
	private List caNode_isAttacked = new ArrayList();

	public void addCaNode_isAttacked(CA_Node elem) {
		caNode_isAttacked.add(elem);
	}

	public boolean removeCaNode_isAttacked(CA_Node elem) {
		boolean result = caNode_isAttacked.remove(elem);
		return result;
	}

	public void clearAllCaNode_isAttacked() {
		caNode_isAttacked.clear();
	}

	public Iterator getAllCaNode_isAttacked() {
		return caNode_isAttacked.iterator();
	}

	public List getCaNode_isAttacked() {
		return caNode_isAttacked;
	}

	public void setCaNode_isAttacked(List l) {
		caNode_isAttacked = l;
	}

}
