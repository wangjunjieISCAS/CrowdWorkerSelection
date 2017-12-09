package com.selectionObjective;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.data.Capability;
import com.data.Constants;
import com.data.CrowdWorker;
import com.data.DomainKnowledge;
import com.data.TestCase;
import com.data.TestProject;
import com.data.TestTask;
import com.dataProcess.SimilarityMeasure;
import com.learner.CapRevData;
import com.learner.WekaDataPrepare;
import com.learner.WekaPrediction;
import com.testCaseDataPrepare.TestCasePrepare;


public class BugProbability {
	private String wekaTrainFile;
	private String wekaTestFile;
	
	public BugProbability ( String projectName ) {
		wekaTrainFile = "data/input/weka/train-" + projectName + ".csv";
		wekaTestFile = "data/input/weka/test-" + projectName + ".csv";
	}
	
	public HashMap<String, Double> ObtainBugProbabilityTotal ( TestProject project, ArrayList<TestProject> historyProjectList, 
			HashMap<String, CrowdWorker> historyWorkerList, LinkedHashMap<String, CrowdWorker> candidateWorkerList ) {
		
		this.prepareWekaData(project, historyProjectList, historyWorkerList, candidateWorkerList);
		WekaPrediction wekaPrediction = new WekaPrediction();
		HashMap<Integer, Double> bugProbResults = wekaPrediction.trainAndPredictProb( wekaTrainFile, wekaTestFile, "");
		
		if ( candidateWorkerList.size() != bugProbResults.size() ) {
			System.out.println( "Wrong in size!");
		}
				
		//the result is in the same order with candidateWorkerList
		HashMap<String, Double> bugProbWorkerResults = new HashMap<String, Double>();
		int i = 0 ;
		for ( String userId: candidateWorkerList.keySet() ) {
			bugProbWorkerResults.put( userId ,  bugProbResults.get( i ) );
			i++;
		}
		
		return bugProbWorkerResults;
	}
	
	
	/*
	 * return the rank of candidate worker list
	 */
	public void prepareWekaData ( TestProject project, ArrayList<TestProject> historyProjectList, HashMap<String, 
			CrowdWorker> workerList, LinkedHashMap<String, CrowdWorker> candidateWorkerList ) {
		TestCasePrepare testCaseTool = new TestCasePrepare();
		
		CapRevData dataTool = new CapRevData();
		ArrayList<TestCase> caseListTrain = testCaseTool.prepareTestCaseInfo_wekaTrain( historyProjectList, workerList);
		Object[] attributeNameValue_train = dataTool.prepareAttributeData(caseListTrain , project.getTestTask() );
		
		WekaDataPrepare wekaDataTool = new WekaDataPrepare ();
		wekaDataTool.generateWekaDataFile(attributeNameValue_train, wekaTrainFile );
		
		ArrayList<TestCase> caseListTest = testCaseTool.prepareTestCaseInfo_wekaTest(project, candidateWorkerList );
		Object[] attributeNameValue_test = dataTool.prepareAttributeData(caseListTest , project.getTestTask() );
		wekaDataTool.generateWekaDataFile(attributeNameValue_test, wekaTestFile );
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
}
