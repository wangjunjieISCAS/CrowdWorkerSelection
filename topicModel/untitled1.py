# -*- coding: utf-8 -*-
"""
Created on Tue Dec 12 13:57:04 2017

@author: wang
"""

import lda
import lda.datasets
import pickle

X = lda.datasets.load_reuters()
titles = lda.datasets.load_reuters_titles()
X_train = X[10:]
X_test = X[:10]
titles_test = titles[:10]

#model = lda.LDA(n_topics=20, n_iter=1500, random_state=1)
#model.fit(X_train)

with open('model.pickle', 'rb') as handle:
    model = pickle.load(handle)


doc_topic_test = model.transform(X_test)
for title, topics in zip(titles_test, doc_topic_test):
    print("{} (top topic: {})".format(title, topics.argmax()))
    print ( topics[0]  )
    print ( topics[1] )
    