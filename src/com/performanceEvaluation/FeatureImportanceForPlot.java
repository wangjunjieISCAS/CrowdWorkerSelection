package com.performanceEvaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.data.Constants;

public class FeatureImportanceForPlot {
	/*
	 * 除了整理成pyplot的格式，还需要将relevance相关的指标去掉，并且进行重新排序
	 */
	
	public ArrayList<HashMap<String, Integer>> readTotalFeatureImportance ( String fileName ) {
		ArrayList<String> attrName = new ArrayList<String>();
		ArrayList<HashMap<String, Integer>> importanceList = new ArrayList<HashMap<String, Integer>>();
		
		try {			
			BufferedReader reader = new BufferedReader(new FileReader(new File( fileName ) ) );
			String line = "";
			boolean isFirstLine = true;
			while ((line = reader.readLine()) != null) {
				String[] temp = line.split(",");
				
				if ( isFirstLine == true ) {
					for ( int i =1; i < temp.length; i++ ) {
						attrName.add( temp[i].trim() );
					}
				}else {
					HashMap<String, Integer> value= new HashMap<String, Integer>();
					for ( int i =1; i < temp.length; i++ ) {
						value.put( attrName.get(i-1), Integer.parseInt( temp[i]));
					}
					importanceList.add( value );
				}
				isFirstLine = false;
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return importanceList;
	}
	
	public void generateFileForPlot ( ArrayList<LinkedHashMap<String, Integer>> importanceList, String fileName , ArrayList<String> storeFeatureName ) {
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( fileName  ) );
			
			writer.write( " " + "," + "importance") ;
			writer.newLine();
			for ( int i =0; i < importanceList.size(); i++ ) {
				LinkedHashMap<String, Integer> valueMap = importanceList.get( i);
				for ( int j =0; j < storeFeatureName.size(); j++ ) {
					String attr = storeFeatureName.get( j);
					
					writer.write( attr + "," + valueMap.get( attr) );
					writer.newLine();
				}
			}
			writer.flush();			
			writer.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public ArrayList<LinkedHashMap<String, Integer>> generateCapFeatureImportance ( ArrayList<HashMap<String, Integer>> importanceList, ArrayList<String> featureName, ArrayList<String> storeFeatureName ) {
		ArrayList<LinkedHashMap<String, Integer>> newImportanceList = new ArrayList<LinkedHashMap<String, Integer>>();
		for ( int i =0; i < importanceList.size(); i++ ) {
			HashMap<String, Integer> value = importanceList.get( i );
			List<HashMap.Entry<String, Integer>> newValue = this.rankFeatureImportance( value );
			
			int newIndex = 1;
			LinkedHashMap<String, Integer> newImportance = new LinkedHashMap<String, Integer>();
			for ( int j=0; j < newValue.size(); j++ ) {
				String attr = newValue.get(j).getKey();
				if ( featureName.contains( attr )) {
					int index = featureName.indexOf( attr );
					String newAttr = storeFeatureName.get( index );
					
					newImportance.put( newAttr, newIndex );
					newIndex++;
					
					newImportanceList.add( newImportance );
				}
			}
		}
		return newImportanceList;
	}
	
	public List<HashMap.Entry<String, Integer>> rankFeatureImportance ( HashMap<String, Integer> featureValue ) {
		List<HashMap.Entry<String, Integer>> newFeatureValue = new ArrayList<HashMap.Entry<String, Integer>>(featureValue.entrySet());

		Collections.sort( newFeatureValue, new Comparator<HashMap.Entry<String, Integer>>() {   
			public int compare(HashMap.Entry<String, Integer> o1, HashMap.Entry<String, Integer> o2) {      
			        //return (o2.getValue() - o1.getValue()); 
			        return o1.getValue().compareTo(o2.getValue() ) ;
			    }
			}); 
		return newFeatureValue;
	}
	
	public static void main ( String args[]) {
		FeatureImportanceForPlot plotTool = new FeatureImportanceForPlot();
		String fileName = "data/output/featureImportance/featureImportance.csv";
		ArrayList<HashMap<String, Integer>> importanceList = plotTool.readTotalFeatureImportance(fileName);
		
		ArrayList<String> featureName = new ArrayList<String>();
		ArrayList<String> storeFeatureName = new ArrayList<String>();
		for ( int i =0; i < 4; i++) {
			featureName.add( "numProject-" + i );
			featureName.add( "numReport-" + i );
			featureName.add( "numBug-" + i );
			featureName.add( "percBug-" + i );
			
			/*
			storeFeatureName.add( "F" + new Integer(i*4+1).toString() );
			storeFeatureName.add( "F" + new Integer(i*4+2).toString() );
			storeFeatureName.add( "F" + new Integer(i*4+3).toString() );
			storeFeatureName.add( "F" + new Integer(i*4+4).toString() );
			*/
			String tag = "";
			if ( i == 0 )
				tag = "0";
			if ( i == 1 )
				tag = "2w";
			if ( i == 2 )
				tag = "1m";
			if ( i == 3 )
				tag = "2m";
			storeFeatureName.add( "P" + tag );
			storeFeatureName.add( "R" + tag );
			storeFeatureName.add( "B" + tag );
			storeFeatureName.add( "C" + tag );
		}
		featureName.add ( "durationLastAct");
		storeFeatureName.add ( "I");
		
		ArrayList<LinkedHashMap<String, Integer>> newImportanceList = plotTool.generateCapFeatureImportance(importanceList, featureName, storeFeatureName);
		plotTool.generateFileForPlot( newImportanceList, Constants.PLOT_DATA_FOLDER + "/RQ1-2.csv" , storeFeatureName );
	}
}
