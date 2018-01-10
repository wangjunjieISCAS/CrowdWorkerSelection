# -*- coding: utf-8 -*-
"""
Created on Tue Jan 02 18:17:28 2018

@author: wang
"""

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

df = pd.read_csv('data/finding3.csv')

header = df['projectName']
relevance = df['relevance with task']

N = len(header)
ind = np.arange(N)
plt1 = plt.bar(ind, relevance, color='b')


plt.ylim([-2,4])
plt.yticks(fontsize=10)
plt.ylabel('difference in relevance', fontsize=12)
#plt.xlim([1, 2410])
plt.xticks(fontsize=10)
plt.xlabel('project id', fontsize=12)
plt.legend(loc='best',numpoints=1, fontsize=12)
#plt.show()

plt.savefig('figure/observ3.jpg', dpi = 2000);