from nltk.tokenize import punkt
import tst, string
import nltk.tag

class BasicArgumentFeatureBuilder:
    " " " Extracts features from a sentence. Depends on SWIGPy and requires tst module " " "
    
    def __init__(self, dictPath = "..\\resources\\arg-dictionary.csv", buildDict = False):
        self.wordTokenizer = punkt.PunktWordTokenizer()
        pos = dictPath.rfind(".")
        if pos < 0:
            pos = len(dictPath)
        dictOutFile = ''.join([dictPath[:pos], ".tst"])
        if buildDict:
            self.buildTSTDictionaryFromFile(dictPath, dictOutFile)
        self.loadTSTDictionary(dictOutFile)
            
    #loads a TST dictionary from file. This dictionary is SWIGPy and can't be pickled
    def loadTSTDictionary(self, fileName):
        self.dict = tst.TST()
        try:
            self.dict.read_from_file(fileName)
        except IOError:
            self.dict = None

    #builds a TST dictionary from a CSV file and stores it in another file
    def buildTSTDictionaryFromFile(self, inFileName, outFileName = "..\\resources\\arg-dictionary.pickle"):
        try:
            f = open(inFileName)
            reader = csv.reader(f)
            keyWordDict = []
            for row in reader:
                keyWordDict.extend(row)
            f.close()
            if self.buildTSTDictionary(keyWordDict, outFileName):
                return True
            else:
                return False
        except IOError:
            return False

    #builds a TST dictionary from a word list and stores it in a file
    def buildTSTDictionary(self, wordList, saveFile = "..\\resources\\code\\arg-dictionary.tst"):
        t = tst.TST()
        for word in wordList:
            t[word] = 1
        try:
            t.write_to_file(saveFile)
            return True
        except IOError :
            return False

    
    #extracts the features from the text and returns a dictionary
    def extractFeatures(self, sentence):
        tokens = self.wordTokenizer.tokenize(sentence)
        tagList = nltk.tag.pos_tag(tokens)
        result = dict(modalVerb = 0, verb = 0, adverb = 0, punct = 0, key = 0)
        for (word,tag) in tagList:
            if tag == "MD":
                result["modalVerb"] = 1
            elif tag.startswith("VB") and self._isMainVerb(word):
                result["verb"] = 1
            elif tag.startswith("RB"):
                result["adverb"] = 1
            elif word in string.punctuation:
                result["punct"] = 1 + result["punct"]
        if self._containsKeyWord(sentence.lower()):
            result["key"] = 1
        return result

    def _isMainVerb(self, word):
        if (word == "be") or (word == "have") or (word == "do"):
            return 0
        else:
            return 1

    #this uses Aho-Corasick algorithm and performs a match in linear time in text length
    def _containsKeyWord(self, sentence):
        fun = BasicArgumentCallbackFunction()
        try:
            return self.dict.scan(sentence, tst.CallableAction(fun.hit, fun.result))
        except RuntimeError:
            return 1

class BasicArgumentCallbackFunction(object):
    " " " represents a callback interface for the TST scaner " " "

    #this explicitly raises an exception to force the caller to terminate. This is done to speed up the Aho-Corasick since
    #we're only interested in the first match of a given word.
    #TODO : Implement own exception just to be safe
    def hit(self, key, length, obj):
     	if length > 0 :
     	    raise RuntimeError
     	    
    def result(self):
        #if no exception was thrown then there is no match
        return 0
