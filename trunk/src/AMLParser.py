from xml.dom import minidom
import ArgumentationStructures

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
                try:
                        self.argdoc = minidom.parse(self.filename)
                except:
                        self.argdoc = None
                        raise
                
        #returns a list containing all schema used in the file
        def getAllSchema(self):
                if(self.schemaList == None):
                        self.schemaList = []
                        schemeSetNode = self.argdoc.getElementsByTagName("SCHEMESET")[0]
                        schemeSetNode = schemeSetNode.getElementsByTagName("SCHEME")
                        for scheme in schemeSetNode:
                                name = scheme.getElementsByTagName("NAME")[0].firstChild.nodeValue
                                premises, conclusion, cqs = self.parseScheme(scheme)
                                s = ArgumentationStructures.ArgumentationBasicSchema(name, premises, conclusion, cqs)
                                self.schemaList.append(s)
                return self.schemaList

        #parses a single schema entry
        def parseScheme(self, scheme):
                cqList = scheme.getElementsByTagName("CQ")
                premises, conclusion = self.parseSchemeForm(scheme.getElementsByTagName("FORM")[0])
                return premises, conclusion, self.__parseListOfElements(cqList)

        #parses the inner form element in a schema
        def parseSchemeForm(self, node):
                premiseList = node.getElementsByTagName("PREMISE")                                                                        
                conclusion = node.getElementsByTagName("CONCLUSION")[0].firstChild.nodeValue
                return self.__parseListOfElements(premiseList), conclusion

        #general purpose parser function that extracts the content for the first child of each node in the given node list
        def __parseListOfElements(self, nodeList):
                l = []
                for node in nodeList:
                        l.append(node.firstChild.nodeValue)
                return l
        
        #returns a string containing the plain text in the file
        def getText(self):
                if(self.text == None):
                        try:
                                self.text = self.argdoc.getElementsByTagName('TEXT')[0];
                                self.text = self.text.firstChild.nodeValue;
                        except:
                                raise
                return self.text

        #returns all argumentation untis for a given node
        def getArgumentationUnits(self, node = None):
                if (node == None):
                        node = self.argdoc.childNodes[1] #the first node is the DTD declaration
                l = []
                for argument in node.childNodes:
                        if (argument.nodeName == "AU"):
                                l.append(self.getArgumentationUnit(argument))
                return l

        def getArgumentationUnit(self, argNode):
                propNode = argNode.getElementsByTagName('PROP')[0]
                proptextNode = argNode.getElementsByTagName('PROPTEXT')[0]
                schemeNode = argNode.getElementsByTagName('INSCHEME')[0]
                refutation = None
                refNode = argNode.getElementsByTagName("REFUTATION")
                if (refNode != []):
                        refutation = self.getArgumentationUnit(refNode[0].firstChild)
                laList, caList = self.getPremises(argNode)
                return ArgumentationStructures.ArgumentationUnit(propNode.getAttribute("identifier"), propNode.getAttribute('missing'),
                                                                 refutation, proptextNode.getAttribute("offset"),
                                                                 proptextNode.firstChild.nodeValue, schemeNode.getAttribute("scheme"),
                                                                 schemeNode.getAttribute("schid"), laList, caList)
                                
        def getPremises(self, argnode):
                laNodes = []
                caNodes = []
                
                childNodes = argnode.childNodes
                for node in childNodes:
                        if node.nodeName == 'LA':
                                laNodes.extend(self.getArgumentationUnits(node))
                        elif node.nodeName == 'CA':
                                caNodes.append(self.getArgumentationUnits(node))
                return laNodes, caNodes

#a = AMLParser("F:\\proiecte\\NLP\\araucaria-aml-files\\arg_1.aml")
#u = a.getArgumentationUnits()[0]
#print u.extractText()
