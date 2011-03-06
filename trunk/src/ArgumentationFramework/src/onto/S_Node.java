package onto;

import jade.util.leap.*;

/**
 * Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#S-Node
 * 
 * @author OntologyBeanGenerator v4.1
 * @version 2011/02/8, 19:01:12
 */
public interface S_Node extends Node {

	/**
	 * Protege name:
	 * http://www.owl-ontologies.com/Ontology1295096279.owl#edgeFromSNode
	 */
	public void addEdgeFromSNode(Node elem);

	public boolean removeEdgeFromSNode(Node elem);

	public void clearAllEdgeFromSNode();

	public Iterator getAllEdgeFromSNode();

	public List getEdgeFromSNode();

	public void setEdgeFromSNode(List l);

	/**
	 * Protege name:
	 * http://www.owl-ontologies.com/Ontology1295096279.owl#fulfilsScheme
	 */
	public void setFulfilsScheme(Scheme value);

	public Scheme getFulfilsScheme();

	/**
	 * Equality test used for internal argument graph modeling
	 */
	public boolean isEqual(S_Node elem);
}
