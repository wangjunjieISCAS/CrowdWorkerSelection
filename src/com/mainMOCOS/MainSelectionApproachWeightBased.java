package com.mainMOCOS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.data.Constants;
import com.data.CrowdWorker;
import com.data.TestProject;
import com.learner.BugProbability;
import com.performanceEvaluation.BugDetectionRateEvaluation;


public class MainSelectionApproachWeightBased extends SelectionSchema{
	
	public void workSelectionApproach ( TestProject project, ArrayList<TestProject> historyProjectList,  String testSetIndex, String taskId ) {
		String type = "MOCOSWeight";
		
		super.workSelectionApproach(project, historyProjectList, testSetIndex, taskId);
		
		BugProbability probTool = new BugProbability();
		String bugProbFile = Constants.BUG_PROB_FOLDER + "/" + testSetIndex + "/" + taskId + "-bugProbability.csv" ;		
		HashMap<String, Double> bugProb = probTool.loadBugProbability( bugProbFile );
		
		WeightSelectionStrategy selectionTool = new WeightSelectionStrategy();
		//HashMap<Integer, ArrayList<ArrayList<String>>> selectionResults = this.selectionStrategy( candidateIDs, candidateWorkerList, bugProb, project );
		HashMap<Integer, ArrayList<ArrayList<String>>> selectionResults = selectionTool.selectionStrategyRankBased(candidateIDs, candidateWorkerList, bugProb, project);
		
		this.storeSelectionResults(selectionResults, taskId, type );
		
		BugDetectionRateEvaluation evaTool = new BugDetectionRateEvaluation();
		evaTool.obtainBugDetectionRate(selectionResults, project, true, type );		
	}	
	
	
	public static void main ( String args[] ) {
		MainSelectionApproachWeightBased selectionTool = new MainSelectionApproachWeightBased();
		for ( int i =19; i <= 20; i++ ) {
			selectionTool.workerSelectionForMultipleProjects( i );
		}
		//selectionTool.workerSelectionForMultipleProjects( Constants.TEST_SET_INDEX );
	}
}
