package com.baseline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.data.CrowdWorker;
import com.data.Phone;
import com.data.TestProject;

public class ISSRESelectionStrategy {
	private double weight = 0.6;
	private int selectionNumEachIter = 5;
	
	public HashMap<Integer, ArrayList<ArrayList<String>>> scoreBasedSelection ( LinkedHashMap<String, CrowdWorker> candidateWorkerList , ArrayList<String> candidateWorkers, TestProject project ) {
		ArrayList<String> selectedWorkers = new ArrayList<String>();
		ArrayList<String> coveredContextList = new ArrayList<String>();
		
		//最外层循环只是遍历所有选择的人数
		for ( int i =0; i < candidateWorkers.size(); i++ ) {
			//在这轮中，还有哪些candidateWorkers
			ArrayList<String> nowCandWorkers = new ArrayList<String>();
			for ( int j = 0; j < candidateWorkers.size(); j++ ) {
				String id = candidateWorkers.get( j );
				if ( !selectedWorkers.contains( id )) {
					nowCandWorkers.add( id );
				}
			}
			
			Double[] covValueList = new Double[nowCandWorkers.size()];
			Double[] qualityValueList = new Double[nowCandWorkers.size()];
			
			for ( int j =0; j < nowCandWorkers.size(); j ++ ) {
				CrowdWorker worker = candidateWorkerList.get( nowCandWorkers.get( j) );
				double covValue = this.extractContextCoverage(worker, coveredContextList);
				double qualityValue = this.extractTestQuality(worker);
				//System.out.println(  "covValue is : " + covValue + " ; qualityValue is : " + qualityValue );
				
				covValueList[j] = covValue;
				qualityValueList[j] = qualityValue;
			}
			
			if ( covValueList.length < 1 )
				break;
			Double maxCovValue, maxQualityValue;
			maxCovValue = Collections.max( Arrays.asList( covValueList ));
			maxQualityValue = Collections.max( Arrays.asList( qualityValueList ));
			
			//System.out.println ( "======================= " + covValueList.length );
			/*
			for ( int k = 0; k < covValueList.length; k++ )
				System.out.println( covValueList[k] );
				*/
			
			HashMap<Integer, Double> comValueMap = new HashMap<Integer, Double>();
			for ( int j = 0; j < nowCandWorkers.size(); j++ ) {
				covValueList[j] = covValueList[j] / maxCovValue;
				qualityValueList[j] = qualityValueList[j] / maxQualityValue;
				
				//System.out.println ( "-------------------------------- " + covValueList[j] + "   " + maxCovValue  ); 
				double comValue = covValueList[j]  + weight * qualityValueList[j];
				
				comValueMap.put( j, comValue );
			}
			
			//每次选择5个，否则速度太慢了
			List<HashMap.Entry<Integer, Double>> newComValueList = new ArrayList<HashMap.Entry<Integer, Double>> ( comValueMap.entrySet() );
			Collections.sort( newComValueList, new Comparator<HashMap.Entry<Integer, Double>>() {
				public int compare(HashMap.Entry<Integer, Double> o1, HashMap.Entry<Integer, Double> o2) {      
			        return o2.getValue().compareTo(o1.getValue() ) ;
			    }
			});
			
			for ( int j =0; j < selectionNumEachIter && j < newComValueList.size(); j++ ) {
				int selectedIndex = newComValueList.get(j).getKey();
				String selectedId = nowCandWorkers.get( selectedIndex );
				//System.out.println( "The selected worker is : " + selectedId + " its strategy value is : " + covValueList[selectedIndex] + " " + qualityValueList[selectedIndex] );
				
				selectedWorkers.add( selectedId );
				
				Phone phoneInfo = candidateWorkerList.get( selectedId).getPhoneInfo();
				coveredContextList.add( phoneInfo.getPhoneType() );
				coveredContextList.add( phoneInfo.getOS() );
				coveredContextList.add( phoneInfo.getNetwork() );
				coveredContextList.add( phoneInfo.getISP() );
				//System.out.println( coveredContextList.size() );
			}
			
		}
		
		HashMap<Integer, ArrayList<ArrayList<String>>> selectionResults = new HashMap<Integer, ArrayList<ArrayList<String>>>();
		for ( int i =0; i < selectedWorkers.size(); i++ ) {
			int selectedNumber = i+1;
			ArrayList<String> thisSelection = new ArrayList<String>();
			for ( int j =0; j <= i ; j++ ) {
				thisSelection.add( selectedWorkers.get( j ));
			}
			
			ArrayList<ArrayList<String>> selectionForNum = new ArrayList<ArrayList<String>>();
			selectionForNum.add( thisSelection );
			
			selectionResults.put( selectedNumber, selectionForNum );
		}
		
		return selectionResults;
	}
	
	public Double extractContextCoverage ( CrowdWorker worker, ArrayList<String> coveredContextList ) {
		double score = 0.0;
		Phone phoneInfo = worker.getPhoneInfo();
		if ( !coveredContextList.contains( phoneInfo.getPhoneType() )) {
			score += 1.0;
		}
		if ( !coveredContextList.contains( phoneInfo.getOS() )) {
			score += 1.0;
		}
		if ( !coveredContextList.contains( phoneInfo.getISP() )) {
			score += 1.0;
		}
		if ( !coveredContextList.contains( phoneInfo.getNetwork() )) {
			score += 1.0;
		}
		
		return score;
	}
	
	public Double extractTestQuality ( CrowdWorker worker  ) {
		double score;
		int bugNum = worker.getCapInfo().getNumBug()[0];
		score = 1.0 * bugNum;
		
		return score;
	}
	
}
