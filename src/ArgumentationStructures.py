
class ArgumentationBasicSchema:
    " " " Represents a basic argumentation schema " " "

    def __init__(self, name, premiseList, conclusion):
        self.name = name
        self.premises = premiseList
        self.conclusion = conclusion

    def addPremise(self, premise):
        self.premises.append(premise)

    def __str__(self):
        return self.name + " - " + repr(self.premises) + " : " + self.conclusion

class ArgumentationUnit:
    " " "Defines an unit for argumentation " " "

    def __init__(self, argId, missing, offset, text, scheme, schId):
        self.id = argId
        self.missing = missing
        self.offset = offset
        self.text = text
        self.scheme = scheme
        self.schId = schId

    def __str__(self):
        return repr(self)

    
