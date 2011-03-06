package onto.impl;

import jade.util.leap.*;
import onto.*;

/**
 * Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#CA-Node
 * 
 * @author OntologyBeanGenerator v4.1
 * @version 2011/02/8, 19:01:12
 */
public class DefaultCA_Node implements CA_Node {

	private static final long serialVersionUID = -3637768939741971919L;

	private String _internalInstanceName = null;

	public DefaultCA_Node() {
		this._internalInstanceName = "";
	}

	public DefaultCA_Node(String instance_name) {
		this._internalInstanceName = instance_name;
	}

	public String toString() {
		return _internalInstanceName;
	}

	/**
	 * Protege name:
	 * http://www.owl-ontologies.com/Ontology1295096279.owl#edgeFromSNode
	 */
	private List edgeFromSNode = new ArrayList();

	public void addEdgeFromSNode(Node elem) {
		edgeFromSNode.add(elem);
	}

	public boolean removeEdgeFromSNode(Node elem) {
		boolean result = edgeFromSNode.remove(elem);
		return result;
	}

	public void clearAllEdgeFromSNode() {
		edgeFromSNode.clear();
	}

	public Iterator getAllEdgeFromSNode() {
		return edgeFromSNode.iterator();
	}

	public List getEdgeFromSNode() {
		return edgeFromSNode;
	}

	public void setEdgeFromSNode(List l) {
		edgeFromSNode = l;
	}

	/**
	 * Protege name:
	 * http://www.owl-ontologies.com/Ontology1295096279.owl#caNode_Attacks
	 */
	private List caNode_Attacks = new ArrayList();

	public void addCaNode_Attacks(I_Node elem) {
		caNode_Attacks.add(elem);
	}

	public boolean removeCaNode_Attacks(I_Node elem) {
		boolean result = caNode_Attacks.remove(elem);
		return result;
	}

	public void clearAllCaNode_Attacks() {
		caNode_Attacks.clear();
	}

	public Iterator getAllCaNode_Attacks() {
		return caNode_Attacks.iterator();
	}

	public List getCaNode_Attacks() {
		return caNode_Attacks;
	}

	public void setCaNode_Attacks(List l) {
		caNode_Attacks = l;
	}

	/**
	 * Protege name:
	 * http://www.owl-ontologies.com/Ontology1295096279.owl#isAttacked
	 */
	private List isAttacked = new ArrayList();

	public void addIsAttacked(Conclusion elem) {
		isAttacked.add(elem);
	}

	public boolean removeIsAttacked(Conclusion elem) {
		boolean result = isAttacked.remove(elem);
		return result;
	}

	public void clearAllIsAttacked() {
		isAttacked.clear();
	}

	public Iterator getAllIsAttacked() {
		return isAttacked.iterator();
	}

	public List getIsAttacked() {
		return isAttacked;
	}

	public void setIsAttacked(List l) {
		isAttacked = l;
	}

	/**
	 * Protege name:
	 * http://www.owl-ontologies.com/Ontology1295096279.owl#fulfilsScheme
	 */
	private Scheme fulfilsScheme;

	public void setFulfilsScheme(Scheme value) {
		this.fulfilsScheme = value;
	}

	public Scheme getFulfilsScheme() {
		return this.fulfilsScheme;
	}

	@Override
	public boolean isEqual(S_Node elem) {
		// TODO Auto-generated method stub
		return false;
	}

}
