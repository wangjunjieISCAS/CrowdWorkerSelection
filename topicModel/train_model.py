# -*- coding: utf-8 -*-
"""
Created on Tue Dec 12 13:27:06 2017

@author: wang
"""

import os
import numpy as np
import lda
import pickle

from data_load import *
from data_store import *


train_dir = 'data/train/'
model_dir = 'data/model/'

train_set = load_term_frequency_each_doc(train_dir)
term_list = load_term_list(train_dir)
index = load_index(train_dir)

model = lda.LDA(n_topics=30, n_iter=5000, random_state=1)
model.fit( train_set )  # model.fit_transform(X) is also available

with open( os.path.join( model_dir + 'model.pickle'), 'wb') as handle:
    pickle.dump( model, handle, protocol=pickle.HIGHEST_PROTOCOL)

topic_word = model.topic_word_  # model.components_ also works
n_top_words = 8
for i, topic_dist in enumerate(topic_word):
    topic_words = np.array(term_list)[np.argsort(topic_dist)][:-(n_top_words+1):-1]
    print('Topic {}: {}'.format(i, ' '.join(topic_words)))


doc_topic = model.doc_topic_

store_doc_topic_distribution ( "data/task_topic_dis.txt", doc_topic, index )