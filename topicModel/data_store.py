# -*- coding: utf-8 -*-
"""
Created on Wed Dec 13 11:25:24 2017

@author: wang
"""

def store_doc_topic_distribution ( file_name, doc_topic, index  ):
    with open( file_name, 'w') as f:
        for i in range (0 , len(doc_topic)):
            indexs = index[i].split()
            f.write ( str(indexs[1]) + ":" )
            for prob in doc_topic[i]:
                f.write ( str(prob) + " " )
            f.write ("\n")
           
        f.close