package com.baseline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.data.Constants;
import com.data.CrowdWorker;
import com.data.TestProject;
import com.mainMOCOS.CandidateIDChoose;
import com.mainMOCOS.MainSelectionApproach;
import com.mainMOCOS.SelectionSchema;
import com.performanceEvaluation.BugDetectionRateEvaluation;


public class COMPASCSelectionApproach extends SelectionSchema{
	public void workSelectionApproach ( TestProject project, ArrayList<TestProject> historyProjectList, String testSetIndex, String taskId ) {
		super.workSelectionApproach(project, historyProjectList, testSetIndex, taskId);
		
		COMPASCSelectionStrategy selectionTool = new COMPASCSelectionStrategy();
		HashMap<Integer, ArrayList<ArrayList<String>>> selectionResults = selectionTool.multipleStrategySelection(candidateWorkerList, candidateIDs, project);
		
		BugDetectionRateEvaluation evaTool = new BugDetectionRateEvaluation();
		evaTool.obtainBugDetectionRate(selectionResults, project, true, "baselineCOMPASC");
	}	
	
	
	public static void main ( String[] args ) {
		COMPASCSelectionApproach selectionTool = new COMPASCSelectionApproach();
		for ( int i =11; i <= 20; i++ ) {
			selectionTool.workerSelectionForMultipleProjects( i );
		}
		
		//selectionTool.workerSelectionForMultipleProjects( Constants.TEST_SET_INDEX );
	}
}
