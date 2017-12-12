package com.mainMOCOS;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import com.data.Constants;
import com.data.CrowdWorker;
import com.data.TestProject;
import com.data.TestReport;
import com.learner.BugProbability;

/*
 * 由于candidateWorkerList有2000多个workers，这里可以过滤一下，只选择部分人作为candidate IDs，进入到multi objective selection
 */
public class CandidateIDChoose {
	private int selectionCount;
	
	public CandidateIDChoose ( ) {
		this.selectionCount = 1000;
	}
	
	public ArrayList<String> obtainCandidateIDs ( LinkedHashMap<String, CrowdWorker> candidateWorkerList ) {
		ArrayList<String> candidateIDs = new ArrayList<String>();
		for ( String userId : candidateWorkerList.keySet() ) {
			candidateIDs.add( userId );
		}
		return candidateIDs;
	}
	
	public ArrayList<String> obtainCandidateIDsRandom ( LinkedHashMap<String, CrowdWorker> candidateWorkerList ){
		ArrayList<String> candidateIDs = new ArrayList<String>();
		
		double thres = 1.0 / ( (1.0*candidateWorkerList.size()) / (1.0*selectionCount) ) ;
		Random rand = new Random();
		for ( String userId : candidateWorkerList.keySet() ) {
			Double isSelect = rand.nextDouble();
			if ( isSelect < thres ){
				candidateIDs.add( userId );
			}
		}
		return candidateIDs;
	}
	
	//只选择在当前test task中出现的candidate ids
	public ArrayList<String> obtainCandidateIDsSpecificTask ( LinkedHashMap<String, CrowdWorker> candidateWorkerList , TestProject project ){
		ArrayList<String> candidateIDs = new ArrayList<String>();
		for ( int i =0; i < project.getTestReportsInProj().size(); i++) {
			TestReport report = project.getTestReportsInProj().get( i );
			String userId = report.getUserId();
			if ( !candidateIDs.contains( userId )) {
				candidateIDs.add( userId );
			}
		}
		return candidateIDs;
	}
	
	public ArrayList<String> obtainCandidateIDsBugProb ( LinkedHashMap<String, CrowdWorker> candidateWorkerList, String bugProbFile ){
		ArrayList<String> candidateIDs = new ArrayList<String>();
		
		BugProbability probTool = new BugProbability();
		HashMap<String, Double> bugProb = probTool.loadBugProbability( bugProbFile );
		
		List<HashMap.Entry<String, Double>> bugProbList = new ArrayList<HashMap.Entry<String, Double>>( bugProb.entrySet() );

		Collections.sort( bugProbList, new Comparator<HashMap.Entry<String, Double>>() {   
			public int compare(HashMap.Entry<String, Double> o1, HashMap.Entry<String, Double> o2) {      
			        //return (o2.getValue() - o1.getValue()); 
			        return o2.getValue().compareTo(o1.getValue() ) ;
			    }
			}); 
		
		for ( int i =0; i < bugProbList.size() &&  i < selectionCount ; i++ ) {
			String userId = bugProbList.get( i ).getKey();
			candidateIDs.add( userId );
		}
		
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( "data/output/selectedBugProb.csv" ));
			for ( int i =0; i < candidateIDs.size(); i++ ) {
				String id = candidateIDs.get( i );
				writer.write( id + "," + bugProb.get( id));
				writer.newLine();
			}
			writer.flush();
			
			writer.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return candidateIDs;
	}
	
	public ArrayList<String> obtainCandidateIDsBugProbAndTask ( LinkedHashMap<String, CrowdWorker> candidateWorkerList, String bugProbFile, TestProject project ){
		ArrayList<String> candidateIDs = new ArrayList<String>();
		
		BugProbability probTool = new BugProbability();
		HashMap<String, Double> bugProb = probTool.loadBugProbability( bugProbFile );
		
		List<HashMap.Entry<String, Double>> bugProbList = new ArrayList<HashMap.Entry<String, Double>>( bugProb.entrySet() );

		Collections.sort( bugProbList, new Comparator<HashMap.Entry<String, Double>>() {   
			public int compare(HashMap.Entry<String, Double> o1, HashMap.Entry<String, Double> o2) {      
			        //return (o2.getValue() - o1.getValue()); 
			        return o2.getValue().compareTo(o1.getValue() ) ;
			    }
			}); 
		
		for ( int i =0; i < bugProbList.size() &&  i < selectionCount ; i++ ) {
			String userId = bugProbList.get( i ).getKey();
			candidateIDs.add( userId );
		}
		ArrayList<String> taskWorkers = this.obtainCandidateIDsSpecificTask(candidateWorkerList, project);
		for ( int i =0; i < taskWorkers.size(); i++ ) {
			String id = taskWorkers.get( i );
			if ( candidateIDs.contains( id ))
				continue;
			candidateIDs.add( id );
		}
		
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( "data/output/selectedBugProb.csv" ));
			for ( int i =0; i < candidateIDs.size(); i++ ) {
				String id = candidateIDs.get( i );
				writer.write( id + "," + bugProb.get( id));
				writer.newLine();
			}
			writer.flush();
			
			writer.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return candidateIDs;
	}
}
