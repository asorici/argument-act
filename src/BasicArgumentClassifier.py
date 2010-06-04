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

    def _testClassifier(self, start, stop):
        testSet = self.trainer.buildTrainingExamples(start, stop, False)
        if testSet is None:
            return None
        featureList = map(lambda x: x[0], testSet)
        actualLabels = map(lambda x: x[1], testSet)
        classLabels = self.classifier.batch_classify(featureList)
        a = Scorer.Scorer()
        a.computeAccuracy(actualLabels, classLabels)
        a.computeFScores(actualLabels, classLabels)
        a.computeMathewsCoef(actualLabels, classLabels)
        return [a.accuracy, a.f1score, a.prMeasures[0], a.prMeasures[1]]
        
    def classifyFiles(self, start, stop):
        u = ArgUtils.ArgUtils()
        #true represents training set
        fileList = u.buildFileList(start, stop, False)
        return reduce(lambda x,y : x + y, map(self.classifyAmlFile, fileList))

    def classifyAmlFile(self, amlFile):
        parser = AMLParser.AMLParser(amlFile)
        text = parser.getText()
        return self.classifyText(text)
    
    def classifyText(self, text):
        sentenceList = self.trainer.sentenceTokenizer.tokenize(text)
        featureList = map(self.trainer.featureExtractor.extractFeatures, sentenceList)
        labels = self.classifier.batch_classify(featureList)
        return map(lambda x,y,z:(x,y,z), sentenceList,labels, featureList)

#a = BasicArgumentClassifier("..\\resources\\max-arg.pickle")
#f = file("..\\resources\\test.txt")
#print map(lambda x:(x[0],x[1]), a.classifyText(f.read()))
#f.close()

##a = BasicArgumentClassifier()
#a.classifyFiles(0,1000)
#a._testClassifier(487, 489)

#test script
#f = file("..\\resources\\basic-arg-small.csv", "w")
#f.write("accuracy, f1score, precision, recall\n")
#map(lambda x:f.write(x[1:len(x) - 1] + "\n"), map(lambda x:repr(x),
#                                                  filter(lambda x: not x is None,
#                                                         map(lambda x:a._testClassifier(x, x + 2), range(800, 1000)))))
#f.close()
