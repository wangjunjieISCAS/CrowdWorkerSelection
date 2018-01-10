# -*- coding: utf-8 -*-
"""
Created on Tue Jan 02 10:00:11 2018

@author: wang
"""

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

df = pd.read_csv('data/finding1.csv')

header = df['projectName']
worker = df['workers submited reports']
bug_worker = df['workers detected bugs']

N = len(header)
ind = np.arange(N)
plt.plot(ind, worker, color='r', linewidth=1 )
plt.plot(ind, bug_worker, color='b', linewidth=1)


plt.ylim([0,350])
plt.yticks(fontsize=10)
plt.ylabel('number', fontsize=12)
plt.xlim([1, 532])
plt.xticks(fontsize=10)
plt.xlabel('project id', fontsize=12)
plt.legend(loc='upper right',numpoints=1, fontsize=12)
#plt.show()

plt.savefig('figure/observ1.jpg', dpi = 2000);