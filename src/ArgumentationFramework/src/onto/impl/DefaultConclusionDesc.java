package onto.impl;


import onto.*;

/**
* Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#ConclusionDesc
* @author OntologyBeanGenerator v4.1
* @version 2011/02/8, 19:01:12
*/
public class DefaultConclusionDesc implements ConclusionDesc {

  private static final long serialVersionUID = -3637768939741971919L;

  private String _internalInstanceName = null;

  public DefaultConclusionDesc() {
    this._internalInstanceName = "";
  }

  public DefaultConclusionDesc(String instance_name) {
    this._internalInstanceName = instance_name;
  }

  public String toString() {
    return _internalInstanceName;
  }

   /**
   * Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#hasDescription
   */
   private String hasDescription;
   public void setHasDescription(String value) { 
    this.hasDescription=value;
   }
   public String getHasDescription() {
     return this.hasDescription;
   }

}
