
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
        la = reduce(lambda x,y: x + y, map(self.extractText, argUnit.laList), [])
        ca = reduce(lambda x,y: x + y, map(self.extractText, reduce(lambda x,y: x + y, argUnit.caList, [])), [])
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
        ca = reduce(lambda x,y: x + y, map(self.extractTextAndOffset, reduce(lambda x,y: x + y, argUnit.caList, [])), [])
        result.extend(la)
        result.extend(ca)
        return result
    
    def get_text_units(self, arg_unit=None):
        if arg_unit is None:
            arg_unit = self
        print arg_unit.conclusion
        print [arg.conclusion for arg in arg_unit.laList]
        c = []
        p = []
        if len(arg_unit.laList) == 0 and len(arg_unit.caList) == 0:
            p.append(arg_unit.conclusion)
        else:
            c.append(arg_unit.conclusion)
        p1, c1 = reduce(lambda (x1, y1),(x2, y2): (x1 + x2, y1 + y2), map(self.get_text_units, arg_unit.laList),(p,c))
        p2, c2 = reduce(lambda (x1, y1),(x2, y2): (x1 + x2, y1 + y2), map(self.get_text_units, arg_unit.caList),(p1,c1))
        return p2, c2
