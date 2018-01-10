# -*- coding: utf-8 -*-
"""
Created on Thu Dec 28 09:59:09 2017

@author: wang
"""

import os
import numpy as np    
from sklearn.linear_model import LogisticRegression
import csv

def load_dataset ( file_name, feature_name, category_name ):
    feature_values = {}
    
    for i in range (len(feature_name)):
        with open( file_name,'rb') as csvfile:
           reader = csv.DictReader(csvfile)
           value = [row[feature_name[i]] for row in reader]
           feature_values[feature_name[i]] = value
        csvfile.close
    
    with open( file_name,'rb') as csvfile:
        reader = csv.DictReader(csvfile)
        category = [row[category_name] for row in reader]
    csvfile.close
    return feature_values, category

def max_min_normalization ( feature_value ):
    feature_value = np.array(feature_value).astype(np.float)

    max_value = np.max(feature_value)
    min_value = np.min(feature_value)
    
    norm_feature_value = []
    for item in feature_value:
        temp = (item - min_value)/ (max_value - min_value )
        norm_feature_value.append ( temp )
        
    return norm_feature_value

def evaluate_features( feature_name, file_name ):
    feature_values, category = load_dataset ( file_name, feature_name , "category")
    
    features = []
    for name in feature_name :
        value = feature_values[name]
        norm_value = max_min_normalization ( value )
        features.append ( norm_value )
        #print len(value)
    
    feature_for_regression = np.column_stack(features)
    
    classifier = LogisticRegression()
    classifier.fit ( feature_for_regression, category )
    
    #for item in classifier.coef_:
     #   print item
    #print( classifier.coef_)
    
    #feature_weight = classifier.coef_
    temp_values = np.asarray ( classifier.coef_).tolist()
    feature_weight = temp_values[0]
    #print type( feature_weight )
    #print feature_weight
    
    feature_weight_dict = {}
    for i in range(len(feature_name)):
        feature_weight_dict[feature_name[i]] = feature_weight[i]
    
    feature_weight_dict = sorted(feature_weight_dict.iteritems(), key=lambda d:d[1], reverse = True)
    #print feature_weight_dict
    
    feature_weight_rank = {}
    for i in range( len(feature_weight_dict)):
        entry = feature_weight_dict[i]
        feature_weight_rank[entry[0]] = i+1
   # print feature_weight_rank
    
    return feature_weight_rank

#生成的结果文件以feature name按照列排列，便于生成python盒图
def evaluate_features_multiple_projects_feature_in_column ( folderName, result_file ):
    feature_name = ['numProject-0', 'numReport-0', 'numBug-0', 'percBug-0',	
                    'numProject-1', 'numReport-1', 'numBug-1', 'percBug-1', 
                    'numProject-2', 'numReport-2', 'numBug-2', 'percBug-2', 
                    'numProject-3',  'numReport-3','numBug-3', 'percBug-3', 
                    'durationLastAct', 'relevant',
                    'topic-0',	'topic-1', 'topic-2',	'topic-3',	'topic-4',	'topic-5',	
                    'topic-6', 'topic-7', 'topic-8',	'topic-9',	'topic-10', 
                    'topic-11', 'topic-12','topic-13', 	'topic-14', 	'topic-15',
                    'topic-16',	'topic-17',	'topic-18',	'topic-19',	'topic-20',
                    'topic-21',	'topic-22', 'topic-23',	'topic-24','topic-25',	
                    'topic-26', 'topic-27',	'topic-28',	'topic-29']
     
    file_name_list = os.listdir ( folderName )
    
    feature_rank_allprojects = {}
    for file_name in file_name_list:
        if ( file_name.startswith ('test')):
            continue
        print file_name
        feature_weight_rank = evaluate_features( feature_name, folderName + "/" + file_name )
        feature_rank_allprojects[file_name] = feature_weight_rank
    
    column_titles = []
    column_titles.append ('project_name')
    column_titles.extend ( feature_name )
    
    with open ( result_file, 'wb') as csvfile:
        writer = csv.writer ( csvfile )
        writer.writerow ( column_titles )
        
        for key, value in feature_rank_allprojects.items():
            csv_values = []
            csv_values.append ( key )
            for feature in feature_name:
                feature_value = value[feature]
                csv_values.append ( feature_value )
            writer.writerow ( csv_values )
             
    csvfile.close
    

#暂时不用了，生成的结果文件以feature name按照行排序
def evaluate_features_multiple_projects ( folderName, result_file ):
    file_name_list = os.listdir ( folderName )
    
    feature_rank_allprojects = {}
    for file_name in file_name_list:
        if ( file_name.startswith ('test')):
            continue
        print file_name
        feature_weight_rank = evaluate_features( folderName + "/" + file_name )
        feature_rank_allprojects[file_name] = feature_weight_rank
    
    column_titles = []
    column_titles.append ( 'feature_name')
    
    project_names = feature_rank_allprojects.keys()
    column_titles.extend ( project_names )
    print column_titles
    
    feature_rank_value = feature_rank_allprojects[column_titles[1]]
    
    with open(result_file,"wb") as csvfile: 
        writer = csv.writer(csvfile)
        writer.writerow ( column_titles )
        
        for (key, value) in feature_rank_value.items():
            csv_values = []
            csv_values.append ( key )
            for i in project_names:
                this_value = feature_rank_allprojects[i]
                csv_values.append ( this_value[key])
            writer.writerow ( csv_values )
        
    csvfile.close 


evaluate_features_multiple_projects_feature_in_column ( '../data/input/weka/evaluation', '../data/output/featureImportance/featureImportance.csv')
#evaluate_features ( '../data/input/weka/a.csv' )