# -*- coding: utf-8 -*-
"""
Created on Tue Jan 02 18:09:49 2018

@author: wang
"""

# -*- coding: utf-8 -*-
"""
Created on Tue Jan 02 17:53:51 2018

@author: wang
"""

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

df = pd.read_csv('data/finding4.csv')

header = df['projectName']
reports = df['reports']
dup_reports = df['duplicate reports']


N = len(header)
ind = np.arange(N)
plt1 = plt.plot(ind, reports, color='r' )
plt1 = plt.plot(ind, dup_reports, color='b' )

#plt.ylim([0,350])
plt.yticks(fontsize=10)
plt.ylabel('number', fontsize=12)
#plt.xlim([1, 2410])
plt.xticks(fontsize=10)
plt.xlabel('project id', fontsize=12)
plt.legend(loc='best',numpoints=1, fontsize=12)
#plt.show()

plt.savefig('data/observ4.jpg', dpi = 2000);