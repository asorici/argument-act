
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
        return repr(self)

    def addArgument(self, argument):
        self.argList.append(argument)

    
