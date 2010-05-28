import sys
import ArgumentPropositionsTrainer
from svmutil import *
import ArgumentPropositionsClassifier


if sys.platform == 'win32':
    path_to_araucaria = "..\\araucaria-aml-files\\"
    path_to_features = "..\\resources\\features_premise_vs_conclusion\\"
else:
    path_to_araucaria = "../araucaria-aml-files/"
    path_to_features = "../resources/features_premise_vs_conclusion/"

#Antrenare
print "*Starting..."
a = ArgumentPropositionsTrainer.ArgumentPropositionsTrainer(path_to_aml_files = path_to_araucaria, path_to_features_files = path_to_features, even=False)
a.get_training_set()
test_data = a.get_test_set()
a.train_classifier()
print "*Classifier model ready."
#Testare
print "*Testing..."
c = ArgumentPropositionsClassifier.ArgumentPropositionsClassifier()
p_label, p_acc, p_val = c.predict(test_data)
ACC, MSE, SCC = evaluations(test_data[1], p_label)
print ACC, MSE, SCC
print "*The End"