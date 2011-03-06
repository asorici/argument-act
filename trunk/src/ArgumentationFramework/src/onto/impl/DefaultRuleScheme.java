package onto.impl;


import jade.util.leap.*;
import onto.*;

/**
* Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#RuleScheme
* @author OntologyBeanGenerator v4.1
* @version 2011/02/8, 19:01:12
*/
public class DefaultRuleScheme implements RuleScheme {

  private static final long serialVersionUID = -3637768939741971919L;

  private String _internalInstanceName = null;

  public DefaultRuleScheme() {
    this._internalInstanceName = "";
  }

  public DefaultRuleScheme(String instance_name) {
    this._internalInstanceName = instance_name;
  }

  public String toString() {
    return _internalInstanceName;
  }

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
