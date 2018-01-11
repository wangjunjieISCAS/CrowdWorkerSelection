package com.baseline;

import java.util.ArrayList;
import java.util.HashMap;

import com.data.Constants;
import com.data.TestProject;
import com.mainMOCOS.SelectionSchema;
import com.performanceEvaluation.BugDetectionRateEvaluation;

public class ISSRESelectionApproach extends SelectionSchema {
	public void workSelectionApproach ( TestProject project, ArrayList<TestProject> historyProjectList, String testSetIndex, String taskId ) {
		super.workSelectionApproach(project, historyProjectList, testSetIndex, taskId);
		
		ISSRESelectionStrategy selectionTool = new ISSRESelectionStrategy();
		HashMap<Integer, ArrayList<ArrayList<String>>> selectionResults = selectionTool.scoreBasedSelection(candidateWorkerList, candidateIDs, project);
		
		BugDetectionRateEvaluation evaTool = new BugDetectionRateEvaluation();
		evaTool.obtainBugDetectionRate(selectionResults, project, true, "baselineISSRE");
	}
	
	public static void main ( String[] args ) {
		ISSRESelectionApproach selectionTool = new ISSRESelectionApproach();
		for ( int i =11; i <= 20; i++ ) {
			selectionTool.workerSelectionForMultipleProjects( i );
		}
		//selectionTool.workerSelectionForMultipleProjects( Constants.TEST_SET_INDEX  );
	}
}
