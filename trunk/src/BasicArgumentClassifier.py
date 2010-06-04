from nltk.tokenize import punkt
from nltk import data
import nltk.tag
import csv, tst, string, pickle
import BasicArgumentTrainer, BasicArgumentFeatureBuilder, ArgUtils, AMLParser
import os.path
import Scorer

class BasicArgumentClassifier:
    
    def __init__(self, classifierFileName = "..\\resources\\basic-arg.pickle"):
        self.trainer = BasicArgumentTrainer.BasicArgumentTrainer()
        self.featureExtractor = BasicArgumentFeatureBuilder.BasicArgumentFeatureBuilder()
        try:
            classifierFile = open(classifierFileName)
            self.classifier = pickle.load(classifierFile)
            classifierFile.close()
        except IOError:
            return None

    def _testCalssifier(self, start, stop):
        estSet = self.trainer.buildTrainingExamples(start, stop, False)
        featureList = map(lambda x: x[0], testSet)
        actualLabels = map(lambda x: x[1], testSet)
        classLabels = self.classifier.batch_classify(featureList)
        a = Scorer.Scorer()
        print a.computeAccuracy(actualLabels, classLabels)
        print a.computeFScore(actualLabels, classLabels)
        print a.prMeasures
        
    def classifyFiles(self, start, stop):
        u = ArgUtils.ArgUtils()
        #true represents training set
        fileList = u.buildFileList(start, stop, False)
        return reduce(lambda x,y : x + y, map(self.classifyAmlFile, fileList))

    def classifyAmlFile(self, amlFile):
        parser = AMLParser.AMLParser(amlFile)
        text = parser.getText()
        a = self.classifyText(text)
        print a
        print "ooooo\n"
        return a
        #return self.classifyText(text)
    
    def classifyText(self, text):
        sentenceList = self.trainer.sentenceTokenizer.tokenize(text)
        featureList = map(self.trainer.featureExtractor.extractFeatures, sentenceList)
        labels = self.classifier.batch_classify(featureList)
        return map(lambda x,y,z:(x,y,z), sentenceList,labels, featureList)

a = BasicArgumentClassifier("..\\resources\\max-arg.pickle")
#a = BasicArgumentClassifier()
#a.classifyFiles(0,1000)
a.classifyFiles(0,3)
