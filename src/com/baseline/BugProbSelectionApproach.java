package com.baseline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.data.Constants;
import com.data.CrowdWorker;
import com.data.Phone;
import com.data.TestProject;
import com.data.TestReport;
import com.dataProcess.CrowdWorkerHandler;
import com.learner.BugProbability;
import com.mainMOCOS.CandidateIDChoose;
import com.mainMOCOS.SelectionSchema;
import com.performanceEvaluation.BugDetectionRateEvaluation;
import com.testCaseDataPrepare.CrowdWokerExtraction;


public class BugProbSelectionApproach extends SelectionSchema{
	private Integer workerNumThres = 300;
	
	public void workSelectionApproach ( TestProject project, ArrayList<TestProject> historyProjectList, Date curTime, String testSetIndex, String taskId ) {
		super.workSelectionApproach(project, historyProjectList, testSetIndex, taskId);
		
		BugProbability probTool = new BugProbability();
		String bugProbFile = Constants.BUG_PROB_FOLDER + "/" + testSetIndex + "/" + taskId + "-bugProbability.csv" ;
		HashMap<String, Double> bugProb = probTool.loadBugProbability( bugProbFile );
		
		HashMap<Integer, ArrayList<ArrayList<String>>> selectionResults = this.selectBasedBugProb(bugProb, candidateIDs);
		BugDetectionRateEvaluation evaTool = new BugDetectionRateEvaluation();
		evaTool.obtainBugDetectionRate(selectionResults, project, true, "baselineBugProb" );
	}
	
	public HashMap<Integer, ArrayList<ArrayList<String>>> selectBasedBugProb ( HashMap<String, Double> bugProb, ArrayList<String> candidateIDs ) {
		HashMap<Integer, ArrayList<ArrayList<String>>> selectionResults = new HashMap<Integer, ArrayList<ArrayList<String>>>();
		
		HashMap<String, Double> candBugProb = new HashMap<String, Double>();
		for ( int i =0; i < candidateIDs.size(); i++ ) {
			String userId = candidateIDs.get( i );
			Double prob = bugProb.get( userId );
			candBugProb.put( userId , prob );
		}
		List<HashMap.Entry<String, Double>> newCandBugProb = new ArrayList<HashMap.Entry<String, Double>>(candBugProb.entrySet());

		Collections.sort( newCandBugProb, new Comparator<HashMap.Entry<String, Double>>() {   
			public int compare(HashMap.Entry<String, Double> o1, HashMap.Entry<String, Double> o2) {      
			        //return (o2.getValue() - o1.getValue()); 
			        return o2.getValue().compareTo(o1.getValue() ) ;
			    }
			}); 
		
		for ( int i =0; i < newCandBugProb.size() && i < workerNumThres ; i++ ) {
			int selectionNum = i+1;
			ArrayList<String> results = new ArrayList<String>();
			for ( int j =0; j <=i; j++ ) {
				results.add( newCandBugProb.get(j).getKey() );
			}
			ArrayList<ArrayList<String>> resultForI = new ArrayList<ArrayList<String>>();
			resultForI.add( results );
			
			selectionResults.put( selectionNum, resultForI );
		}
		
		return selectionResults;
	}
	
	
	public static void main ( String[] args ) {
		BugProbSelectionApproach selectionTool = new BugProbSelectionApproach();
		selectionTool.workerSelectionForMultipleProjects( Constants.TEST_SET_INDEX  );
	}
}
