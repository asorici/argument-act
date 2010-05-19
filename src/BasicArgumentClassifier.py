from nltk.tokenize import punkt
from nltk import data
import nltk.tag
import csv, tst, string, pickle
import BasicArgumentTrainer
import os.path

class BasicArgumentClassifier:
    
    def __init__(self, classifierFileName = "..\\resources\\basic-arg.pickle"):
        self.t = BasicArgumentTrainer()
        try:
            classifierFile = open(classifierFileName)
            classifier = pickle.load(classifierFile)
        except IOError:
            return None
        
    def batchClassify(self, featureList):
        classifier.batch_classify(featureList)
        #call the decision component
        return None
        
    def classifyFiles(self, start, stop):
        testSet = self.buildTrainingExamples(start, stop, False)
        featureList = map(lambda x: x[0], testSet)
        self.batchClassify(featureList)
        return None

#a = BasicArgumentFeatureBuilder("..\\resources\\arg-dictionary.csv", True)
#print a.extractFeatures("For of the -  weather we would badly cancelled our trip.") #this is not true english just good test case :)
#a = BasicArgumentTrainer()
#a.trainClassifier(0,10)
#print a._buildTrainingExamples(0,20)
#print a.buildTrainingExample("F:\\proiecte\\NLP\\araucaria-aml-files\\arg_15.aml")
