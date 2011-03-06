package onto;

import jade.util.leap.*;

/**
 * Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#Premise
 * 
 * @author OntologyBeanGenerator v4.1
 * @version 2011/02/8, 19:01:12
 */
public interface Premise extends I_Node {

	/**
	 * Protege name:
	 * http://www.owl-ontologies.com/Ontology1295096279.owl#fulfilsPremiseDesc
	 */
	public void setFulfilsPremiseDesc(PremiseDesc value);

	public PremiseDesc getFulfilsPremiseDesc();

	/**
	 * Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#
	 * underminesPresumption
	 */
	public void setUnderminesPresumption(Presumption value);

	public Presumption getUnderminesPresumption();

	/**
	 * Protege name:
	 * http://www.owl-ontologies.com/Ontology1295096279.owl#supports
	 */
	public void addSupports(RA_Node elem);

	public boolean removeSupports(RA_Node elem);

	public void clearAllSupports();

	public Iterator getAllSupports();

	public List getSupports();

	public void setSupports(List l);

}
