package com.baseline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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


public class COMPASCSelectionApproach {
	public void COMPASCWorkSelectionApproach ( TestProject project, ArrayList<TestProject> historyProjectList, String testSetIndex, String taskId ) {
		MainSelectionApproach candWorkerTool = new MainSelectionApproach();
		LinkedHashMap<String, CrowdWorker> candidateWorkerList  = candWorkerTool.obtainCandidateWorkers(project, historyProjectList, testSetIndex);
		
		CandidateIDChoose chooseTool = new CandidateIDChoose();
		ArrayList<String> candidateIDs = chooseTool.obtainCandidateIDsBasedLastActivity(historyProjectList, candidateWorkerList, project);
		//ArrayList<String> candidateIDs = chooseTool.obtainCandidateIDsRandom(candidateWorkerList);
		
		System.out.println ( "CandidateIDs size is: " + candidateIDs.size() ); 	
		
		COMPASCSelectionStrategy selectionTool = new COMPASCSelectionStrategy();
		HashMap<Integer, ArrayList<ArrayList<String>>> selectionResults = selectionTool.multipleStrategySelection(candidateWorkerList, candidateIDs, project);
		
		BugDetectionRateEvaluation evaTool = new BugDetectionRateEvaluation();
		evaTool.obtainBugDetectionRate(selectionResults, project, true, "baselineCOMPASC");
	}	
	
	public void COMPASCWorkerSelectionForMultipleProjects ( Integer testSetIndex ) {
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
			this.COMPASCWorkSelectionApproach(project, historyProjectList, testSetIndex.toString(),  new Integer(i).toString()  );
		}		
	}
	
	public static void main ( String[] args ) {
		COMPASCSelectionApproach selectionTool = new COMPASCSelectionApproach();
		selectionTool.COMPASCWorkerSelectionForMultipleProjects( 20 );
	}
}