# -*- coding: utf-8 -*-
"""
Created on Tue Dec 12 13:27:06 2017

@author: wang
"""

import numpy as np
import lda
import pickle

from data_load import *


X = load_reuters('data/train/')
vocab = load_reuters_vocab('data/train/')
#titles = lda.datasets.load_reuters_titles()

X.shape
X.sum()

model = lda.LDA(n_topics=20, n_iter=1500, random_state=1)
model.fit(X)  # model.fit_transform(X) is also available

with open('model.pickle', 'wb') as handle:
    pickle.dump( model, handle, protocol=pickle.HIGHEST_PROTOCOL)

topic_word = model.topic_word_  # model.components_ also works
n_top_words = 8
for i, topic_dist in enumerate(topic_word):
    topic_words = np.array(vocab)[np.argsort(topic_dist)][:-(n_top_words+1):-1]
    print('Topic {}: {}'.format(i, ' '.join(topic_words)))

    
#doc_topic = model.doc_topic_
#for i in range(10):
#    print("{} (top topic: {})".format(titles[i], doc_topic[i].argmax()))