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
import com.dataProcess.TestProjectReader;
import com.performanceEvaluation.BugDetectionRateEvaluation;
import com.selectionApproach.MultiObjectiveSelection;
import com.selectionApproach.QualityIndicator;
import com.testCaseDataPrepare.CrowdWokerExtraction;

import jmetal.core.SolutionSet;
import jmetal.util.JMException;

public class MainSelectionApproach extends SelectionSchema{
	public void workSelectionApproach ( TestProject project, ArrayList<TestProject> historyProjectList,  String testSetIndex, String taskId ) {
		super.workSelectionApproach(project, historyProjectList, testSetIndex, taskId);
		
		MultiObjectiveSelection selectionTool = new MultiObjectiveSelection( );
		BugDetectionRateEvaluation evaTool = new BugDetectionRateEvaluation();
		
		SolutionSet paretoFroniter;
		try {
			//ArrayList<String> candidateIDs = selectionTool.obtainCandidateIDs();
			paretoFroniter = selectionTool.multiObjectiveWorkerSelection(candidateIDs, 12345L, testSetIndex, taskId, project );
			selectionTool.storeParetoFront(paretoFroniter, Constants.PARETO_FRONT_FOLDER + "/" + taskId + "-front.txt");
			
			//QualityIndicator qualityTool = new QualityIndicator();
			//qualityTool.obtainQualityIndicators(paretoFroniter, taskId );
			
			HashMap<Integer, ArrayList<ArrayList<String>>> selectionResults = selectionTool.obtainWorkerSelectionResults(paretoFroniter, taskId );
			
			evaTool.obtainBugDetectionRate(selectionResults, project, true, "MOCOS" );
		} catch (ClassNotFoundException | JMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	
	public static void main ( String[] args ) {
		MainSelectionApproach selectionApproach = new MainSelectionApproach();
		//selectionApproach.workerSelectionForMultipleProjects( Constants.TEST_SET_INDEX );
		
		//for pareto front
		Integer beginTestProjIndex = 533, endTestProjIndex = 536, testSetIndex = 20;
		TestProjectReader projReader = new TestProjectReader();
		ArrayList<TestProject> historyProjectList = projReader.loadTestProjectAndTaskListBasedId( 1, beginTestProjIndex -1, Constants.TOTAL_PROJECT_FOLDER, Constants.TOTAL_TASK_DES_FOLDER );
		System.out.println( "historyProjectList is : " + historyProjectList.size() );
		
		for ( int i = beginTestProjIndex; i <= endTestProjIndex; i++  ) {
			//System.out.println( "=================================================================\nWorker Selection for project: " + i  );
			TestProject project = projReader.loadTestProjectAndTaskBasedId( i , Constants.TOTAL_PROJECT_FOLDER, Constants.TOTAL_TASK_DES_FOLDER  );
			//selectionApproach.workSelectionApproach(project, historyProjectList, testSetIndex.toString(),  new Integer(i).toString()  );		
			for ( int j = 0; j < 10; j++ )
				selectionApproach.workSelectionApproach(project, historyProjectList, testSetIndex.toString(),  new Integer(i).toString()  );
		}		
	}
}
