package onto;


import jade.util.leap.*;

/**
* Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#CA-Node
* @author OntologyBeanGenerator v4.1
* @version 2011/02/8, 19:01:12
*/
public interface CA_Node extends S_Node {

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#caNode_Attacks
   */
   public void addCaNode_Attacks(I_Node elem);
   public boolean removeCaNode_Attacks(I_Node elem);
   public void clearAllCaNode_Attacks();
   public Iterator getAllCaNode_Attacks();
   public List getCaNode_Attacks();
   public void setCaNode_Attacks(List l);

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#isAttacked
   */
   public void addIsAttacked(Conclusion elem);
   public boolean removeIsAttacked(Conclusion elem);
   public void clearAllIsAttacked();
   public Iterator getAllIsAttacked();
   public List getIsAttacked();
   public void setIsAttacked(List l);

}
