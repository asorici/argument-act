package onto;


import jade.util.leap.*;

/**
* Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#Scheme
* @author OntologyBeanGenerator v4.1
* @version 2011/02/8, 19:01:12
*/
public interface Scheme extends jade.content.Concept {

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#hasConclusionDescription
   */
   public void setHasConclusionDescription(ConclusionDesc value);
   public ConclusionDesc getHasConclusionDescription();

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#hasPremiseDescription
   */
   public void addHasPremiseDescription(PremiseDesc elem);
   public boolean removeHasPremiseDescription(PremiseDesc elem);
   public void clearAllHasPremiseDescription();
   public Iterator getAllHasPremiseDescription();
   public List getHasPremiseDescription();
   public void setHasPremiseDescription(List l);

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#hasSchemeName
   */
   public void setHasSchemeName(String value);
   public String getHasSchemeName();

}
