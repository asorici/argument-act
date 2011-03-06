package onto.impl;

import jade.util.leap.*;
import onto.*;

/**
 * Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#S-Node
 * 
 * @author OntologyBeanGenerator v4.1
 * @version 2011/02/8, 19:01:12
 */
public class DefaultS_Node implements S_Node {

	private static final long serialVersionUID = -3637768939741971919L;

	private String _internalInstanceName = null;

	public DefaultS_Node() {
		this._internalInstanceName = "";
	}

	public DefaultS_Node(String instance_name) {
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
