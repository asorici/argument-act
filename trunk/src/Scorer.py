class Scorer:
    " " " Represents a general scorer for binary classifiers " " "

    def __init__(self, y="y"):
        self.basicMeasures = None
        self.prMeasures = None
        self.f1score = None
        self.y = y
        
    def computeFScores(self, targetLabels, actualLabels):
        """computes the F1 score"""
        if self.prMeasures is None:
            self.prMeasures = self.computePRMeasures(targetLabels, actualLabels)
        self.f1score = 2 * self.prMeasures[0] * self.prMeasures[1] / (self.prMeasures[0] * self.prMeasures[1])
        return self.f1score

    def computePRMeasures(self, targetLabels, actualLabels):
        """ computes precision and recall measures"""
        if self.basicMeasures is None:
            self.basicMeasures = self.computeBasicStatistics(targetLabels, actualLabels)
        if self.basicMeasures[0] == 0:
            self.prMeasures = (0,0)
        else:
            self.prMeasures = ((0.0 + self.basicMeasures[0]) / (self.basicMeasures[0] + self.basicMeasures[1]),
                               (0.0 + self.basicMeasures[0]) / (self.basicMeasures[0] + self.basicMeasures[3]))
        return self.prMeasures
    
    #returns true positive, false positive, true negative, false negative
    def computeBasicStatistics(self, targetLabels, actualLabels):
        """computes the basic statistics"""
        self.basicMeasures = reduce(self._cbe, map(lambda x,y:(x,y), targetLabels,
                                     actualLabels), (0,0,0,0))
        return self.basicMeasures

    def computeAccuracy(self, targetLabels, actualLabels):
        """computes the accuracy based on target labels and actual labels"""
        return (0.0 + sum([1 for x in map(lambda y,z:(y,z), targetLabels, actualLabels) if x[0] == x[1]])) / len(targetLabels)

    def _equal1(self, x, y):
        if x == y:
            return 1
        else:
            return 0
    
    def _cbe(self, value, pair):
        if pair[0] == self.y:
            if pair[1] == self.y: #tp
                return (1 + value[0], value[1], value[2], value[3])
            else : #fn
                return (value[0], value[1], value[2], value[3] + 1)
        elif pair[1] == self.y: #fp
            return (value[0], 1 + value[1], value[2], value[3])
        else: #tn
            return (value[0], value[1], 1 + value[2], value[3])

