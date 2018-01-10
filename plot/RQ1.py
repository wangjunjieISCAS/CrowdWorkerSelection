# -*- coding: utf-8 -*-
"""
Created on Tue Jan 09 10:56:07 2018

@author: wang
"""

import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt

sns.set(style="ticks")

df = pd.read_csv('data/RQ1.csv')

flatui = [ "red", "yellow", "cyan"]
p1 = sns.color_palette(flatui)

sns_plot = sns.boxplot(x =df['   '], y= df['  '], hue = df[' '], palette=p1,  width = 0.6)
#sns_plot = sns.boxplot(x =df['warning type'], y= df['warning number'], hue = df[' '], width = 0.6)

#plt.legend(loc="lower center", bbox_to_anchor=(0.35, 0), numpoints=1)
#lower center
plt.legend(loc="best", numpoints=1)
leg = plt.gca().get_legend()
ltext  = leg.get_texts()
plt.setp(ltext, fontsize=15) 

#plt.ylim(0.0, 1.0)
plt.yticks(fontsize=12)

plt.xticks(fontsize=18);

plt.ylabel('performance', fontsize=18)
#plt.xlabel('type', fontsize=18)



sns_plot.figure.savefig('figure/RQ1.jpg', dpi = 500)


