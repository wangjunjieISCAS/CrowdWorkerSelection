# -*- coding: utf-8 -*-
"""
Created on Tue Jan 02 17:53:51 2018

@author: wang
"""

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

df = pd.read_csv('data/finding2.csv')

num = df['workerName']
#bug = df['accumulated bugs detected']
bug = df['number of bugs detected']

N = len(num)
ind = np.arange(N)
plt1 = plt.scatter(ind, bug, color='b' )


#plt.ylim([0,350])
plt.yticks(fontsize=10)
plt.ylabel('number of bugs detected', fontsize=12)
plt.xlim([1, 2410])
plt.xticks(fontsize=10)
plt.xlabel('workers', fontsize=12)
#plt.legend(loc='best',numpoints=1, fontsize=12)
#plt.show()

plt.savefig('data/observ2.jpg', dpi = 2000);