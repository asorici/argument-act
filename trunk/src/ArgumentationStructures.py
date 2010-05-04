
class ArgumentationBasicSchema:
    " " " Represents a basic argumentation schema " " "

    def __init__(self, name, premiseList, conclusion, criticalQuestions):
        self.name = name
        self.premises = premiseList
        self.conclusion = conclusion
        self.criticalQuestions = criticalQuestions

    def addPremise(self, premise):
        self.premises.append(premise)

    def __str__(self):
        return self.name + " - " + repr(self.premises) + " : " + self.conclusion

class ArgumentationUnit:
    " " "Defines an unit for argumentation " " "
    
    def __init__(self, argId, missing, refutation, offset, conclusion, scheme, schId, laList, caList):
        self.id = argId
        self.missing = missing
        self.refutation = refutation
        self.offset = offset
        self.conclusion = conclusion
        self.scheme = scheme
        self.schId = schId
        self.laList = laList
        self.caList = caList

    def __str__(self):
        return self.id

    def addArgument(self, argument):
        self.argList.append(argument)

    #recursively extracts all text from this argumentation unit(including the missing ones).
    def extractText(self, argUnit=None):
        if argUnit is None:
            argUnit = self
        result = [argUnit.conclusion]
        la = [entry[0] for entry in map(self.extractText, argUnit.laList)]
        ca = [entry[0] for entry in map(self.extractText, argUnit.caList)]
        result.extend(la)
        result.extend(ca)
        return result

    def extractTextAndOffset(self, argUnit=None):
        if argUnit is None:
            argUnit = self
        result = [(argUnit.conclusion, int(argUnit.offset))]
        if not self.refutation is None:
            result.extend(self.refutation.extractTextAndOffset())
        la = reduce(lambda x,y: x + y, map(self.extractTextAndOffset, argUnit.laList), [])
        ca = reduce(lambda x,y: x + y, map(self.extractTextAndOffset, argUnit.caList), [])
        result.extend(la)
        result.extend(ca)
        return result
