import AMLParser
import WordSenseDisambiguation
import time, subprocess
from nltk import data
from nltk.tokenize import punkt
from nltk.tokenize.punkt import PunktWordTokenizer
from nltk.tokenize.regexp import WordPunctTokenizer, RegexpTokenizer
from nltk.corpus import stopwords, wordnet, wordnet_ic
from nltk.stem.porter import PorterStemmer
from numpy import *
from cluster import *

class BasicArgumentSegmentation:
	
	def __init__(self, sentencelist):
		self.sentenceList = sentencelist
		self.brown_ic = wordnet_ic.ic('ic-brown.dat')
		self.semcor_ic = wordnet_ic.ic('ic-semcor.dat')
		
	def segment(self):
		similarityMatrix = self.sentenceSimilarity()
		self.printSimilarityMatrix(similarityMatrix)
		
		n, m = similarityMatrix.shape
		self.distanceMatrix = zeros((n,m))
		
		for i in range(n):
			for j in range(m):
				self.distanceMatrix[i][j] = 1 - similarityMatrix[i][j]
		
		data = range(len(self.sentenceList))
		distanceFunction = lambda i, j: self.distanceMatrix[i][j]
		
		clusterer = HierarchicalClustering(data, distanceFunction, 'uclus')
		clusters = clusterer.getlevel(0.875)
		
		return clusters
	
	def printSimilarityMatrix(self, matrix):
		n, m = matrix.shape
		
		for i in range(n):
			for j in range(m):
				print "%4.2f " % matrix[i][j],
				
			print ""
	
	def sentenceSimilarity(self):
		wordLists = self._getWordLists()
		
		size = len(wordLists)
		similarityMatrix = ones((size, size))
		
		for i in range(len(wordLists)):
			for j in range(i + 1, len(wordLists)):
				sim = self._getSentenceSim(i, j, wordLists)
				similarityMatrix[i][j] = sim
				similarityMatrix[j][i] = sim
		
		return similarityMatrix
	
	def _getWordLists(self):
		
		# tokenize sentences
		#wordLists = map(lambda s: WordPunctTokenizer().tokenize(s), self.sentenceList)
		#wordLists = map(lambda s: PunktWordTokenizer().tokenize(s), self.sentenceList)
		wordLists = map(lambda s: RegexpTokenizer("\w+").tokenize(s), self.sentenceList)
		
		# remove stopwords
		stopWords = stopwords.words('english')
		wordLists = map(lambda wlist: filter(lambda w: w not in stopWords, wlist), wordLists)
		
		# use stemmer
		#stemmer = PorterStemmer()
		#wordLists = map(lambda wlist: map(lambda w: stemmer.stem(w), wlist), wordLists)
		
		return wordLists
	
	def _disambiguate(self, wordLists):
		wordSenseLists = []
		
		for wList in wordLists:
			nrWords = len(wList)
			
			# call perl disambiguation script and get result
			arg_list = ["./sense-relate-test.pl"] + wList
			pipe = subprocess.Popen(arg_list, stdout=subprocess.PIPE)
			result = pipe.stdout.read()
			res_list = result.split(" ")
			
			senseDict = {}
			for i in range(nrWords):
				ws = res_list[i]
				ws_args = ws.split("#")
				word = wList[i]
				
				if len(ws_args) < 3:
					senseDict[word] = None
				else:
					wn_sense = ws.replace("#", ".")
					senseDict[word] = wordnet.synset(wn_sense)
			
			wordSenseLists.append(senseDict)
		
		return wordSenseLists
	
	def _getSentenceSim(self, i, j, wordLists):
		X = wordLists[i]
		Y = wordLists[j]
		
		R = self._getWordSimilarityMatrix(X, Y)
		
		sizeX = len(X)
		sizeY = len(Y)
		sumX = 0
		sumY = 0
		
		for w1 in range(sizeX):
			max_w1 = 0
			
			for w2 in range(sizeY):
				if R[w1][w2] > max_w1:
					max_w1 = R[w1][w2]
			
			sumX += max_w1
			
		for w2 in range(sizeY):
			max_w2 = 0
			
			for w1 in range(sizeX):
				if R[w1][w2] > max_w2:
					max_w2 = R[w1][w2]
			
			sumY += max_w2

		return (sumX + sumY) / (2 * (sizeX + sizeY))
		
	def _getWordSimilarityMatrix(self, X, Y):
		sizeX = len(X)
		sizeY = len(Y)
		
		R = zeros( (sizeX, sizeY) )
		
		for i in range(sizeX):
			wi = X[i]
			
			for j in range(sizeY):
				wj = Y[j]
				
				# first check to see if words are the same
				if wi == wj:
					R[i][j] = 1
				else:
					synsets_i = wordnet.synsets(wi)
					synsets_j = wordnet.synsets(wj)
					
					if synsets_i and synsets_j:
						sense_i = synsets_i[0]
						sense_j = synsets_j[0]
						
						try:
							R[i][j] = sense_i.lin_similarity(sense_j, self.semcor_ic)
							#R[i][j] = sense_i.lch_similarity(sense_j)
							if R[i][j] > 1 or R[i][j] < 0.01:
								R[i][j] = 0
							#if R[i][j] < 0:
							#	R[i][j] = 0
						except:
							R[i][j] = 0
					else:
						R[i][j] = 0
		return R
		

amlFile = "../araucaria-aml-files/arg_128.aml"
parser = AMLParser.AMLParser(amlFile)
text = parser.getText()
sentenceTokenizer = data.LazyLoader("tokenizers/punkt/english.pickle")
sentenceList = sentenceTokenizer.tokenize(text)

argSeg = BasicArgumentSegmentation(sentenceList)
wordLists = argSeg._getWordLists()

print len(wordLists)
for wList in wordLists:
	print wList

"""
sim = argSeg._getWordSimilarityMatrix(wordLists[1], wordLists[2])
n,m = sim.shape

for i in range(n):
	for j in range(m):
		print "%4.2f " % sim[i][j],
		
	print ""
"""

"""
t0 = time.clock()
similarityMatrix = argSeg.sentenceSimilarity()
print time.clock() - t0, "seconds process time"
	
size = len(wordLists)
for i in range(size):
	for j in range(size):
		print "%4.2f " % similarityMatrix[i][j],
	
	print ""
"""

clusters = argSeg.segment()
print clusters

