package com.baseline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.data.Constants;
import com.data.CrowdWorker;
import com.data.TestProject;
import com.dataProcess.SimilarityMeasure;
import com.mainMOCOS.SelectionSchema;
import com.performanceEvaluation.BugDetectionRateEvaluation;
import com.topicModelData.TopicDataPrepare;

public class TOPICSelectionApproach extends SelectionSchema {
	private int selectionNumEachIter = 5;
	
	public void workSelectionApproach ( TestProject project, ArrayList<TestProject> historyProjectList, String testSetIndex, String taskId ) {
		super.workSelectionApproach(project, historyProjectList, testSetIndex, taskId);
		
		TopicDataPrepare topicTool = new TopicDataPrepare();
		String workerTopicFile = "data/input/topic/topicBaseline/worker_topic_dis.txt"; 
		HashMap<String, ArrayList<Double>> topicDisForWorker = topicTool.loadTopicDistribution( workerTopicFile );
		System.out.println ( "================================ " + topicDisForWorker.size() );
		
		HashMap<Integer, ArrayList<ArrayList<String>>> selectionResults = this.topicDistanceBasedSelection( topicDisForWorker );
		
		BugDetectionRateEvaluation evaTool = new BugDetectionRateEvaluation();
		evaTool.obtainBugDetectionRate(selectionResults, project, true, "baselineTOPIC");
	}		
	
	public HashMap<Integer, ArrayList<ArrayList<String>>> topicDistanceBasedSelection ( HashMap<String, ArrayList<Double>> topicDisForWorker ) {
		ArrayList<String> selectedWorkers = new ArrayList<String>();
		
		//最外层循环只是遍历所有选择的人数
		for ( int i =0; i < candidateIDs.size(); i++ ) {
			//在这轮中，还有哪些candidateWorkers
			ArrayList<String> nowCandWorkers = new ArrayList<String>();
			for ( int j = 0; j < candidateIDs.size(); j++ ) {
				String id = candidateIDs.get( j );
				if ( !selectedWorkers.contains( id )) {
					nowCandWorkers.add( id );
				}
			}
			
			HashMap<Integer, Double> distValueMap = new HashMap<Integer, Double>();
			
			for ( int j =0; j < nowCandWorkers.size(); j ++ ) {
				CrowdWorker worker = candidateWorkerList.get( nowCandWorkers.get( j) );
				double distValue = this.extractDistance( worker, selectedWorkers,  topicDisForWorker );
				//System.out.println(  "distValue is : " + distValue  );
				
				distValueMap.put( j, distValue );
			}
			
			//每次选择5个，否则速度太慢了
			List<HashMap.Entry<Integer, Double>> newDistValueList = new ArrayList<HashMap.Entry<Integer, Double>> ( distValueMap.entrySet() );
			Collections.sort( newDistValueList, new Comparator<HashMap.Entry<Integer, Double>>() {
				public int compare(HashMap.Entry<Integer, Double> o1, HashMap.Entry<Integer, Double> o2) {      
			        return o2.getValue().compareTo(o1.getValue() ) ;
			    }
			});
			
			for ( int j =0; j < selectionNumEachIter && j < newDistValueList.size(); j++ ) {
				int selectedIndex = newDistValueList.get(j).getKey();
				String selectedId = nowCandWorkers.get( selectedIndex );
				//System.out.println( "The selected worker is : " + selectedId + " its strategy value is : " + distValueMap.get( selectedIndex ) );
				
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
	
	//某候选worker和一组已有的selectedWorkers的距离定义为：候选worker和selectedWorkers的所有距离的最小值
	public Double extractDistance ( CrowdWorker worker, ArrayList<String> selectedWorkers, HashMap<String, ArrayList<Double>> topicDisForWorker ) {
		SimilarityMeasure simTool = new SimilarityMeasure();
		
		ArrayList<Double> defaultTopicInfo = new ArrayList<Double>();
		for ( int i =0; i < 30; i ++ ) {
			defaultTopicInfo.add( 0.0 );
		}
		
		double minDist = 10000000000.0;
		String userId = worker.getWorkerId();
		ArrayList<Double> topicInfo = new ArrayList<Double>();
		if ( topicDisForWorker.containsKey( userId )) {
			topicInfo = topicDisForWorker.get( userId );
		}else {
			topicInfo = defaultTopicInfo;
		}
		
		
		for ( int i =0; i < selectedWorkers.size(); i++ ) {
			String userIdSe = selectedWorkers.get( i );
			
			ArrayList<Double> topicSe = new ArrayList<Double>();
			if ( topicDisForWorker.containsKey( userIdSe )) {
				topicSe = topicDisForWorker.get( userIdSe );
			}else {
				topicSe = defaultTopicInfo;
			}			
			
			double distance = simTool.ManhattanDistanceDouble( topicInfo, topicSe );
			
			if ( minDist > distance )
				minDist = distance;
		}
		return minDist;
	}
	
	public static void main ( String[] args ) {
		TOPICSelectionApproach selectionTool = new TOPICSelectionApproach();
		for ( int i =11; i <= 20; i++ ) {
			selectionTool.workerSelectionForMultipleProjects( i );
		}
		//selectionTool.workerSelectionForMultipleProjects( Constants.TEST_SET_INDEX );
	}

}
