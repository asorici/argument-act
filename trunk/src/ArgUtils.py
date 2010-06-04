import os.path

"""Provides various utilities for the project"""
class ArgUtils:

    def __init__(self, pathToAmlFiles = "..\\araucaria-aml-files\\"):
        self.pathToFiles = pathToAmlFiles
        
    """builds a list of aml files from start to stop"""
    def buildFileList(self, start, stop, even = None):
        name = self.pathToFiles + "arg_"
        if even:
            nameList = filter(self._isEven, range(start, stop))
        elif not even is None:
            nameList = filter(self._isOdd, range(start, stop))
        else:
            nameList = range(start, stop)
        return filter(os.path.exists, map(lambda x: name + str(x) + ".aml", nameList))

    def _isEven(self, x):
        if x % 2 == 0:
            return x
        else:
            return None

    def _isOdd(self, x):
        if x % 2 == 0:
            return None
        else:
            return x
