package com.baseline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.data.Constants;
import com.data.CrowdWorker;
import com.data.TestProject;
import com.dataProcess.TestProjectReader;
import com.mainMOCOS.CandidateIDChoose;
import com.mainMOCOS.MainSelectionApproach;
import com.performanceEvaluation.BugDetectionRateEvaluation;
import com.selectionApproach.SEKEMultiObjectiveSelection;

import jmetal.core.SolutionSet;
import jmetal.util.JMException;


public class SEKESelectionApproach extends SelectionSchema{
	public void workSelectionApproach ( TestProject project, ArrayList<TestProject> historyProjectList, String testSetIndex, String taskId ) {
		MainSelectionApproach candWorkerTool = new MainSelectionApproach();
		LinkedHashMap<String, CrowdWorker> candidateWorkerList  = candWorkerTool.obtainCandidateWorkers(project, historyProjectList, testSetIndex);
		
		CandidateIDChoose chooseTool = new CandidateIDChoose();
		ArrayList<String> candidateIDs = chooseTool.obtainCandidateIDsBasedLastActivity(historyProjectList, candidateWorkerList, project);
		
		System.out.println ( "CandidateIDs size is: " + candidateIDs.size() ); 	
		
		SEKEMultiObjectiveSelection selectionTool = new SEKEMultiObjectiveSelection();
		BugDetectionRateEvaluation evaTool = new BugDetectionRateEvaluation();
		
		SolutionSet paretoFroniter;
		try {
			paretoFroniter = selectionTool.multiObjectiveWorkerSelection(candidateIDs, 12345L, testSetIndex, taskId, project );
			HashMap<Integer, ArrayList<ArrayList<String>>> selectionResults = selectionTool.obtainWorkerSelectionResults(paretoFroniter, taskId );
			
			evaTool.obtainBugDetectionRate(selectionResults, project, true, "baselineSEKE" );
		} catch (ClassNotFoundException | JMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	
	public static void main ( String[] args ) {
		SEKESelectionApproach selectionApproach = new SEKESelectionApproach();
		selectionApproach.workerSelectionForMultipleProjects( 20 );
	}
}
