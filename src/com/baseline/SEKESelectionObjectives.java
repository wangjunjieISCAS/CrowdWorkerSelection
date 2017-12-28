package com.baseline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.SortedMap;

import com.data.Constants;
import com.data.CrowdWorker;
import com.data.DomainKnowledge;
import com.data.TestProject;
import com.dataProcess.CrowdWorkerHandler;

public class SEKESelectionObjectives {
	private HashMap<String, CrowdWorker> candidateWorkers;
	private TestProject project;
	
	public SEKESelectionObjectives ( String testSetIndex, String taskId, TestProject project ) {
		CrowdWorkerHandler workerTool = new CrowdWorkerHandler();
		candidateWorkers = workerTool.loadCrowdWorkerInfo( Constants.WORKER_INFO_FOLDER + "/" + testSetIndex + "/workerPhone.csv", 
				Constants.WORKER_INFO_FOLDER + "/" + testSetIndex + "/workerCap.csv", Constants.WORKER_INFO_FOLDER + "/" + testSetIndex + "/workerDomain.csv" );
		
		this.project = project;
	}
	
	
	public Double extractReqCoverage ( SortedMap<String, Boolean> selection ) {
		double score = 0.0;
		
		ArrayList<String> reqTermList = project.getTestTask().getTaskDescription();
		ArrayList<String> workerTermList = new ArrayList<String>();

		for (String userId : selection.keySet()) {
			Boolean isSelect = selection.get(userId);
			if (isSelect == false) {
				continue;
			}
			if ( !candidateWorkers.containsKey( userId )) {
				continue;
			}
			
			DomainKnowledge domain = candidateWorkers.get( userId ).getDomainKnInfo();
			ArrayList<String> termList = domain.getDomainKnowledge();
			
			workerTermList.addAll( termList );
		}
		
		HashSet<String> reqTermSet = new HashSet<String>();
		for ( int i =0; i < reqTermList.size(); i++ ) {
			reqTermSet.add( reqTermList.get( i ) );
		}
		int workerTermCount = 0;
		for ( String term : reqTermSet ) {
			if ( workerTermList.contains( term )) {
				workerTermCount++;
			}
		}
		
		score = (1.0*workerTermCount) / (1.0*reqTermSet.size() );
		return score;
	}
	
	public Double extractDetExperience ( SortedMap<String, Boolean> selection ) {
		Double score = 0.0;
		
		for (String userId : selection.keySet()) {
			Boolean isSelect = selection.get(userId);
			if (isSelect == false) {
				continue;
			}
			if ( !candidateWorkers.containsKey( userId )) {
				continue;
			}
			
			Integer numBug = candidateWorkers.get( userId).getCapInfo().getNumBug()[0];
			score += 1.0*numBug;
		}
				
		return score;
	}
	
	public Double extractCost(SortedMap<String, Boolean> selection) {
		double score = 0.0;
		for (String userId : selection.keySet()) {
			Boolean isSelect = selection.get(userId);
			if (isSelect == false) {
				continue;
			}
			score += 1.0;
		}
		return score;
	}
}
