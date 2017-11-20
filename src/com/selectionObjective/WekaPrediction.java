package com.selectionObjective;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.Logistic;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Standardize;

public class WekaPrediction {
	
	public HashMap<Integer, Double> trainAndPredictProb ( String fileTrain, String fileTest, String classifyType ){
		try {
			/*
			 * not suitable for label yes/no, 
			 * do not have confusion matrix, 
			 * so do not use linear regression
			 * LinearRegression classify = new LinearRegression();   
			 */
			Classifier classify = null;
			if ( classifyType.equals( "AdaBoostM1")){
				classify = new AdaBoostM1();
			}
			else if (classifyType.equals( "J48")){
				classify =  new J48();			}
			else if (classifyType.equals( "Logistic")){
				classify = new Logistic() ; 
			}
			else if (classifyType.equals( "NaiveBayes")){
				classify = new NaiveBayes() ; 
			}
			else if (classifyType.equals( "RandomForest")){
				classify = new RandomForest() ; 
			}
			else if (classifyType.equals( "LibSVM")){
				classify = new LibSVM() ; 
			}
			else{
				classify = new NaiveBayes() ;     //the default one is naive bayes
			}
			
		    String[] options = { };
		    
		    classify.setOptions(options);
		    
			Instances train = DataSource.read( fileTrain );
			Instances test = DataSource.read( fileTest );

			// filter data
			Standardize filter = new Standardize();
			filter.setInputFormat(train); // initializing the filter once with training set
												
			Instances newTrain = Filter.useFilter(train, filter); // configures filter based on train instances and returns filtered instances
			Instances newTest = Filter.useFilter(test, filter); // create new test set
			
			newTrain.setClassIndex( newTrain.numAttributes() - 1 );
			newTest.setClassIndex( newTest.numAttributes() - 1 );
			   
			classify.buildClassifier( newTrain);
			// evaluate classifier and print some statistics
			Evaluation evaluation = new Evaluation( newTrain);
			evaluation.evaluateModel( classify, newTest);
			
			//只记录为yes的概率
			HashMap<Integer, Double> predictResult = new HashMap<Integer, Double>();
			HashMap<Integer, String> trueLabel = new HashMap<Integer, String>();
			
			int instanceNum = newTest.numInstances();//获取预测实例的总数
			for( int i=0; i< instanceNum ; i++){//输出预测数据
				double[] probability = classify.distributionForInstance( newTest.instance( i ) );
				System.out.println ( "prob " + probability[0] + " || " + probability[1] );   
				
				double predicted = 0.0;
				double predictedResult = classify.classifyInstance( newTest.instance(i) );
				String category = newTest.classAttribute().value( (int)predictedResult );
				
				String trueClassLabel = newTest.instance(i).toString( newTest.classIndex());
				trueLabel.put( i, trueClassLabel );
				
				if ( category.equals( "yes") ){
					predicted = Math.max( probability[0], probability[1]);
				}
				else{
					predicted = Math.min( probability[0], probability[1]);
				}
				
				predictResult.put( i , predicted );
			}
			
			System.out.println ( evaluation.toSummaryString() );
			System.out.println ( evaluation.toMatrixString() );
			System.out.println( evaluation.areaUnderROC( 0) );
			System.out.println( );
			System.out.println( evaluation.precision( 1) + " " + evaluation.recall( 1 ));
			
			//将f-measure进行存储，不用计算了
			Double fMeasure = evaluation.fMeasure( 1);
			
			Double[] confusionMatrix = new Double[16];
			confusionMatrix[0] = evaluation.truePositiveRate(0);
			confusionMatrix[1] = evaluation.trueNegativeRate(0);
			confusionMatrix[2] = evaluation.falsePositiveRate(0);
			confusionMatrix[3] = evaluation.falseNegativeRate(0);
			
			confusionMatrix[4] = evaluation.truePositiveRate(1);
			confusionMatrix[5] = evaluation.trueNegativeRate(1);
			confusionMatrix[6] = evaluation.falsePositiveRate(1);
			confusionMatrix[7] = evaluation.falseNegativeRate(1);
			
			confusionMatrix[8] = evaluation.precision(0);
			confusionMatrix[9] = evaluation.recall( 0);
			confusionMatrix[10] = evaluation.fMeasure( 0);
			confusionMatrix[11] = evaluation.precision(1);
			confusionMatrix[12] = evaluation.recall( 1);
			confusionMatrix[13] = evaluation.fMeasure( 1);
			
			confusionMatrix[14] = evaluation.areaUnderROC(0);
			confusionMatrix[15] = evaluation.areaUnderROC(1);
			
			//not return the performance results, might need in future
			//return confusionMatrix;		
			
			return predictResult;
		} catch (Exception e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
