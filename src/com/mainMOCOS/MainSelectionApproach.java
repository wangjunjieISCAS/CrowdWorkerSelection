package com.mainMOCOS;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.SortedMap;
import java.util.TreeMap;

import com.TFIDF.TFIDF;
import com.data.Capability;
import com.data.Constants;
import com.data.CrowdWorker;
import com.data.DomainKnowledge;
import com.data.Phone;
import com.data.TestCase;
import com.data.TestProject;
import com.data.TestReport;
import com.dataProcess.CrowdWorkerHandler;
import com.dataProcess.DataSetPrepare;
import com.dataProcess.TestProjectReader;
import com.learner.BugProbability;
import com.performanceEvaluation.ProbPredictEvaluation;
import com.taskReverse.FinalTermListGeneration;
import com.testCaseDataPrepare.CrowdWokerExtraction;

public class MainSelectionApproach {
	public void workSelectionApproach ( TestProject project, ArrayList<TestProject> historyProjectList) {
		FinalTermListGeneration termTool = new FinalTermListGeneration();
		ArrayList<String> finalTermList = termTool.loadFinalTermList();
		System.out.println ( "FinalTermList is done!");
		
		CrowdWorkerHandler workerHandler = new CrowdWorkerHandler();
		
		SimpleDateFormat formatLine = new SimpleDateFormat ("yyyy/MM/dd HH:mm:ss");
		Date curTime = null;
		try {
			curTime = formatLine.parse( "2016/2/20  21:54:00" );
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		CrowdWokerExtraction workerTool = new CrowdWokerExtraction();
		HashMap<String, CrowdWorker> historyWorkerList = workerTool.obtainCrowdWokerInfo( historyProjectList, finalTermList, curTime );
		System.out.println ( "HistoryWorkerList is done! " + System.currentTimeMillis()/1000 );
		//workerHandler.storeCrowdWorkerInfo(  historyWorkerList, Constants.WORKER_PHONE_FILE, Constants.WORKER_CAP_FILE, Constants.WORKER_DOMAIN_FILE );
		
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
		workerHandler.storeCrowdWorkerInfo(candidateWorkerList, Constants.WORKER_PHONE_FILE, Constants.WORKER_CAP_FILE, Constants.WORKER_DOMAIN_FILE );
		System.out.println ( "CandidateWorkerList is done! " + + System.currentTimeMillis()/1000);
		
		//obtain the bugProbability of all candidate workers, and store it into related file
		String projectName = project.getProjectName();
		BugProbability probTool = new BugProbability( projectName );
		HashMap<String, Double> bugProbWorkerResults = probTool.ObtainBugProbabilityTotal(project, historyProjectList, historyWorkerList, candidateWorkerList);
		probTool.storeBugProb(bugProbWorkerResults, "data/output/bugProb/bugProbability-" + projectName + ".csv" );
		
		ProbPredictEvaluation evaluationTool = new ProbPredictEvaluation();
		Double[] performance = evaluationTool.obtainProbPredictionPerformance(bugProbWorkerResults, project);
		
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( Constants.BUG_PROB_PERFORMANCE , true));
			for ( int i =0; i < performance.length; i++ ) {
				writer.write( performance[i] + ",");
			}
			writer.newLine();
			writer.flush();
			
			writer.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		System.out.println ( "bugProbWorkerResults is done!");
	}	
	
	
	public static void main ( String[] args ) {
		TestProjectReader projReader = new TestProjectReader();
		ArrayList<TestProject> historyProjectList = projReader.loadTestProjectAndTaskList( "data/input/total crowdsourced reports", "data/input/taskDescription");
		TestProject project = projReader.loadTestProjectAndTask( "data/input/Öñ¶µÓý¶ù²âÊÔ_1463737902.csv", "data/input/Öñ¶µÓý¶ù²âÊÔ_1463737902.txt");
		
		MainSelectionApproach selectionApproach = new MainSelectionApproach();
		selectionApproach.workSelectionApproach(project, historyProjectList);
	}
}
