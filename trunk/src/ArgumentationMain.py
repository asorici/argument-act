from BasicArgumentClassifier import BasicArgumentClassifier
from BasicArgumentSegmentation import BasicArgumentSegmentation
from ArgumentPropositionsClassifier import ArgumentPropositionsClassifier

class ArgumentationAct:
	" " " Main class of the project " " "
	def __init__(self):
		self.classifierUnit = BasicArgumentClassifier("..\\resources\\max-arg.pickle")
		self.segmentationUnit = BasicArgumentSegmentation()
		self.argPropositionsUnit = ArgumentPropositionsClassifier()

	#should return an argumentation unit -either a schema or a arg unit
	def parseText(self, text):
		data = self.classifierUnit.classifyText(text)
		textSegments = self.segmentationUnit.segment(data)
		classifiedTextSegments = self.argPropositionsUnit.predict2(textSegments)
		
		print classifiedTextSegments
		#print textSegments[len(textSegments) - 5]
		#list, length = textSegments[len(textSegments) - 5]
		#for sen in list:
			#print str(sen[0])
		
	def parseFromAml(self, start, stop):
		data = self.classifierUnit.classifyFiles(start, stop)
		textSegments = self.segmentationUnit.segment(data)
		classifiedTextSegments = self.argPropositionsUnit.predict2(textSegments)
		print classifiedTextSegments
		
aa = ArgumentationAct()
aa.parseFromAml(475,483)
#aa.parseFromAml(487,489)

#f = file("..\\resources\\test.txt")
#aa.parseText(f.read())
#f.close()
