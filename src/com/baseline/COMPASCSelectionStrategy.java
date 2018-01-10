package com.baseline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.data.CrowdWorker;
import com.data.TestProject;
import com.dataProcess.SimilarityMeasure;

public class COMPASCSelectionStrategy {
	private double expWeight = 0.6;
	private double revWeight = 0.2;
	private double divWeight = 0.2;
	
	private int selectionNumEachIter = 5;
	
	public Double extractExperienceValue ( CrowdWorker worker ) {
		Integer experience = worker.getCapInfo().getNumBug()[0];
		return 1.0*experience;
	}
	
	public Double extractRelevantStrategy ( CrowdWorker worker, TestProject project ) {
		SimilarityMeasure simTool = new SimilarityMeasure();
		ArrayList<String> taskTerms = project.getTestTask().getTaskDescription();
		ArrayList<String> workerTerms = worker.getDomainKnInfo().getDomainKnowledge();
		
		Double sim = simTool.cosineSimilarity( taskTerms, workerTerms );
		return sim;
	}
	
	public Double extractDiversityStrategy ( CrowdWorker worker, ArrayList<CrowdWorker> selectedWorkers, TestProject project ) {
		ArrayList<String> taskTermList = project.getTestTask().getTaskDescription();
		
		HashMap<String, Integer> taskTermMap = new HashMap<String, Integer>();
		int maxTaskCount =0;
		for ( int i =0; i < taskTermList.size(); i++ ) {
			String term = taskTermList.get( i );
			int count = 1;
			if ( taskTermMap.containsKey( term )) {
				count+= taskTermMap.get( term );
			}
			if ( count > maxTaskCount )
				maxTaskCount = count;
			
			taskTermMap.put( term, count );
		}
		
		HashMap<String, Integer> workerTermMap = new HashMap<String, Integer>();
		int maxWorkerCount = 1;
		for ( int i =0; i < selectedWorkers.size(); i++ ) {
			CrowdWorker tempWorker = selectedWorkers.get( i );
			ArrayList<String> workerTerm = tempWorker.getDomainKnInfo().getDomainKnowledge();
			for ( int j =0; j < workerTerm.size(); j++ ) {
				String term = workerTerm.get( j );
				int count = 1;
				if ( workerTermMap.containsKey( term )) {
					count += workerTermMap.get( term );
				}
				if ( count > maxWorkerCount )
					maxWorkerCount = count;
				
				workerTermMap.put( term, count );
			}
		}
		
		HashMap<String, Double> coverProb = new HashMap<String, Double>();
		for ( String term : taskTermMap.keySet() ) {
			int termCount = taskTermMap.get( term );
			int count = 0;
			if ( workerTermMap.containsKey( term )) {
				count = workerTermMap.get( term );
			}
			
			double rateCount = (1.0*count) / (1.0*maxWorkerCount );
			double prob = rateCount / (1.0* termCount );
			prob = 1.0-prob;
			coverProb.put( term , prob );
		}
		
		ArrayList<String> candWorkerTermList = worker.getDomainKnInfo().getDomainKnowledge();
		double diversity = 0.0;
		for ( String term : coverProb.keySet() ) {
			double prob = coverProb.get( term );
			
			int count = Collections.frequency( candWorkerTermList,  term );
			diversity += count * prob;
		}
		return diversity;
	}
	
	
	public HashMap<Integer, ArrayList<ArrayList<String>>> multipleStrategySelection  ( LinkedHashMap<String, CrowdWorker> candidateWorkerList , ArrayList<String> candidateWorkers, TestProject project ) {
		ArrayList<String> selectedWorkers = new ArrayList<String>();
		ArrayList<CrowdWorker> selectedWorkersInfo = new ArrayList<CrowdWorker>();
		
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
			
			Double[] expValueList = new Double[nowCandWorkers.size()];
			Double[] revValueList = new Double[nowCandWorkers.size()];
			Double[] divValueList = new Double[nowCandWorkers.size()];
			
			for ( int j =0; j < nowCandWorkers.size(); j ++ ) {
				CrowdWorker worker = candidateWorkerList.get( nowCandWorkers.get( j) );
				double expValue = this.extractExperienceValue( worker );
				double revValue = this.extractRelevantStrategy( worker, project);
				double divValue = this.extractDiversityStrategy(worker, selectedWorkersInfo, project);
				//System.out.println(  "expValue is : " + expValue + " ; revValue is : " + revValue + " ; divValue is : " + divValue );
				
				expValueList[j] = expValue;
				revValueList[j] = revValue;
				divValueList[j] = divValue;
			}
			
			if ( expValueList.length < 1 )
				break;
			Double maxExpValue, minExpValue, maxRevValue, minRevValue, maxDivValue, minDivValue;
			maxExpValue = Collections.max( Arrays.asList( expValueList ));
			minExpValue = Collections.min( Arrays.asList( expValueList ));
			maxRevValue = Collections.max( Arrays.asList( revValueList ));
			minRevValue = Collections.min( Arrays.asList( revValueList ));
			maxDivValue = Collections.max( Arrays.asList( divValueList ));
			minDivValue = Collections.min( Arrays.asList( divValueList ));
			
			HashMap<Integer, Double> mulValueMap = new HashMap<Integer, Double>();
			for ( int j = 0; j < nowCandWorkers.size(); j++ ) {
				expValueList[j] = (expValueList[j] - minExpValue) / ( maxExpValue - minExpValue );
				revValueList[j] = (revValueList[j] - minRevValue) / (maxRevValue - minRevValue );
				divValueList[j] = (divValueList[j] - minDivValue) / (maxDivValue - minDivValue );
				
				double multiValue = expWeight * expValueList[j] + revWeight * revValueList[j] + divWeight * divValueList[j];
				
				mulValueMap.put( j, multiValue );
			}
			
			//每次选择5个，否则速度太慢了
			List<HashMap.Entry<Integer, Double>> newMulValueList = new ArrayList<HashMap.Entry<Integer, Double>> ( mulValueMap.entrySet() );
			Collections.sort( newMulValueList, new Comparator<HashMap.Entry<Integer, Double>>() {
				public int compare(HashMap.Entry<Integer, Double> o1, HashMap.Entry<Integer, Double> o2) {      
			        return o2.getValue().compareTo(o1.getValue() ) ;
			    }
			});
			
			for ( int j =0; j < selectionNumEachIter && j < newMulValueList.size(); j++ ) {
				int selectedIndex = newMulValueList.get(j).getKey();
				String selectedId = nowCandWorkers.get( selectedIndex );
				//System.out.println( "The selected worker is : " + selectedId + " its strategy value is : " + expValueList[j] + 
					//	" " + revValueList[j] + " " + divValueList[j] );
				
				selectedWorkers.add( selectedId );
				selectedWorkersInfo.add( candidateWorkerList.get( selectedId ) );
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
}
