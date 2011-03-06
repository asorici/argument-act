package onto.impl;

import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.onto.Ontology;
import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;
import jade.util.leap.List;

import java.util.Map;

import onto.CA_Node;
import onto.I_Node;
import onto.S_Node;
import agents.ArgumentationAgent;

/**
 * Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#I-Node
 * 
 * @author OntologyBeanGenerator v4.1
 * @version 2011/02/8, 19:01:12
 */
public class DefaultI_Node implements I_Node {

	private static final long serialVersionUID = -3637768939741971919L;

	private String _internalInstanceName = null;

	public DefaultI_Node() {
		this._internalInstanceName = "";
	}

	public DefaultI_Node(String instance_name) {
		this._internalInstanceName = instance_name;
	}

	public String toString() {
		return _internalInstanceName;
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
