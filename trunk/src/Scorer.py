class Scorer:
    " " " Represents a general scorer for binary classifiers " " "

    def __init__(self):
        self.basicMeasures = None
        self.prMeasures = None
        self.f1score = None
        
    def computeFScores(self, targetLabels, actualLabels):
        if self.prMeasures is None:
            self.prMeasures = self.computePRMeasures(targetLabels, actualLabels)
        self.f1score = 2 * self.prMeasures[0] * self.prMeasures[1] /
                (self.prMeasures[0] * self.prMeasures[1])
        return self.f1score

    def computePRMeasures(self, targetLabels, actualLabels):
        if self.basicMeasures is None:
            self.basicMeasures = self.computeBasicStatistics(targetLabels, actualLabels)
        self.prMeasures = (self.basicMeasures[0] / (self.basicMeasures[0] + self.basicMeasures[1]),
                           self.basicMeasures[0] / (self.basicMeasures[0] + self.basicMeasures[3]))
        return self.prMeasures
    
    #returns true positive, false positive, true negative, false negative
    def computeBasicStatistics
        self.basicMeasures = reduce(self._cbe, map(lambda x,y:(x,y), targetLabels,
                                     actualLabels), (0,0,0,0))
        return self.basicMeasures

    def _cbe(self, value, pair):
        if pair[0] == "y":
            if pair[1] == "y": #tp
                return (1 + value[0], value[1], value[2], value[3])
            else : #fn
                return (value[0], value[1], value[2], value[3] + 1)
        elif pair[1] == "y": #fp
            return (value[0], 1 + value[1], value[2], value[3])
        else: #tn
            return (value[0], value[1], 1 + value[2], value[3])
