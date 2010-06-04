import AMLParser

" " " Provieds a training set for " " "
class ArgumentationSchemaTrainer:

    def __init__(self, dictFile="..\\resources\\walton.scm"):
        parser = AMLParser.AMLParser(dictFile)
        self.schemaList = map(lambda x:x.name, parser.getAllSchema())
        #print schemaList

    def buildTrainingExample(self, amlFile):
        parser = AMLParser.AMLParser(amlFile)
        argUnit = parser.getArgumentationUnits()
        print argUnit[0].schemes

a = ArgumentationSchemaTrainer()
a.buildTrainingExample("..\\araucaria-aml-files\\arg_123.aml")
