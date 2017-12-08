package com.mainMOCOS;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
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
import com.dataProcess.DataSetPrepare;
import com.dataProcess.TestProjectReader;
import com.performanceEvaluation.ProbPredictEvaluation;
import com.selectionObjective.BugProbability;
import com.taskReverse.FinalTermListGeneration;
import com.testCaseDataPrepare.CrowdWokerExtraction;

public class MainSelectionApproach {
	public void workSelectionApproach ( TestProject project, ArrayList<TestProject> historyProjectList) {
		FinalTermListGeneration termTool = new FinalTermListGeneration();
		ArrayList<String> finalTermList = termTool.loadFinalTermList();
		System.out.println ( "FinalTermList is done!");
		
		CrowdWokerExtraction workerTool = new CrowdWokerExtraction();
		HashMap<String, CrowdWorker> historyWorkerList = workerTool.obtainCrowdWokerInfo( historyProjectList, finalTermList);
		System.out.println ( "HistoryWorkerList is done!");
		//this.storeWorkerInfo(  historyWorkerList );
		
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
		this.storeWorkerInfo(candidateWorkerList);
		System.out.println ( "CandidateWorkerList is done!");
		
		//obtain the bugProbability of all candidate workers, and store it into related file
		String projectName = project.getProjectName();
		BugProbability probTool = new BugProbability( projectName );
		HashMap<String, Double> bugProbWorkerResults = probTool.ObtainBugProbabilityTotal(project, historyProjectList, historyWorkerList, candidateWorkerList);
		this.storeBugProb(bugProbWorkerResults, "data/output/bugProb/bugProbability-" + projectName + ".csv" );
		
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
	
	public void storeWorkerInfo ( LinkedHashMap<String, CrowdWorker> candidateWorkerList ) {
		try {
			BufferedWriter phoneWriter = new BufferedWriter( new FileWriter ( Constants.WORKER_PHONE_FILE ));
			BufferedWriter capWriter = new BufferedWriter( new FileWriter ( Constants.WORKER_CAP_FILE ));
			BufferedWriter domainWriter = new BufferedWriter( new FileWriter ( Constants.WORKER_DOMAIN_FILE ));
			
			for ( String userId : candidateWorkerList.keySet() ) {
				CrowdWorker worker = candidateWorkerList.get( userId );
				
				phoneWriter.write( userId + ",");
				Phone phoneInfo = worker.getPhoneInfo();
				phoneWriter.write( phoneInfo.getPhoneType() + "," );
				phoneWriter.write( phoneInfo.getOS() + ",");
				phoneWriter.write( phoneInfo.getNetwork() + "," );
				phoneWriter.write( phoneInfo.getISP() );
				phoneWriter.newLine();
				
				capWriter.write( userId + ",");
				Capability capInfo = worker.getCapInfo();
				capWriter.write( capInfo.getNumProject() + ",");
				capWriter.write( capInfo.getNumReport() +",");
				capWriter.write( capInfo.getNumBug() +",");
				capWriter.write( capInfo.getPercBug().toString() );
				capWriter.newLine();
				
				domainWriter.write( userId + ",");
				DomainKnowledge domainInfo = worker.getDomainKnInfo();
				for ( int i =0; i < domainInfo.getDomainKnowledge().size(); i++ ) {
					domainWriter.write( domainInfo.getDomainKnowledge().get( i ) + ",");
				}
				domainWriter.newLine();
			}
			
			phoneWriter.flush();
			phoneWriter.close();
			
			capWriter.flush();
			capWriter.close();
			
			domainWriter.flush();
			domainWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void storeBugProb ( HashMap<String, Double> bugProbWorkerResults, String fileName ) {
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( fileName ));
			for ( String userId: bugProbWorkerResults.keySet() ) {
				Double prob = bugProbWorkerResults.get( userId );
				
				writer.write( userId + "," + prob);
				writer.newLine();
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static void main ( String[] args ) {
		ArrayList<String> test = new ArrayList<String>();
		test.add( "距离");
		test.add( "大小");
		test.add( "距不离");
		test.add( "没有大小");
		test.add( "没有关系");
		Collections.sort( test );
		System.out.println( test );
		
		TestProjectReader projReader = new TestProjectReader();
		//ArrayList<TestProject> historyProjectList = projReader.loadTestProjectAndTaskList( "data/input/baidu-crowdsourcing-2016.5.24", "data/input/taskDescription");
		//TestProject project = projReader.loadTestProjectAndTask( "data/input/竹兜育儿测试_1463737902.csv", "data/input/竹兜育儿测试_1463737902.txt");
		
		//MainSelectionApproach selectionApproach = new MainSelectionApproach();
		//selectionApproach.workSelectionApproach(project, historyProjectList);
	}
}
