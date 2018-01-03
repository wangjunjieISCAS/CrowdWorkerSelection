package com.baseline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.data.CrowdWorker;
import com.data.TestProject;
import com.dataProcess.SimilarityMeasure;
import com.performanceEvaluation.BugDetectionRateEvaluation;


public class STRINGSelectionApproach extends SelectionSchema {
	private int selectionNumEachIter = 5;
	
	public void workSelectionApproach ( TestProject project, ArrayList<TestProject> historyProjectList, String testSetIndex, String taskId ) {
		super.workSelectionApproach(project, historyProjectList, testSetIndex, taskId);
		
		HashMap<Integer, ArrayList<ArrayList<String>>> selectionResults = this.distanceBasedSelection( project);
		
		BugDetectionRateEvaluation evaTool = new BugDetectionRateEvaluation();
		evaTool.obtainBugDetectionRate(selectionResults, project, true, "baselineSTRING");
	}		
	
	public HashMap<Integer, ArrayList<ArrayList<String>>> distanceBasedSelection ( TestProject project ) {
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
				double distValue = this.extractDistance( worker, selectedWorkers);
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
	public Double extractDistance ( CrowdWorker worker, ArrayList<String> selectedWorkers ) {
		SimilarityMeasure simTool = new SimilarityMeasure();
		
		double minDist = 10000000000.0;
		ArrayList<String> domainInfo = worker.getDomainKnInfo().getDomainKnowledge();
		for ( int i =0; i < selectedWorkers.size(); i++ ) {
			String userId = selectedWorkers.get( i );
			CrowdWorker workerSe = candidateWorkerList.get( userId );
			
			ArrayList<String> domainSe = workerSe.getDomainKnInfo().getDomainKnowledge();
			double distance = simTool.ManhattanDistance( domainInfo, domainSe );
			
			if ( minDist > distance )
				minDist = distance;
		}
		return minDist;
	}
	
	public static void main ( String[] args ) {
		STRINGSelectionApproach selectionTool = new STRINGSelectionApproach();
		selectionTool.workerSelectionForMultipleProjects( 20 );
	}
}
