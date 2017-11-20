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
import com.testCaseDataPrepare.TestCasePrepare;



public class BugProbability {
	private String wekaTrainFile = "data/input/weka/train.csv";
	private String wekaTestFile = "data/input/weka/test.csv";
	
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
		ArrayList<TestCase> caseListTrain = testCaseTool.prepareTestCaseInfo_wekaTrain( historyProjectList, workerList);
		this.generateWekaDataFile(caseListTrain, project.getTestTask(), wekaTrainFile );
		
		ArrayList<TestCase> caseListTest = testCaseTool.prepareTestCaseInfo_wekaTest(project, candidateWorkerList );
		this.generateWekaDataFile( caseListTest, project.getTestTask(), wekaTestFile );
	}
	
	public void generateWekaDataFile ( ArrayList<TestCase> caseList, TestTask task, String fileName ) {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter( new FileWriter ( fileName ));
			
			writer.write( "numProject" + "," + "numReport" + "," + "numBug" + "," + "percBug" + "," + "relevance");
			writer.write( "category" );
			writer.newLine();
			
			for ( int i =0;  i < caseList.size(); i++ ) {
				TestCase testCase = caseList.get( i);
				
				Capability capInfo = testCase.getWorker().getCapInfo();
				DomainKnowledge domainInfo = testCase.getWorker().getDomainKnInfo();
				SimilarityMeasure similarityTool = new SimilarityMeasure();
				
				writer.write( capInfo.getNumProject() + ",");
				writer.write( capInfo.getNumReport() + ",");
				writer.write( capInfo.getNumBug() + ",");
				writer.write( capInfo.getPercBug() + ",");
				
				Double sim = similarityTool.cosinSimilarity( domainInfo.getDomainKnowledge(), task.getTaskDescription() );
				writer.write( sim + ",");
				
				if ( testCase.getTestOracle().equals( "ÉóºËÍ¨¹ý")) {
					writer.write( "yes");
				}else {
					writer.write( "no");
				}
				
				writer.newLine();
				writer.flush();
			}
			writer.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
