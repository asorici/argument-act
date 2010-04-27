from xml.dom import minidom
import ArgumentationBasicSchema

class AMLParser:
        """
        parse aml file for argument text and its structure
        """
        
        def __init__ (self, filename):
                self.argdoc = None
                self.filename = filename
                
                #cache level is not currently used
                self.cacheLevel = None
                
                self.text = None
                self.schemaList = None
                self.argUnits = None
                
                self.parseFile()
                
        #returns a list containing all schema used in the file
        def getAllSchema(self):
                if(self.schemaList == None):
                        self.schemaList = []
                        schemeSetNode = self.argdoc.getElementsByTagName("SCHEMESET")[0]
                        schemeSetNode = schemeSetNode.getElementsByTagName("SCHEME")
                        for scheme in schemeSetNode:
                                name = scheme.getElementsByTagName("NAME")[0].firstChild.nodeValue
                                premises, conclusion = self.parseSchemeForm(scheme.getElementsByTagName("FORM")[0])
                                s = ArgumentationBasicSchema.ArgumentationBasicSchema(name, premises, conclusion)
                                self.schemaList.append(s)
                return self.schemaList

        def parseSchemeForm(self, node):
                premiseList = node.getElementsByTagName("PREMISE")                                                                        
                conclusion = node.getElementsByTagName("CONCLUSION")[0].firstChild.nodeValue
                premises = []
                for premise in premiseList:
                        premises.append(premise.firstChild.nodeValue)
                return premises, conclusion
        
        #returns a string containing the plain text in the file
        def getText(self):
                if(self.text == None):
                        try:
                                self.text = self.argdoc.getElementsByTagName('TEXT')[0];
                                self.text = self.text.firstChild.nodeValue;
                        except:
                                raise
                return self.text
        
        #returns all argumentation untis in the file
        def getArgumentationUnits(self):
                return None
        
        def parseFile(self):
                """ returns a dictionary containing the argument text, the top conclusion and supporting premises. """
                
                try:
                        self.argdoc = minidom.parse(self.filename)
                        
                        #text = self.getArgumentText()
                        #conclusion = self.getTopConclusion()
                        #premiseList = self.getPremiseList()
                        
                        #return {"text" : text, "conclusion" : conclusion, "premiseList" : premiseList}
                except:
                        self.argdoc = None
                        raise
                                
        def getTopConclusion(self):
                if self.argdoc == None:
                        return None
                else:
                        try:
                                """ get top most AU tag - the first PROP tag after that is the top conclusion """
                                
                                auNode = self.argdoc.getElementsByTagName('AU')[0]
                                return self.__getNodeData(auNode)
                        except:
                                raise
        
        def getPremiseList(self):
                if self.argdoc == None:
                        return None
                else:
                        try:
                                """ get the argument premises - disregarding argumentative structure within premises """
                                auNode = self.argdoc.getElementsByTagName('AU')[0]
                                
                                premises = self.__getPremises(auNode)
                                
                                return premises
                        except:
                                raise
                                
        def __getPremises(self, argnode):
                premises = []
                laNodes = []
                caNodes = []
                
                childNodes = argnode.childNodes
                for node in childNodes:
                        if node.nodeName == 'LA':
                                laNodes.append(node)
                        elif node.nodeName == 'CA':
                                caNodes.append(node)
                
                for lanode in laNodes:
                        auNodes = lanode.childNodes
                        for aunode in auNodes:
                                if aunode.nodeName == 'AU':
                                        data = self.__getNodeData(aunode)
                                        if data:
                                                premises.append(data)
                                                
                                        premises += self.__getPremises(aunode)
                
                for canode in caNodes:
                        auNodes = canode.childNodes
                        for aunode in auNodes:
                                if aunode.nodeName == 'AU':
                                        data = self.__getNodeData(aunode)
                                        if data:
                                                premises.append(data)
                                                
                                        premises += self.__getPremises(aunode)
                
                return premises
                
        
        def __getNodeData(self, argnode):
                dict = {}
                
                propNode = argnode.getElementsByTagName('PROP')[0]
                proptextNode = propNode.getElementsByTagName('PROPTEXT')[0]
                schemeNode = propNode.getElementsByTagName('INSCHEME')[0]
                
                dict['text'] = proptextNode.firstChild.nodeValue
                dict['missing'] = propNode.getAttribute('missing')
                dict['scheme'] = schemeNode.getAttribute('scheme')
                
                return dict

a = AMLParser("f:\\proiecte\\NLP\\araucaria-aml-files\\arg_1.aml")
a.parseFile()
print a.getText()
