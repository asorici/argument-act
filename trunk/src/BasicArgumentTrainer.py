from nltk import data
import nltk.tag
import csv, tst, string, pickle
import os.path
from nltk.tokenize import punkt
import BasicArgumentFeatureBuilder

class BasicArgumentTrainer:
    " " " Provieds a training set for the BasicArgumentClassifier " " "
    
    def __init__(self, pathToAmlFiles = "..\\araucaria-aml-files\\"):
        #this is the sentence tokenizer for english language
        self.sentenceTokenizer = data.LazyLoader("tokenizers\\punkt\\english.pickle")
        #this is the feature extractor
        self.featureExtractor = BasicArgumentFeatureBuilder.BasicArgumentFeatureBuilder(
            "..\\resources\\arg-dictionary.csv", False)
        self.text = None
        self.pathToFiles = pathToAmlFiles

    #builds a training set from all aml files with number between start and stop
    def buildTrainingExamples(self, start, stop):
        name = self.pathToFiles + "arg_"
        fileList = filter(os.path.exists, map(lambda x: name + str(x) + ".aml", range(start, stop)))
        return reduce(lambda x,y : x + y, map(self.buildTrainingExample, fileList))

    #trains a classifier of the specifified type and saves it in the given file
    def trainClassifier(self, start, stop, classifierType="naive-bayes",
                        fileName="..\\resources\\basic-arg.pickle"):
        trainingSet = self.buildTrainingExamples(start, stop)
        if classifierType == "naive-bayes":
            classifier = nltk.NaiveBayesClassifier.train(trainingSet)
        elif classifierType == "maxent":
            algortihm = "IIS" #this is hardcoded for now. All algorithm should yeld the same results
            classifier =  nltk.MaxentClassifier.train(trainingSet, algorithm, trace=0, max_iter=1000)
        else :
            return None
        try :
            f = open(fileName, "w")
            pickle.dump(classifier, f)
            f.close()
        except IOError:
            return None
        return True
        
    #builds a training set from an aml file
    def buildTrainingExample(self, amlFile):
        parser = AMLParser.AMLParser(amlFile)
        self.text = parser.getText()
        sentenceList = self.sentenceTokenizer.tokenize(self.text)
        featureList = map(self.featureExtractor.extractFeatures, sentenceList)
        argUnit = parser.getArgumentationUnits()[0]
        argIntervals = sorted(map(lambda x:(x[1], x[1] + len(x[0])), argUnit.extractTextAndOffset()), key=lambda x: x[0])
        senIntervals = map(self._mapToInterval, sentenceList)
        labels = self._generateLabels(argIntervals, senIntervals)
        return map(lambda x,y:(x,y), featureList, labels)   

    def _generateLabels(self, argIntervals, senIntervals):
        i = 0
        result = ["n"] * len(senIntervals)
        #skip the intervals for missing
        while argIntervals[i][0] < 0:
            i = i + 1
        for j in range(len(senIntervals)):
            #skip first sentences if non argumentative
            if senIntervals[j][1] < argIntervals[i][0]:
                continue
            while i < len(argIntervals) and not self._intervalIntersect(argIntervals[i], senIntervals[j]):
                i = i + 1
            if i < len(argIntervals):
                result[j] = "y"
            else:
                #rest of the sentences remain non argumentative
                break
        return result

    def _intervalIntersect(self, a, b):
        if a[1] < b[0] or b[1] < a[0] :
            return False
        else:
            return True
            
    def _buildInterval(self, pair):
        #return (pair[1], pair[1] + len(pair[0]
        return None
        
    def _mapToInterval(self, sentence):
        pi = self.text.find(sentence)
        return (pi, pi + len(sentence))

a = BasicArgumentTrainer()
a.trainClassifier(0,10)
