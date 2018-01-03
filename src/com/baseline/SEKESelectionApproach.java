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


public class SEKESelectionApproach {
	public void SEKEWorkSelectionApproach ( TestProject project, ArrayList<TestProject> historyProjectList, String testSetIndex, String taskId ) {
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
	
	public void SEKEWorkerSelectionForMultipleProjects ( Integer testSetIndex ) {
		int beginTestProjIndex =0, endTestProjIndex = 0;
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader( new File ( Constants.TRAIN_TEST_SET_SETTING_FILE)));
			String line = "";
			
			boolean isFirstLine = true;
			while ( ( line = br.readLine() ) != null ) {
				if ( isFirstLine == true ) {
					isFirstLine = false;
					continue;
				}
				String[] temp = line.split( ",");
				Integer testSetNum = Integer.parseInt( temp[0] );
				if ( testSetNum.equals( testSetIndex )) {
					beginTestProjIndex = Integer.parseInt(  temp[1]);
					endTestProjIndex = Integer.parseInt(  temp[2] );
					//closeTime = formatLine.parse(  temp[3] );
					
					break;
				}
			}			
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		TestProjectReader projReader = new TestProjectReader();
		ArrayList<TestProject> historyProjectList = projReader.loadTestProjectAndTaskListBasedId( 1, beginTestProjIndex -1, Constants.TOTAL_PROJECT_FOLDER, Constants.TOTAL_TASK_DES_FOLDER );
		System.out.println( "historyProjectList is : " + historyProjectList.size() );
		
		for ( int i = beginTestProjIndex; i <= endTestProjIndex; i++  ) {
			System.out.println( "=================================================================\nWorker Selection for project: " + i  );
			TestProject project = projReader.loadTestProjectAndTaskBasedId( i , Constants.TOTAL_PROJECT_FOLDER, Constants.TOTAL_TASK_DES_FOLDER  );
			this.SEKEWorkSelectionApproach(project, historyProjectList, testSetIndex.toString(),  new Integer(i).toString()  );
		}		
	}
	
	public static void main ( String[] args ) {
		SEKESelectionApproach selectionApproach = new SEKESelectionApproach();
		selectionApproach.SEKEWorkerSelectionForMultipleProjects( 20 );
	}
}
