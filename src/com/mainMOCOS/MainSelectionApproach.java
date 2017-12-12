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
import java.util.Random;
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
import com.performanceEvaluation.BugDetectionRateEvaluation;
import com.performanceEvaluation.ProbPredictEvaluation;
import com.selectionApproach.MultiObjectiveSelection;
import com.taskReverse.FinalTermListGeneration;
import com.testCaseDataPrepare.CrowdWokerExtraction;

import jmetal.core.SolutionSet;
import jmetal.util.JMException;

public class MainSelectionApproach {
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
		//workerHandler.storeCrowdWorkerInfo(candidateWorkerList, Constants.WORKER_PHONE_FILE, Constants.WORKER_CAP_FILE, Constants.WORKER_DOMAIN_FILE );
		System.out.println ( "CandidateWorkerList is done! " );
		
		CandidateIDChoose chooseTool = new CandidateIDChoose();
		String bugProbFile = Constants.BUG_PROB_FOLDER + "/" + taskId + "-bugProbability.csv" ;
		//ArrayList<String> candidateIDs = chooseTool.obtainCandidateIDsRandom(candidateWorkerList);
		ArrayList<String> candidateIDs = chooseTool.obtainCandidateIDsBugProb(candidateWorkerList, bugProbFile );
		//ArrayList<String> candidateIDs = chooseTool.obtainCandidateIDsBugProbAndTask(candidateWorkerList, bugProbFile, project);
		//ArrayList<String> candidateIDs = chooseTool.obtainCandidateIDsSpecificTask(candidateWorkerList, project);
		System.out.println ( "CandidateIDs size is: " + candidateIDs.size() ); 
		
		
		MultiObjectiveSelection selectionTool = new MultiObjectiveSelection();
		BugDetectionRateEvaluation evaTool = new BugDetectionRateEvaluation();
		
		SolutionSet paretoFroniter;
		try {
			//ArrayList<String> candidateIDs = selectionTool.obtainCandidateIDs();
			paretoFroniter = selectionTool.multiObjectiveWorkerSelection(candidateIDs, 12345L, testSetIndex, taskId );
			HashMap<Integer, ArrayList<ArrayList<String>>> selectionResults = selectionTool.obtainWorkerSelectionResults(paretoFroniter, taskId );
			
			evaTool.obtainBugDetectionRate(selectionResults, project);
		} catch (ClassNotFoundException | JMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	public static void main ( String[] args ) {
		//Math.sqrt(b)*random.nextGaussian()+a
		Random random = new Random();
		double a = 0.0;
		double b = 100;
		ArrayList<Double> randValues = new ArrayList<Double>();
		for ( int i =0; i < 2335; i++ ) {
			double value = Math.sqrt(b)*random.nextGaussian()+a;
			randValues.add( value );
		}
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( "data/output/test.csv" ));
			for ( int i =0; i < randValues.size(); i++ ) {
				writer.write( randValues.get( i ).toString() );
				writer.newLine();
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		SimpleDateFormat formatLine = new SimpleDateFormat ("yyyy/MM/dd HH:mm:ss");
		Date curTime = null;
		try {
			curTime = formatLine.parse( "2016/8/4 10:00:00" );
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		TestProjectReader projReader = new TestProjectReader();
		ArrayList<TestProject> historyProjectList = projReader.loadTestProjectAndTaskList( Constants.TOTAL_PROJECT_FOLDER, Constants.TOTAL_TASK_DES_FOLDER );
		TestProject project = projReader.loadTestProjectAndTask( "data/input/experimental dataset/560-76-bug好运一元V1.0测试_1470654272.csv", "data/input/taskDescription/560-76-bug好运一元V1.0测试_1470654272.txt");
		
		MainSelectionApproach selectionApproach = new MainSelectionApproach();
		selectionApproach.workSelectionApproach(project, historyProjectList, curTime, "20", "560" );
	}
}
