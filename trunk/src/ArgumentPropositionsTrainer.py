import AMLParser
import ArgumentationStructures
import string
from nltk.tokenize import punkt
from nltk.corpus import PlaintextCorpusReader
from nltk.text import Text
from nltk import word_tokenize
from nltk.probability import FreqDist, ConditionalFreqDist
from nltk import bigrams
from svmutil import *

import os.path

class ArgumentPropositionsTrainer:
    """Provides a training set for the ArgumentPropositionsClassifier"""

    def __init__(self, path_to_aml_files = "../araucaria-aml-files/", path_to_features_files = "../resources/features_premise_vs_conclusion/", even=None):
        self.training_set = None
        self.test_set = None
        self.even = even
        self.path_to_amls = path_to_aml_files
        self.path_to_features = path_to_features_files
        self.wordTokenizer = punkt.PunktWordTokenizer()
        self.factors = None
        #self.FILE_C = open("conclusions","w")
        #self.FILE_P = open("premises","w")
        
    def get_training_set(self):
        if self.training_set is None:
            self.training_set = self.build_training_set(0,1005,self.even)
            print "Training set loaded!"
            #self.FILE_C.close()
            #self.FILE_P.close()
        return self.training_set
    
    def get_test_set(self):
        if self.test_set is None:
            self.test_set = self.build_training_set(0, 1005, not self.even)
            print "Test set loaded!"
        return self.test_set
        
    def build_training_set(self, start, stop, even=None):
        name = self.path_to_amls + "arg_"
        if even:
            name_list = filter(self._is_even, range(start, stop))
        elif not even is None:
            name_list = filter(self._is_odd, range(start, stop))
        else:
            name_list = range(start, stop)
        file_list = filter(os.path.exists, map(lambda x: name + str(x) + ".aml", name_list))
        real_valued_features = reduce(lambda x,y : [[x[0][0]+y[0][0],x[0][1]+y[0][1],x[0][2]+y[0][2],x[0][3]+y[0][3],x[0][4]+y[0][4],x[0][5]+y[0][5]],x[1] + y[1]], map(self.extract_examples_from_aml, file_list), [[[],[],[],[],[],[]],[]])
        unscaled_values = real_valued_features[0]
        labels = real_valued_features[1]
        scaled_values = self._scale_data(unscaled_values)
        return [scaled_values, labels]
        
    
    def extract_examples_from_aml(self, aml_file):
        aml_parser = AMLParser.AMLParser(aml_file)
        root = aml_parser.getArgumentationUnits()[0]
        #print aml_file
        premises, conclusions = root.get_text_units(is_root=True)
        #self.FILE_C.writelines([c.conclusion + " " for c in conclusions])
        #self.FILE_P.writelines([c.conclusion + " " for c in premises])
        pv = self._extract_features(premises, len(aml_parser.getText()))
        cv = self._extract_features(conclusions, len(aml_parser.getText()))
        real_valued_features = [pv[0]+cv[0],pv[1]+cv[1],pv[2]+cv[2],pv[3]+cv[3],pv[4]+cv[4],pv[5]+cv[5]]
        labels = [1] * len(premises) + [0] * len(conclusions)
        return [real_valued_features, labels]
    
    def _is_even(self, x):
        if x % 2 == 0:
            return x
        else:
            return None

    def _is_odd(self, x):
        if x % 2 == 0:
            return None
        else:
            return x

    def _extract_features(self,propositions,text_length):
        """ Computes numerical values for the features used"""
        #1. Sentence length
        lengths = []
        for p in propositions:
            tokens = self.wordTokenizer.tokenize(p.conclusion)
            lengths.append(float(len(tokens)))
        #2. Relative position in document
        relative_positions = map(lambda p,l: float((int(p.offset) + len(p.conclusion)/2))/float(l), propositions, [text_length] * len(propositions))
        #3. Has some key word - conclusion
        fcw = word_tokenize(open(self.path_to_features + "conclusions_freq_words_manual").read())
        fcw_found = []
        for p in propositions:
            tokens = self.wordTokenizer.tokenize(p.conclusion)
            if len(set(tokens) & set(fcw)) > 0:
                fcw_found.append(1)
            else:
                fcw_found.append(-1.0)
        #4. Has some key word - premise
        fpw = word_tokenize(open(self.path_to_features + "premises_freq_words_manual").read())
        fpw_found = []
        for p in propositions:
            tokens = self.wordTokenizer.tokenize(p.conclusion)
            if len(set(tokens) & set(fpw)) > 0:
                fpw_found.append(1)
            else:
                fpw_found.append(-1.0)
        #5. Bigrams - premises
        fpb = open(self.path_to_features + "premises_freq_bigrams_manual").read()
        pbs = string.split(fpb,",")
        pbs2 = set(map(lambda x: string.strip(x), pbs))
        fpb_found = []
        for p in propositions:
            tokens = self.wordTokenizer.tokenize(p.conclusion)
            bigrams_ = set(map(lambda (x,y) : x + " " + y, bigrams(tokens)))
            #print bigrams_, "vs", pbs2
            if len(bigrams_ & pbs2) > 0:
                fpb_found.append(1)
            else:
                fpb_found.append(-1.0)

        
        #6. Bigrams - conclusions
        fcb = open(self.path_to_features + "conclusions_freq_bigrams_manual").read()
        cbs = string.split(fcb,",")
        cbs2 = set(map(lambda x: string.strip(x), cbs))
        fcb_found = []
        for p in propositions:
            tokens = self.wordTokenizer.tokenize(p.conclusion)
            bigrams_ = set(map(lambda (x,y) : x + " " + y, bigrams(tokens)))
            if len(bigrams_ & cbs2) > 0:
                fcb_found.append(1.0)
            else:
                fcb_found.append(-1.0)
                
        #print map(lambda x,y,z,t,u,v: (x,y,z,t,u,v),lengths,relative_positions,fcw_found,fpw_found,fpb_found,fcb_found)
        return [lengths,relative_positions,fcw_found,fpw_found,fpb_found,fcb_found]
    
    def train_classifier(self, model_file_path="arg-svm.m"):
        prob  = self._transform_test_data_into_problem(self.training_set)
        param = svm_parameter('-c 4 -b 1')
        m = svm_train(prob, param)
        svm_save_model(model_file_path, m)
        return None
    
    def _transform_test_data_into_problem(self,input_data):
        labels = input_data[1]
        values = input_data[0]
        new_values = map(lambda x1,x2,x3,x4,x5,x6: dict([(1,x1),(2,x2),(3,x3),(4,x4),(5,x5),(6,x6)]),values[0],values[1],values[2],values[3],values[4],values[5])
        return svm_problem(labels,new_values)
    
    def _scale_data(self,values):
        if self.factors == None:
            self.factors = map(lambda x: [min(x), (float(max(x))-float(min(x)))/2.0],values)
        new_values = map(lambda vector, parameters: map(lambda x, p: float(x-p[0])*p[1]-1.0, vector,[parameters] * len(vector)),values,self.factors)
        return new_values
        