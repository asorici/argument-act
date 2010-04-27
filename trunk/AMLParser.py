from xml.dom import minidom

class AMLParser:
	"""
	parse aml file for argument text and its structure
	"""
	
	def __init__ (self):
		self.argdoc = None
	
	
	def parseFile(self, filename):
		""" returns a dictionary containing the argument text, the top conclusion and supporting premises. """
		
		try:
			self.argdoc = minidom.parse(filename)
			text = self.getArgumentText()
			conclusion = self.getTopConclusion()
			premiseList = self.getPremiseList()
			
			return {"text" : text, "conclusion" : conclusion, "premiseList" : premiseList}
		except:
			self.argdoc = None
			raise
	
	def getArgumentText(self):
		if self.argdoc == None:
			return None
		else:
			try:
				textNode = self.argdoc.getElementsByTagName('TEXT')[0];
				return textNode.firstChild.nodeValue;
			except:
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