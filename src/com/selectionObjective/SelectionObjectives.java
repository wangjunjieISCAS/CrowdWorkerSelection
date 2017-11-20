package com.selectionObjective;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.SortedMap;

import com.data.CrowdWorker;

public class SelectionObjectives {
	private static HashMap<String, CrowdWorker> candidateWorkers;
	private static HashMap<String, Double> bugProForWorker;
	
	//read the file to initiate the candidateWorkers, and the bugProbForWorker
	static {
		
	}
	
	public static Double extractBugProbability ( SortedMap<String, Boolean> selection) {
		double score = 0.0;
		return score;
	}
	
	public static Double extractDiversity ( SortedMap<String, Boolean> selection) {
		double score = 0.0;
		return score;
	}
	
	public static Double extractCost ( SortedMap<String, Boolean> selection ) {
		double score = 0.0;
		return score;
	}
}
