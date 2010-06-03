from nltk.tokenize import punkt
from nltk import data
import nltk.tag
import csv, tst, string, pickle
import BasicArgumentTrainer
import os.path
import Scorer

class BasicArgumentClassifier:
    
    def __init__(self, classifierFileName = "..\\resources\\basic-arg.pickle"):
        self.trainer = BasicArgumentTrainer.BasicArgumentTrainer()
        try:
            classifierFile = open(classifierFileName)
            self.classifier = pickle.load(classifierFile)
            classifierFile.close()
        except IOError:
            return None
        
    def batchClassify(self, featureList):
        return self.classifier.batch_classify(featureList)
        #call the decision component
        
    def classifyFiles(self, start, stop):
        testSet = self.trainer.buildTrainingExamples(start, stop, False)
        featureList = map(lambda x: x[0], testSet)
        actualLabels = map(lambda x: x[1], testSet)
        sentenceList = map(lambda x: x[2], testSet)
        sentenceIntervals = map(lambda x: x[3], testSet)
        
        displacement = 0
        index = 0
        for i in range(len(sentenceIntervals)):
            if i != 0 and sentenceIntervals[i][0] == 0:
                displacement += sentenceIntervals[i-1][1]
                if index == 0:
                    index = i
		
        for i in range(index, len(sentenceIntervals)):
            sentenceIntervals[i] = (sentenceIntervals[i][0] + displacement, sentenceIntervals[i][1] + displacement)
		
        textLength = sentenceIntervals[ len(sentenceIntervals) - 1 ][1]
        sentenceOffsets = map(lambda x: x[0], sentenceIntervals)
        classLabels = self.batchClassify(featureList)
        
        outputList = []
        for i in range(len(sentenceIntervals)):
            outputList.append((sentenceList[i], classLabels[i], sentenceOffsets[i]))
		
        #a = Scorer.Scorer()
        #print a.computeAccuracy(actualLabels, classLabels)
        return (outputList, textLength)

#a = BasicArgumentFeatureBuilder("..\\resources\\arg-dictionary.csv", True)
#print a.extractFeatures("For of the -  weather we would badly cancelled our trip.") #this is not true english just good test case :)
#a = BasicArgumentTrainer()
#a.trainClassifier(0,10)
#print a._buildTrainingExamples(0,20)
#print a.buildTrainingExample("F:\\proiecte\\NLP\\araucaria-aml-files\\arg_15.aml")

"""
a = BasicArgumentClassifier()
data = a.classifyFiles(475,483)
dataList, textLength = data

argSentences = map(lambda x: x[0], filter(lambda (x,y,z): y == 'y', dataList))
print argSentences
"""