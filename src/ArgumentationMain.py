from BasicArgumentClassifier import BasicArgumentClassifier
from BasicArgumentSegmentation import BasicArgumentSegmentation
from ArgumentPropositionsClassifier import ArgumentPropositionsClassifier

class ArgumentationAct:
	" " " Main class of the project " " "
	def __init__(self):
		self.classifierUnit = BasicArgumentClassifier()
		self.segmentationUnit = BasicArgumentSegmentation()
		self.argPropositionsUnit = ArgumentPropositionsClassifier()

	#should return an argumentation unit -either a schema or a arg unit
	def parseText(self, text):
		pass
		
	def parseFromAml(self, start, stop):
		data = self.classifierUnit.classifyFiles(start, stop)
		textSegments = self.segmentationUnit.segment(data)
		
		print textSegments
		
aa = ArgumentationAct()
aa.parseFromAml(475,483)