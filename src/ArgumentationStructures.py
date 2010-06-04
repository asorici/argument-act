
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
    
    def __init__(self, argId, missing, refutation, offset, conclusion, schemes, schIds, laList, caList):
        self.id = argId
        self.missing = missing
        self.refutation = refutation
        self.offset = offset
        self.conclusion = conclusion
        self.schemes = schemes
        self.schIds = schIds
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
    
    def is_leaf(self):
        """ True if argument unit has empty ca & la lists """
        if len(self.caList) > 0 or len(self.laList) > 0:
            return False
        else:
            return True
    
    def get_text_units(self, arg_unit=None, is_root=True, parent_offset=0):
        """ Builds two lists (premises and conclusions) from the current node's subtree """
        if arg_unit is None:
            arg_unit = self
        if (arg_unit.is_leaf() == True):
            if arg_unit.missing == "yes":
                return([],[])
            else:
                if (is_root == True):
                    return ([],[arg_unit])
                else:
                    return ([arg_unit],[])
        else:
            if arg_unit.missing == "yes":
                p = c = []
            else:
                p1 = []
                c1 = [arg_unit]
                p2, c2 = reduce(lambda (x1, y1),(x2, y2): (x1 + x2, y1 + y2), map(self.get_text_units, arg_unit.laList, [False]*len(arg_unit.laList), [arg_unit.offset] * len(arg_unit.laList)),(p1,c1))
                #p, c = reduce(lambda (x1, y1),(x2, y2): (x1 + x2, y1 + y2), map(self.get_text_units, arg_unit.caList, [False]*len(arg_unit.caList), [arg_unit.offset] * len(arg_unit.caList)),(p2,c2))
                for ca_inner_list in arg_unit.caList:
                    p2, c2 = reduce(lambda (x1, y1),(x2, y2): (x1 + x2, y1 + y2), map(self.get_text_units, ca_inner_list, [False]*len(ca_inner_list), [arg_unit.offset] * len(ca_inner_list)),(p2,c2))
                #print reduce(lambda (x1, y1),(x2, y2): (x1 + x2, y1 + y2), map(self.get_text_units, arg_unit.caList, [False]*len(arg_unit.caList), [arg_unit.offset] * len(arg_unit.caList)),([],[]))
                p,c = p2,c2
            return p, c
