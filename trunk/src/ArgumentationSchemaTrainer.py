import AMLParser

" " " Provieds a training set for " " "
class ArgumentationSchemaTrainer:

    def __init__(self, dictFile = "..\\resources\\walton.scm"):
        parser = AMLParser.AMLParser(dictFile)
        schemaList = map(lambda x:x.name, parser.getAllSchema())
        print schemaList

a = ArgumentationSchemaTrainer()
