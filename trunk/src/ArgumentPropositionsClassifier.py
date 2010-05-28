from svmutil import *

class ArgumentPropositionsClassifier:
    def __init__(self, model_file_path="arg-svm.m"):
        self.m = svm_load_model(model_file_path)
        print "*Model loaded into classifier"
    
    def _transform_test_data_into_problemxy(self,input_data):
        labels = input_data[1]
        values = input_data[0]
        new_values = map(lambda x1,x2,x3,x4,x5,x6: dict([(1,x1),(2,x2),(3,x3),(4,x4),(5,x5),(6,x6)]),values[0],values[1],values[2],values[3],values[4],values[5])
        return labels,new_values
    
    def predict(self, input_data):
        y, x = self._transform_test_data_into_problemxy(input_data)
        p_label, p_acc, p_val = svm_predict(y, x, self.m)
        print "*Prediction Done"    
        return p_label,p_acc, p_val          
    