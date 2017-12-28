package com.baseline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.dataProcess.TestProjectReader;
import com.learner.BugProbability;
import com.mainMOCOS.CandidateIDChoose;
import com.performanceEvaluation.BugDetectionRateEvaluation;
import com.testCaseDataPrepare.CrowdWokerExtraction;


public class BugProbSelectionApproach {
	private Integer workerNumThres = 300;
	
	public void workSelectionApproach ( TestProject project, ArrayList<TestProject> historyProjectList, Date curTime, String testSetIndex, String taskId ) {
		CrowdWorkerHandler workerHandler = new CrowdWorkerHandler();
		HashMap<String, CrowdWorker> historyWorkerList = workerHandler.loadCrowdWorkerInfo( Constants.WORKER_INFO_FOLDER + "/" + testSetIndex + "/workerPhone.csv", 
				Constants.WORKER_INFO_FOLDER + "/" + testSetIndex + "/workerCap.csv", Constants.WORKER_INFO_FOLDER + "/" + testSetIndex + "/workerDomain.csv" );
		System.out.println ( "HistoryWorkerList is done! " );
		
		CrowdWokerExtraction workerTool = new CrowdWokerExtraction();
		CrowdWorker defaultWorker = workerTool.obtainDefaultCrowdWorker( historyWorkerList );
		//obtain candidate worker, besides the history worker, there could be worker who join the platform for the first time in this project
		LinkedHashMap<String, CrowdWorker> candidateWorkerList = new LinkedHashMap<String, CrowdWorker>();
		for ( String userId : historyWorkerList.keySet() ) {
			CrowdWorker hisWorker = historyWorkerList.get( userId );
			
			CrowdWorker worker = new CrowdWorker ( hisWorker.getWorkerId(), hisWorker.getPhoneInfo(), hisWorker.getCapInfo(), hisWorker.getDomainKnInfo() );
			candidateWorkerList.put( userId,  worker );
		}
		for ( int i =0; i < project.getTestReportsInProj().size(); i++ ) {
			TestReport report = project.getTestReportsInProj().get(i);
			String userId = report.getUserId();
			if ( candidateWorkerList.containsKey( userId ))
				continue;
			
			Phone phoneInfo = new Phone ( report.getPhoneType(), report.getOS(), report.getNetwork(), report.getISP() );
			CrowdWorker worker = new CrowdWorker ( userId, phoneInfo, defaultWorker.getCapInfo(), defaultWorker.getDomainKnInfo() );
			
			candidateWorkerList.put( userId, worker );
		}
		
		CandidateIDChoose chooseTool = new CandidateIDChoose();
		ArrayList<String> candidateIDs = chooseTool.obtainCandidateIDsBasedLastActivity(historyProjectList, candidateWorkerList, project);
		//ArrayList<String> candidateIDs = chooseTool.obtainCandidateIDsSpecificTask(candidateWorkerList, project);
		System.out.println ( "CandidateWorkerList is done! " );
		
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
	
	public void workerSelectionForMultipleProjects ( Integer testSetIndex ) {
		int beginTestProjIndex =0, endTestProjIndex = 0;
		Date closeTime = null;
		SimpleDateFormat formatLine = new SimpleDateFormat ("yyyy/MM/dd HH:mm");
		
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
					closeTime = formatLine.parse(  temp[3] );
					
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
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TestProjectReader projReader = new TestProjectReader();
		ArrayList<TestProject> historyProjectList = projReader.loadTestProjectAndTaskListBasedId( 1, beginTestProjIndex -1, Constants.TOTAL_PROJECT_FOLDER, Constants.TOTAL_TASK_DES_FOLDER );
		System.out.println( "historyProjectList is : " + historyProjectList.size() );
		
		for ( int i = beginTestProjIndex; i <= endTestProjIndex; i++  ) {
			System.out.println( "=================================================================\nWorker Selection for project: " + i  );
			TestProject project = projReader.loadTestProjectAndTaskBasedId( i , Constants.TOTAL_PROJECT_FOLDER, Constants.TOTAL_TASK_DES_FOLDER  );
			this.workSelectionApproach(project, historyProjectList, closeTime, testSetIndex.toString(),  new Integer(i).toString()  );
		}		
	}
	
	public static void main ( String[] args ) {
		BugProbSelectionApproach selectionTool = new BugProbSelectionApproach();
		selectionTool.workerSelectionForMultipleProjects( 20 );
	}
}
