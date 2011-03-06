package onto.impl;


import onto.*;

/**
* Protege name: http://www.owl-ontologies.com/Ontology1295096279.owl#Node
* @author OntologyBeanGenerator v4.1
* @version 2011/02/8, 19:01:12
*/
public class DefaultNode implements Node {

  private static final long serialVersionUID = -3637768939741971919L;

  private String _internalInstanceName = null;

  public DefaultNode() {
    this._internalInstanceName = "";
  }

  public DefaultNode(String instance_name) {
    this._internalInstanceName = instance_name;
  }

  public String toString() {
    return _internalInstanceName;
  }

}
