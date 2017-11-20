package com.selectionObjective;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.SortedMap;
import java.util.TreeMap;

import com.TFIDF.TFIDF;
import com.data.Constants;
import com.data.CrowdWorker;
import com.data.Phone;
import com.data.TestCase;
import com.data.TestProject;
import com.data.TestReport;
import com.dataProcess.DataSetPrepare;
import com.testCaseDataPrepare.CrowdWokerExtraction;

public class MainSelectionApproach {
	public void workSelectionApproach ( TestProject project, ArrayList<TestProject> historyProjectList) {
		DataSetPrepare dataTool = new DataSetPrepare ( );
		ArrayList<HashMap<String, Integer>> totalDataSet = dataTool.prepareDataSet( historyProjectList );
		
		TFIDF tfidfTool = new TFIDF();
		ArrayList<String> finalTermList = tfidfTool.obtainFinalTermList(totalDataSet);
		
		CrowdWokerExtraction workerTool = new CrowdWokerExtraction();
		HashMap<String, CrowdWorker> historyWorkerList = workerTool.obtainCrowdWokerInfo( historyProjectList, finalTermList);
		
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

		//obtain the bugProbability of all candidate workers, and store it into related file
		BugProbability probTool = new BugProbability();
		HashMap<String, Double> bugProbWorkerResults = probTool.ObtainBugProbabilityTotal(project, historyProjectList, historyWorkerList, candidateWorkerList);
		this.storeBugProb(bugProbWorkerResults);
	}
	
	public void storeWorkerInfo ( LinkedHashMap<String, CrowdWorker> candidateWorkerList ) {
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( Constants.BUG_PROB_FILE ));
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
	
	public void storeBugProb ( HashMap<String, Double> bugProbWorkerResults ) {
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( Constants.BUG_PROB_FILE ));
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
}
