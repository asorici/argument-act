package onto;

import jade.content.lang.Codec;
import jade.content.onto.Ontology;

import jade.util.leap.Iterator;
import jade.util.leap.List;
import java.util.Map;

/**
 * Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#I-Node
 * 
 * @author OntologyBeanGenerator v4.1
 * @version 2011/02/8, 19:01:12
 */
public interface I_Node extends Node {

	/**
	 * Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#text
	 */
	public void setText(String value);

	public String getText();

	/**
	 * Protege name:
	 * http://www.owl-ontologies.com/Ontology1295096279.owl#edgeFromINode
	 */
	public void addEdgeFromINode(S_Node elem);

	public boolean removeEdgeFromINode(S_Node elem);

	public void clearAllEdgeFromINode();

	public Iterator getAllEdgeFromINode();

	public List getEdgeFromINode();

	public void setEdgeFromINode(List l);

	/**
	 * Protege name:
	 * http://www.owl-ontologies.com/Ontology1295096279.owl#attacks
	 */
	public void addAttacks(CA_Node elem);

	public boolean removeAttacks(CA_Node elem);

	public void clearAllAttacks();

	public Iterator getAllAttacks();

	public List getAttacks();

	public void setAttacks(List l);

	/**
	 * Protege name:
	 * http://www.owl-ontologies.com/Ontology1295096279.owl#caNode_isAttacked
	 */
	public void addCaNode_isAttacked(CA_Node elem);

	public boolean removeCaNode_isAttacked(CA_Node elem);

	public void clearAllCaNode_isAttacked();

	public Iterator getAllCaNode_isAttacked();

	public List getCaNode_isAttacked();

	public void setCaNode_isAttacked(List l);

	/**
	 * Equality test used for internal argument graph modeling
	 */
	//public boolean isEqual(I_Node elem, Map<String, Codec> codecList, Map<String, Ontology> ontologyList);
	
}
