package com.mainMOCOS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import com.data.CrowdWorker;
import com.data.TestProject;
import com.dataProcess.SimilarityMeasure;

public class WeightSelectionStrategy {
	private double bugProbWeight = 0.4;
	private double revWeight = 0.3;
	private double divWeight = 0.3;
	private int selectionNumEachIter = 1;
	
	//基于值进行排序
	public HashMap<Integer, ArrayList<ArrayList<String>>> selectionStrategy ( ArrayList<String> candidateWorkers, LinkedHashMap<String, CrowdWorker> candidateWorkerList, HashMap<String, Double> bugProbList, TestProject project  ){
		ArrayList<String> selectedWorkers = new ArrayList<String>();
		
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
			
			Double[] bugProbValueList = new Double[nowCandWorkers.size()];
			Double[] revValueList = new Double[nowCandWorkers.size()];
			Double[] divValueList = new Double[nowCandWorkers.size()];
			
			for ( int j =0; j < nowCandWorkers.size(); j ++ ) {
				String userId = nowCandWorkers.get( j );
				CrowdWorker worker = candidateWorkerList.get( userId );
				
				double bugProbValue = bugProbList.get( userId );
				double revValue = this.obtainRelevance(worker, project);
				double divValue = this.obtainDiversity(selectedWorkers, worker, candidateWorkerList);
				
				//System.out.println(  "bugProbValue is : " + bugProbValue + " ; revValue is :" + revValue + " divValue is : " + divValue );
				
				bugProbValueList[j] = bugProbValue;
				revValueList[j] = revValue;
				divValueList[j] = divValue;
			}
			
			if ( bugProbValueList.length < 1 )
				break;
			Double maxBugValue, minBugValue, maxRevValue, minRevValue, maxDivValue, minDivValue;
			maxBugValue = Collections.max( Arrays.asList( bugProbValueList ));
			minBugValue = Collections.min( Arrays.asList( bugProbValueList ));
			maxRevValue = Collections.max( Arrays.asList( revValueList ));
			minRevValue = Collections.min( Arrays.asList( revValueList ));
			maxDivValue = Collections.max( Arrays.asList( divValueList ));
			minDivValue = Collections.min( Arrays.asList( divValueList ));
			
			//System.out.println( maxRevValue + " " + minRevValue );
			HashMap<Integer, Double> mulValueMap = new HashMap<Integer, Double>();
			for ( int j = 0; j < nowCandWorkers.size(); j++ ) {
				//System.out.println( bugProbValueList[j] + " " + revValueList[j] + " " + divValueList[j] );
				
				bugProbValueList[j] = (bugProbValueList[j] - minBugValue) / ( maxBugValue - minBugValue );
				revValueList[j] = ( revValueList[j] - minRevValue ) / (maxRevValue - minRevValue );
				divValueList[j] = (divValueList[j] - minDivValue) / (maxDivValue - minDivValue );
				
				double multiValue = bugProbWeight * bugProbValueList[j] + revWeight * revValueList[j] + divWeight * divValueList[j];
				
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
				//System.out.println( "The selected worker is : " + selectedId + " its strategy value is : " + bugProbValueList[j] + 
					//	" " + revValueList[j] + " " + divValueList[j] );
				
				selectedWorkers.add( selectedId );
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

	//首先基于值进行排序，然后基于序列进行排序；因为有些值之间可能相差不太大
	public HashMap<Integer, ArrayList<ArrayList<String>>> selectionStrategyRankBased ( ArrayList<String> candidateWorkers, LinkedHashMap<String, CrowdWorker> candidateWorkerList, HashMap<String, Double> bugProbList, TestProject project  ){
		ArrayList<String> selectedWorkers = new ArrayList<String>();
		
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
			
			HashMap<Integer, Double> bugProbValueMap = new HashMap<Integer, Double>();
			HashMap<Integer, Double> revValueMap = new HashMap<Integer, Double>();
			HashMap<Integer, Double> divValueMap = new HashMap<Integer, Double>();
			
			for ( int j =0; j < nowCandWorkers.size(); j ++ ) {
				String userId = nowCandWorkers.get( j );
				CrowdWorker worker = candidateWorkerList.get( userId );
				
				double bugProbValue = bugProbList.get( userId );
				double revValue = this.obtainRelevance(worker, project);
				double divValue = this.obtainDiversity(selectedWorkers, worker, candidateWorkerList);
				
				//System.out.println(  "bugProbValue is : " + bugProbValue + " ; revValue is :" + revValue + " divValue is : " + divValue );
				
				bugProbValueMap.put( j, bugProbValue );
				revValueMap.put( j , revValue );
				divValueMap.put( j, divValue );
			}
			
			if ( bugProbValueMap.size() < 1 )
				break;
			
			List<HashMap.Entry<Integer, Double>> bugProbValueList = this.rankHashMap( bugProbValueMap, true );
			List<HashMap.Entry<Integer, Double>> revValueList = this.rankHashMap( revValueMap, true );
			List<HashMap.Entry<Integer, Double>> divValueList = this.rankHashMap( divValueMap, true );
			
			HashMap<Integer, Integer> bugProbRankMap = this.valueRankToRankMap( bugProbValueList );
			HashMap<Integer, Integer> revRankMap = this.valueRankToRankMap( revValueList );
			HashMap<Integer, Integer> divRankMap = this.valueRankToRankMap( divValueList );
			
			HashMap<Integer, Double> mulValueMap = new HashMap<Integer, Double>();
			for ( int j = 0; j < nowCandWorkers.size(); j++ ) {
				//System.out.println( bugProbValueList[j] + " " + revValueList[j] + " " + divValueList[j] );
				
				double multiValue = bugProbWeight * bugProbRankMap.get( j) + revWeight * revRankMap.get(j) + divWeight * divRankMap.get(j);
				
				mulValueMap.put( j, multiValue );
			}
			
			//每次选择5个，否则速度太慢了
			List<HashMap.Entry<Integer, Double>> newMulValueList = this.rankHashMap( mulValueMap, false);
			
			for ( int j =0; j < selectionNumEachIter && j < newMulValueList.size(); j++ ) {
				int selectedIndex = newMulValueList.get(j).getKey();
				String selectedId = nowCandWorkers.get( selectedIndex );
				//System.out.println( "The selected worker is : " + selectedId + " its strategy value is : " + bugProbRankMap.get(selectedIndex) + 
					//	" " + revRankMap.get(selectedIndex) + " " + divRankMap.get( selectedIndex));
				
				selectedWorkers.add( selectedId );
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
	
	//从小到大排序
	public List<HashMap.Entry<Integer, Double>> rankHashMap ( HashMap<Integer, Double> mapItems, boolean isForward ) {
		List<HashMap.Entry<Integer, Double>> newMapItems = new ArrayList<HashMap.Entry<Integer, Double>> ( mapItems.entrySet() );
		Collections.sort( newMapItems, new Comparator<HashMap.Entry<Integer, Double>>() {
			public int compare(HashMap.Entry<Integer, Double> o1, HashMap.Entry<Integer, Double> o2) {   
				if ( isForward )
					return o1.getValue().compareTo(o2.getValue() ) ;
				else
					return o2.getValue().compareTo(o1.getValue() ) ;
		    }
		});	
		
		return newMapItems;
	}
	
	public HashMap<Integer, Integer> valueRankToRankMap ( List<HashMap.Entry<Integer, Double>> newMapItems ) {
		HashMap<Integer, Integer> rankMap = new HashMap<Integer, Integer>();
		for ( int i=0; i < newMapItems.size(); i++) {
			Integer id = newMapItems.get(i).getKey();
			rankMap.put(id, i+1);
		}
		return rankMap;
	}
	
	
	public Double obtainRelevance ( CrowdWorker worker, TestProject project ) {
		SimilarityMeasure simTool = new SimilarityMeasure();
		ArrayList<String> taskTerms = project.getTestTask().getTaskDescription();
		ArrayList<String> workerTerms = worker.getDomainKnInfo().getDomainKnowledge();
		
		Double sim = simTool.cosineSimilarity( taskTerms, workerTerms );
		return sim;
	}
	
	public Integer obtainDiversity ( ArrayList<String> selectedWorkers, CrowdWorker worker, LinkedHashMap<String, CrowdWorker> candidateWorkerList ) {
		HashSet<String> workerInfo = new HashSet<String>();
		workerInfo.addAll( worker.getDomainKnInfo().getDomainKnowledge() );
		workerInfo.add( worker.getPhoneInfo().getPhoneType() );
		workerInfo.add( worker.getPhoneInfo().getOS() );
		workerInfo.add( worker.getPhoneInfo().getISP() );
		workerInfo.add( worker.getPhoneInfo().getNetwork() );
		
		HashSet<String> selectedWorkerInfo = new HashSet<String>();
		for ( int i =0; i < selectedWorkers.size(); i++ ) {
			String userId = selectedWorkers.get( i );
			CrowdWorker seWorker = candidateWorkerList.get( userId );
			
			selectedWorkerInfo.addAll( seWorker.getDomainKnInfo().getDomainKnowledge());
			selectedWorkerInfo.add ( seWorker.getPhoneInfo().getPhoneType());
			selectedWorkerInfo.add( seWorker.getPhoneInfo().getOS() );
			selectedWorkerInfo.add( seWorker.getPhoneInfo().getISP() );
			selectedWorkerInfo.add( seWorker.getPhoneInfo().getNetwork() );
		}
		
		int count = 0;
		for ( String info : workerInfo ) {
			if ( !selectedWorkerInfo.contains( info )) {
				count ++;
			}
		}
		
		return count;
	}
}
