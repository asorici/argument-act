package onto.impl;


import jade.util.leap.*;
import onto.*;

/**
* Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#PresumptiveInferenceScheme
* @author OntologyBeanGenerator v4.1
* @version 2011/02/8, 19:01:12
*/
public class DefaultPresumptiveInferenceScheme implements PresumptiveInferenceScheme {

  private static final long serialVersionUID = -3637768939741971919L;

  private String _internalInstanceName = null;

  public DefaultPresumptiveInferenceScheme() {
    this._internalInstanceName = "";
  }

  public DefaultPresumptiveInferenceScheme(String instance_name) {
    this._internalInstanceName = instance_name;
  }

  public String toString() {
    return _internalInstanceName;
  }

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#hasException
   */
   private List hasException = new ArrayList();
   public void addHasException(ConflictScheme elem) { 
     hasException.add(elem);
   }
   public boolean removeHasException(ConflictScheme elem) {
     boolean result = hasException.remove(elem);
     return result;
   }
   public void clearAllHasException() {
     hasException.clear();
   }
   public Iterator getAllHasException() {return hasException.iterator(); }
   public List getHasException() {return hasException; }
   public void setHasException(List l) {hasException = l; }

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#hasConclusionDescription
   */
   private ConclusionDesc hasConclusionDescription;
   public void setHasConclusionDescription(ConclusionDesc value) { 
    this.hasConclusionDescription=value;
   }
   public ConclusionDesc getHasConclusionDescription() {
     return this.hasConclusionDescription;
   }

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#hasPresumption
   */
   private List hasPresumption = new ArrayList();
   public void addHasPresumption(Presumption elem) { 
     hasPresumption.add(elem);
   }
   public boolean removeHasPresumption(Presumption elem) {
     boolean result = hasPresumption.remove(elem);
     return result;
   }
   public void clearAllHasPresumption() {
     hasPresumption.clear();
   }
   public Iterator getAllHasPresumption() {return hasPresumption.iterator(); }
   public List getHasPresumption() {return hasPresumption; }
   public void setHasPresumption(List l) {hasPresumption = l; }

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#hasPremiseDescription
   */
   private List hasPremiseDescription = new ArrayList();
   public void addHasPremiseDescription(PremiseDesc elem) { 
     hasPremiseDescription.add(elem);
   }
   public boolean removeHasPremiseDescription(PremiseDesc elem) {
     boolean result = hasPremiseDescription.remove(elem);
     return result;
   }
   public void clearAllHasPremiseDescription() {
     hasPremiseDescription.clear();
   }
   public Iterator getAllHasPremiseDescription() {return hasPremiseDescription.iterator(); }
   public List getHasPremiseDescription() {return hasPremiseDescription; }
   public void setHasPremiseDescription(List l) {hasPremiseDescription = l; }

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#hasSchemeName
   */
   private String hasSchemeName;
   public void setHasSchemeName(String value) { 
    this.hasSchemeName=value;
   }
   public String getHasSchemeName() {
     return this.hasSchemeName;
   }

}
