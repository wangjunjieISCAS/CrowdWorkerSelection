# -*- coding: utf-8 -*-
"""
Created on Tue Dec 12 13:57:04 2017

@author: wang
"""

import os
import lda
import pickle

from data_load import *

test_dir = 'data/test/'
model_dir = 'data/model/'


test_set = load_term_frequency_each_doc(test_dir)
index = load_index(test_dir)


with open( os.path.join( model_dir + 'model.pickle'), 'rb') as handle:
    model = pickle.load(handle)

doc_topic_test = model.transform( test_set )

store_doc_topic_distribution ( "data/worker_topic_dis.txt", doc_topic_test, index )


    