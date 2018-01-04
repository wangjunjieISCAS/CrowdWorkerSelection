package com.mainMOCOS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.data.Constants;
import com.data.CrowdWorker;
import com.data.Phone;
import com.data.TestProject;
import com.data.TestReport;
import com.dataProcess.CrowdWorkerHandler;
import com.performanceEvaluation.BugDetectionRateEvaluation;
import com.selectionApproach.MultiObjectiveSelection;
import com.testCaseDataPrepare.CrowdWokerExtraction;

import jmetal.core.SolutionSet;
import jmetal.util.JMException;

public class MainSelectionApproach extends SelectionSchema{
	public void workSelectionApproach ( TestProject project, ArrayList<TestProject> historyProjectList,  String testSetIndex, String taskId ) {
		super.workSelectionApproach(project, historyProjectList, testSetIndex, taskId);
		
		MultiObjectiveSelection selectionTool = new MultiObjectiveSelection();
		BugDetectionRateEvaluation evaTool = new BugDetectionRateEvaluation();
		
		SolutionSet paretoFroniter;
		try {
			//ArrayList<String> candidateIDs = selectionTool.obtainCandidateIDs();
			paretoFroniter = selectionTool.multiObjectiveWorkerSelection(candidateIDs, 12345L, testSetIndex, taskId, project );
			HashMap<Integer, ArrayList<ArrayList<String>>> selectionResults = selectionTool.obtainWorkerSelectionResults(paretoFroniter, taskId );
			
			evaTool.obtainBugDetectionRate(selectionResults, project, true, "MOCOS" );
		} catch (ClassNotFoundException | JMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	
	public static void main ( String[] args ) {
		MainSelectionApproach selectionApproach = new MainSelectionApproach();
		selectionApproach.workerSelectionForMultipleProjects( Constants.TEST_SET_INDEX );
		
	}
}
