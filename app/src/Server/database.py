import os
import pandas as pd
import numpy as np

INDEX_COLUMN = "id"
NAME_COLUMN = "name"
HASH_COLUMN = "hash"
IP_COLUMN = "ip"

class BaseError(RuntimeError): pass

class Base:
    df : pd.DataFrame = None

    @staticmethod
    def __get_base_path():
        return os.path.join("logs", f"base.csv")
    
    @staticmethod
    def __make_keys():
        keys = [
            NAME_COLUMN,
            HASH_COLUMN,
            IP_COLUMN
            ]
        
        return keys
    
    def __init__(self):
        self.filename = Base.__get_base_path()

        if os.path.exists(self.filename):
            self.df = pd.read_csv(self.filename, index_col=INDEX_COLUMN, dtype=str)
        else:
            keys = Base.__make_keys()
            self.df = pd.DataFrame(data=[], columns=keys, index=pd.Series([], name=INDEX_COLUMN), dtype=str)

    def __del__(self):
        if not self.df.empty:
            self.df.to_csv(self.filename)
            
    def check_append(self):
        transactionID = len(self.df)
        return transactionID
    
    def append(self, transaction : dict) -> int:
        """ Add transcation to batch and return (trxID, RRN, AuthCode) """
        transactionID = self.check_append()
       
        self.df.loc[transactionID] = pd.Series(data=transaction, index=transaction.keys())
        return transactionID
    
    def get_by_id(self, transcationID : int):
        return self.df.iloc[transcationID]

    def modify(self, transactionID : int, data : dict):
        # need to optimize:
        for key in data.keys():
            self.df.iloc[transactionID, self.df.columns.get_loc(key)] = data[key]
   
    def find(self, request : dict) -> pd.DataFrame:
        search = []
        for key in request.keys():
            if key in self.df.columns:
                search.append(self.df[key] == request[key])

        return self.df.loc[np.logical_and.reduce(search)]