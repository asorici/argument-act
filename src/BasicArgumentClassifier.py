from nltk.tokenize import punkt
from nltk import data
import nltk.tag
import csv, tst, string, pickle
from .. import AMLParser
import os.path

class BasicArgumentClassifier:

    def __init__(self):
        #this is the sentence tokenizer for english language
        self.sentenceTokenizer = data.LazyLoader("tokenizers/punkt/english.pickle")
        #this is the feature extractor
        self.featureExtractor = BasicArgumentFeatureBuilder("..\\resources\\arg-dictionary.csv", False)
        
    def getArgumentativeSentences(self, text):
        sentenceList = self.sentenceTokenizer.tokenize(text)

    def train(label_probdist):
        return None

    def classify(featureset):
        return None

#a = BasicArgumentFeatureBuilder("..\\resources\\arg-dictionary.csv", True)
#print a.extractFeatures("For of the -  weather we would badly cancelled our trip.") #this is not true english just good test case :)
a = BasicArgumentTrainer()
a.trainClassifier(0,10)
#print a._buildTrainingExamples(0,20)
#print a.buildTrainingExample("F:\\proiecte\\NLP\\araucaria-aml-files\\arg_15.aml")
