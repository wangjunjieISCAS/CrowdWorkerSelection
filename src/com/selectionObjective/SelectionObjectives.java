package com.selectionObjective;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.SortedMap;

import com.data.Capability;
import com.data.Constants;
import com.data.CrowdWorker;
import com.data.DomainKnowledge;
import com.data.Phone;
import com.dataProcess.CrowdWorkerHandler;
import com.dataProcess.SimilarityMeasure;
import com.learner.BugProbability;

public class SelectionObjectives {
	private HashMap<String, CrowdWorker> candidateWorkers;
	private HashMap<String, Double> bugProForWorker;
	
	public SelectionObjectives ( String testSetIndex, String taskId ) {
		CrowdWorkerHandler workerTool = new CrowdWorkerHandler();
		candidateWorkers = workerTool.loadCrowdWorkerInfo( Constants.WORKER_INFO_FOLDER + "/" + testSetIndex + "/workerPhone.csv", 
				Constants.WORKER_INFO_FOLDER + "/" + testSetIndex + "/workerCap.csv", Constants.WORKER_INFO_FOLDER + "/" + testSetIndex + "/workerDomain.csv" );
		
		BugProbability probTool = new BugProbability( );
		bugProForWorker = probTool.loadBugProbability( Constants.BUG_PROB_FOLDER + "/" + taskId + "-bugProbability.csv" );
	}
	
	public double getBugProb(String workid){
		return this.bugProForWorker.get(workid);
	}

	public Double extractBugProbability(SortedMap<String, Boolean> selection) {
		double score = 0.0;

		int count = 0;
		for (String userId : selection.keySet()) {
			Boolean isSelect = selection.get(userId);
			if (isSelect == false) {
				continue;
			}
			count++;
			Double prob = 100 * bugProForWorker.get(userId);
			score += prob;
		}

		// score = score / count;
		// score = score * 100;
		return score;
	}

	//extractDiversity_Distance
	public Double extractDiversity_Distance (SortedMap<String, Boolean> selection) {
		double score = 0.0;
		ArrayList<CrowdWorker> workerList = new ArrayList<CrowdWorker>();
		for (String userId : selection.keySet()) {
			Boolean isSelect = selection.get(userId);
			if (isSelect == false) {
				continue;
			}
			workerList.add(candidateWorkers.get(userId));
		}

		SimilarityMeasure simTool = new SimilarityMeasure();
		// compute the distance between each pair of workers
		for (int i = 0; i < workerList.size(); i++) {
			for (int j = i + 1; j < workerList.size(); j++) {
				CrowdWorker workerI = workerList.get(i);
				CrowdWorker workerJ = workerList.get(j);

				double phoneDis = simTool.hammingDistanceForPhone(workerI.getPhoneInfo(), workerJ.getPhoneInfo());
				double domainDis = simTool.hammingDistanceForDomain(workerI.getDomainKnInfo(),
						workerJ.getDomainKnInfo());

				double dis = phoneDis * Constants.DIVERSITY_PHONE_WEIGHT
						+ domainDis * (1.0 - Constants.DIVERSITY_PHONE_WEIGHT);

				score += dis;
			}
		}

		// score = score / (workerList.size() * workerList.size() / 2);

		return score;
	}
	
	 //extractDiversity_Count
	public Double extractDiversity ( SortedMap<String, Boolean> selection ) {
		double score = 0.0;
		
		HashSet<String> phoneList = new HashSet<String>();
		HashSet<String> domainList = new HashSet<String>();
		for (String userId : selection.keySet()) {
			Boolean isSelect = selection.get(userId);
			if (isSelect == false) {
				continue;
			}
			
			Phone phone = candidateWorkers.get( userId ).getPhoneInfo();
			phoneList.add( phone.getPhoneType());
			phoneList.add ( phone.getOS() );
			phoneList.add( phone.getISP() );
			phoneList.add( phone.getNetwork() );
			
			DomainKnowledge domain = candidateWorkers.get( userId ).getDomainKnInfo();
			for ( int i =0; i < domain.getDomainKnowledge().size(); i++ ) {
				domainList.add( domain.getDomainKnowledge().get( i ) );
			}
		}
		
		score = phoneList.size() + domainList.size();
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
