package onto;


import jade.util.leap.*;

/**
* Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#RA-Node
* @author OntologyBeanGenerator v4.1
* @version 2011/02/8, 19:01:12
*/
public interface RA_Node extends S_Node {

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#hasConclusion
   */
   public void setHasConclusion(Conclusion value);
   public Conclusion getHasConclusion();

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#hasPremise
   */
   public void addHasPremise(Premise elem);
   public boolean removeHasPremise(Premise elem);
   public void clearAllHasPremise();
   public Iterator getAllHasPremise();
   public List getHasPremise();
   public void setHasPremise(List l);

}
