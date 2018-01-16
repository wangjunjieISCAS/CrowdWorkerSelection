package com.performanceEvaluation;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import weka.attributeSelection.ASSearch;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.RankedOutputSearch;
import weka.attributeSelection.Ranker;
import weka.attributeSelection.WrapperSubsetEval;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;

/*
 * 运行完FeatureRefinement后，
 * 运行该类，进行特征选择；
 */
public class FeatureSelection {
	
	public void wrapperSearchGreedyStepwiseFeatureSelection ( String fileName, String outFileName ){
		try {
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( outFileName )) , "GB2312"), 1024);
			
			Instances data = DataSource.read(  fileName );	
			//DataSink.write( "data/feature/featuresOut.arff", data);
			
			System.out.println ( data.numAttributes() );
			data.setClassIndex( data.numAttributes() -1  );
			
			AttributeSelection selection = new AttributeSelection();  
			WrapperSubsetEval evaluator = new WrapperSubsetEval();
			evaluator.setClassifier( new Logistic() );
			GreedyStepwise search = new GreedyStepwise();
			search.setSearchBackwards(true);
			selection.setEvaluator( evaluator );
			selection.setSearch( search );
			selection.SelectAttributes( data );
			selection.setRanking( true );
			selection.setXval( true );
			
			evaluator.buildEvaluator( data );
			int[] attrIndex = selection.selectedAttributes();
			
			System.out.println ( "numberAttributesSelected: " + selection.numberAttributesSelected() );
			for ( int i =0; i < attrIndex.length; i++){
				int index = attrIndex[i];
				String featureName = data.attribute( index).name();
				
				output.write( index + "," +"," + featureName + ",");
				output.newLine();
				System.out.println( i + " " + index + " " + " " + featureName );
			}
			
			output.flush();
			output.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public HashMap<String, Integer> informationGainFeatureSelection ( String fileName ){
		HashMap<String, Double> featureValue = new HashMap<String, Double>();
		
		Instances data;
		try {
			data = DataSource.read(  fileName );
			
			//System.out.println ( data.numAttributes() );
			data.setClassIndex( data.numAttributes() -1  );
			
			Ranker rank = new Ranker();
			InfoGainAttributeEval eval = new InfoGainAttributeEval();
			
			eval.buildEvaluator( data );
			int[] attrIndex = rank.search( eval, data);
			
			for ( int i =0; i < attrIndex.length; i++){
				int index = attrIndex[i];
				Double value = eval.evaluateAttribute( index);
				String featureName = data.attribute( index).name();
				
				featureValue.put( featureName, value);
			}			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		
		ArrayList<Map.Entry<String, Double>> featureValueList =  new ArrayList<Map.Entry<String, Double>>(featureValue.entrySet());
		Collections.sort( featureValueList, new Comparator <Map.Entry<String, Double>>() {
			@Override
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
				return o1.getValue().compareTo( o2.getValue() );
			}
		});
		
		HashMap<String, Integer> featureRank = new HashMap<String, Integer>();
		for ( int i =0; i < featureValueList.size(); i++ ) {
			int index = i+1;
			String featureName = featureValueList.get(i).getKey();
			
			featureRank.put( featureName, index );
		}
		return featureRank;
	}
	
		
	public void featureEvaluation ( String folderName, String outFileName ) {
		HashMap<String, String> featureNameMap = new HashMap<String, String>();
		
		for ( int i =0; i < 4; i++) {
			String tag = "";
			if ( i == 0 )
				tag = "0";
			if ( i == 1 )
				tag = "2w";
			if ( i == 2 )
				tag = "1m";
			if ( i == 3 )
				tag = "2m";
			
			String featureName = "numProject-" + i;
			featureNameMap.put( featureName , "P" + tag );
			
			featureName = "numReport-" + i;
			featureNameMap.put( featureName , "R" + tag );
			
			featureName = "numBug-" + i ;
			featureNameMap.put( featureName , "B" + tag );
			
			featureName = "percBug-" + i ;
			featureNameMap.put( featureName , "C" + tag );
		}
		featureNameMap.put( "durationLastAct", "IV");		
		
		File projectsFolder = new File ( folderName );
		String[] projectFileList = projectsFolder.list();
		
		HashMap<String, ArrayList<Integer>> featureRankList = new HashMap<String, ArrayList<Integer>>();
		for ( int i = 0; i< projectFileList.length; i++ ){
			String projectFileName = folderName + "/" + projectFileList[i];
			
			System.out.println( "processing the " +  i  + " projects");
			HashMap<String, Integer> featureRank = this.informationGainFeatureSelection( projectFileName );
			
			for ( String feature : featureRank.keySet() ) {
				Integer rank = featureRank.get( feature );
				
				ArrayList<Integer> valueList = new ArrayList<Integer>();
				if ( featureRankList.containsKey( feature )) {
					valueList = featureRankList.get( feature );
				}
				valueList.add( rank );
				
				featureRankList.put( feature, valueList );
			}
		}
		
		HashMap<String, Double> averageRankList = new HashMap<String, Double>();
		for ( String feature : featureRankList.keySet() ) {
			ArrayList<Integer> valueList = featureRankList.get( feature );
			
			double sum = 0.0;
			for ( int i =0; i < valueList.size(); i++ ) {
				sum += valueList.get( i );
			}
			sum = sum / valueList.size();
			
			averageRankList.put( feature, sum );
			System.out.println ( feature + " " + sum );
		}
		
		/*
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( outFileName  ) );
			
			writer.write( " " + "," + "importance") ;
			writer.newLine();
			
			for ( String feature : featureRankList.keySet() ) {
				ArrayList<Integer> valueList = featureRankList.get( feature );
				
				String storeFeature = featureNameMap.get( feature );
				for ( int i =0; i < valueList.size() ; i ++) {
					writer.write( storeFeature  + "," + valueList.get( i ));
					writer.newLine();
				}
				
			}
			
			writer.flush();			
			writer.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/		
		
	}
	
	public static void main ( String args[] ){
		FeatureSelection selection = new FeatureSelection();
		//selection.informationGainFeatureSelection( "data/feature/totalFeatures.csv");
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式
		String time = dateFormat.format( new Date() ); 
		System.out.println( "current time : " + time ); 
		
		String folderName = "data/input/weka/test2";
		String outFileName = "data/output/featureImportance/selection.csv";
		//selection.wrapperSearchGreedyStepwiseFeatureSelection( fileName, outFileName  );
		//selection.informationGainFeatureSelection( foldName );
		
		selection.featureEvaluation(folderName, outFileName );
		
		time = dateFormat.format( new Date() ); 
		System.out.println( "current time : " + time ); 
	}
}	
