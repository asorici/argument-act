import AMLParser
import os.path

class ArgumentPropositionsTrainer:
    """Provides a training set for the ArgumentPropositionsClassifier"""

    def __init__(self, path_to_aml_files = "..\\araucaria-aml-files\\", even=None):
        self.training_set = None
        self.even = even
        self.path_to_amls = path_to_aml_files
        
    def get_training_set(self):
        if self.training_set is None:
            self.training_set = self.build_training_set(0,2,self.even)
        return self.training_set
        
    def build_training_set(self, start, stop, even=None):
        name = self.path_to_amls + "arg_"
        if even:
            name_list = filter(self._is_even, range(start, stop))
        elif not even is None:
            name_list = filter(self._is_odd, range(start, stop))
        else:
            name_list = range(start, stop)
        file_list = filter(os.path.exists, map(lambda x: name + str(x) + ".aml", name_list))
        return reduce(lambda x,y : x + y, map(self.extract_examples_from_aml, file_list))
    
    def extract_examples_from_aml(self, aml_file):
        aml_parser = AMLParser.AMLParser(aml_file)
        root = aml_parser.getArgumentationUnits()[0]
        premises, conclusions = root.get_text_units()
        print premises
        print conclusions
        featureList = []
        labels = []
        return map(lambda x,y:(x,y), featureList, labels)
        
    def _is_even(self, x):
        if x % 2 == 0:
            return x
        else:
            return None

    def _is_odd(self, x):
        if x % 2 == 0:
            return None
        else:
            return x

a = ArgumentPropositionsTrainer(even=False)
a.get_training_set()