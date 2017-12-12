# -*- coding: utf-8 -*-
"""
Created on Tue Dec 12 14:09:08 2017

@author: wang
"""

import os
import lda.utils


def load_term_frequency_each_doc( dir ):
        reuters_ldac_fn = os.path.join( dir, 'termFreq.txt')
        return lda.utils.ldac2dtm(open(reuters_ldac_fn), offset=0)

def load_term_list ( dir):
        reuters_vocab_fn = os.path.join(dir, 'terms.txt')
        with open(reuters_vocab_fn) as f:
            vocab = tuple(f.read().split())
        return vocab


def load_index ( dir):
        reuters_titles_fn = os.path.join(dir, 'index.txt')
        with open(reuters_titles_fn) as f:
            titles = tuple(line.strip() for line in f.readlines())
        return titles