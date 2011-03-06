package onto;


import jade.util.leap.*;

/**
* Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#PresumptiveInferenceScheme
* @author OntologyBeanGenerator v4.1
* @version 2011/02/8, 19:01:12
*/
public interface PresumptiveInferenceScheme extends RuleScheme {

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#hasException
   */
   public void addHasException(ConflictScheme elem);
   public boolean removeHasException(ConflictScheme elem);
   public void clearAllHasException();
   public Iterator getAllHasException();
   public List getHasException();
   public void setHasException(List l);

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#hasPresumption
   */
   public void addHasPresumption(Presumption elem);
   public boolean removeHasPresumption(Presumption elem);
   public void clearAllHasPresumption();
   public Iterator getAllHasPresumption();
   public List getHasPresumption();
   public void setHasPresumption(List l);

}
