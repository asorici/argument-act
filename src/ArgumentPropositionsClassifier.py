from nltk.tokenize import punkt
from nltk.corpus import PlaintextCorpusReader
from nltk.text import Text
from nltk import word_tokenize
from nltk.probability import FreqDist, ConditionalFreqDist
from nltk import bigrams
from svmutil import *
import string

class ArgumentPropositionsClassifier:
    def __init__(self, model_file_path="arg-svm.m", path_to_features_files = "../resources/features_premise_vs_conclusion/"):
        self.m = svm_load_model(model_file_path)
        self.wordTokenizer = punkt.PunktWordTokenizer()
        self.path_to_features = path_to_features_files
        self.factors = None
        print "*Model loaded into classifier"
    
    def _transform_test_data_into_problemxy(self,input_data):
        labels = input_data[1]
        values = input_data[0]
        new_values = map(lambda x1,x2,x3,x4,x5,x6,x7,x8: dict([(1,x1),(2,x2),(3,x3),(4,x4),(5,x5),(6,x6)]),values[0],values[1],values[2],values[3],values[4],values[5],values[6],values[7])
        return labels,new_values
    
    def predict(self, input_data):
        y, x = self._transform_test_data_into_problemxy(input_data)
        p_label, p_acc, p_val = svm_predict(y, x, self.m)
        print "*Prediction Done"    
        return p_label,p_acc, p_val      
    
    
    def _extract_features(self,p,l):
        """ Computes numerical values for the features used"""
        #1. Sentence length
        lengths = []
        tokens = self.wordTokenizer.tokenize(p[0])
        lengths.append(float(len(tokens)))
        #2. Relative position in document
        relative_positions = [float((int(p[1]) + len(p[0])/2))/float(l)]
        #3. Has some key word - conclusion
        fcw = word_tokenize(open(self.path_to_features + "conclusions_freq_words_raw").read())
        fcw_found = []
        tokens = self.wordTokenizer.tokenize(p[0])
        if len(set(tokens) & set(fcw)) > 0:
            fcw_found.append(1)
        else:
            fcw_found.append(-1.0)
        #4. Has some key word - premise
        fpw = word_tokenize(open(self.path_to_features + "premises_freq_words_raw").read())
        fpw_found = []
        tokens = self.wordTokenizer.tokenize(p[0])
        if len(set(tokens) & set(fpw)) > 0:
            fpw_found.append(-1.0)
        else:
            fpw_found.append(1.0)
        #5. Bigrams - premises
        fpb = open(self.path_to_features + "premises_freq_bigrams_raw").read()
        pbs = string.split(fpb,",")
        pbs2 = set(map(lambda x: string.strip(x), pbs))
        fpb_found = []
        
        tokens = self.wordTokenizer.tokenize(p[0])
        bigrams_ = set(map(lambda (x,y) : x + " " + y, bigrams(tokens)))
        #print bigrams_, "vs", pbs2
        if len(bigrams_ & pbs2) > 0:
            fpb_found.append(-1.0)
        else:
            fpb_found.append(1.0)
           
        #6. Bigrams - conclusions
        fcb = open(self.path_to_features + "conclusions_freq_bigrams_raw").read()
        cbs = string.split(fcb,",")
        cbs2 = set(map(lambda x: string.strip(x), cbs))
        fcb_found = []
        tokens = self.wordTokenizer.tokenize(p[0])
        bigrams_ = set(map(lambda (x,y) : x + " " + y, bigrams(tokens)))
        if len(bigrams_ & cbs2) > 0:
            fcb_found.append(1.0)
        else:
            fcb_found.append(-1.0)
                
        #7. Has some key word - conclusion
        ecw = word_tokenize(open(self.path_to_features + "conclusions_exclusive_words_raw").read())
        ecw_found = []
        tokens = self.wordTokenizer.tokenize(p[0])
        if len(set(tokens) & set(ecw)) > 0:
            ecw_found.append(1)
        else:
            ecw_found.append(-1.0)
        #8. Has some key word - premise
        epw = word_tokenize(open(self.path_to_features + "premises_exclusive_words_raw").read())
        epw_found = []
        tokens = self.wordTokenizer.tokenize(p[0])
        if len(set(tokens) & set(epw)) > 0:
            epw_found.append(1)
        else:
            epw_found.append(-1.0)
        #print map(lambda x,y,z,t,u,v: (x,y,z,t,u,v),lengths,relative_positions,fcw_found,fpw_found,fpb_found,fcb_found)
        return [lengths,relative_positions,fcw_found,fpw_found,fpb_found,fcb_found,ecw_found,epw_found]
    
    def _extract_examples_from_cluster(self, cluster):
        sentences = cluster[0]
        length = [cluster[1]] * len(sentences)
        unscaled_values = reduce(lambda x,y : [x[0]+y[0],x[1]+y[1],x[2]+y[2],x[3]+y[3],x[4]+y[4],x[5]+y[5],x[6]+y[6],x[7]+y[7]], map(self._extract_features, sentences, length), [[],[],[],[],[],[],[],[]])
        scaled_values = self._scale_data(unscaled_values)
        print scaled_values
        new_values = map(lambda x1,x2,x3,x4,x5,x6,x7,x8: dict([(1,x1),(2,x2),(3,x3),(4,x4),(5,x5),(6,x6)]),scaled_values[0],scaled_values[1],scaled_values[2],scaled_values[3],scaled_values[4],scaled_values[5],scaled_values[6],scaled_values[7])
        p_label, p_acc, p_val = svm_predict([], new_values, self.m)
        return map(lambda s,l : (s[0],l), sentences, p_label)
    
    
    def _scale_data(self,values):
        if self.factors == None:
            FILE_FACTORS = open("factors", "r")
            self.factors = []
            for i in range(0,8):
                line = FILE_FACTORS.readline()
                vals = line.replace("\n","").split()
                self.factors.append(map(lambda x:float(x),vals))
            print self.factors
            FILE_FACTORS.close()
            
        new_values = map(lambda vector, parameters: map(lambda x, p: float(x-p[0])*p[1]-1.0, vector,[parameters] * len(vector)),values,self.factors)
        return new_values
    
    def _extract_from(self,segmentation_output):
        r = map(lambda s : self._extract_examples_from_cluster(s), segmentation_output)
        return r
    
    def predict2(self, segmentation_output):
        r = self._extract_from(segmentation_output)
        print "*Prediction Done"    
        return r 
    