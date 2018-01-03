package com.learner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.data.Capability;
import com.data.Constants;
import com.data.CrowdWorker;
import com.data.DomainKnowledge;
import com.data.Phone;
import com.data.TestCase;
import com.data.TestProject;
import com.data.TestReport;
import com.data.TestTask;
import com.dataProcess.CrowdWorkerHandler;
import com.dataProcess.SimilarityMeasure;
import com.dataProcess.TestProjectReader;
import com.performanceEvaluation.ProbPredictEvaluation;
import com.testCaseDataPrepare.CrowdWokerExtraction;
import com.testCaseDataPrepare.TestCasePrepare;
import com.topicModelData.TopicDataPrepare;


public class BugProbability {
	private String wekaTrainFile;
	private String wekaTestFile;
	private String workerTopicFile;
	private String taskTopicFile;
	
	public BugProbability ( String projectName ) {
		wekaTrainFile = "data/input/weka/train-" + projectName + ".csv";
		wekaTestFile = "data/input/weka/test-" + projectName + ".csv";
		workerTopicFile  = "data/input/topic/worker_topic_dis.txt";
		taskTopicFile = "data/input/topic/task_topic_dis.txt";
	}
	public BugProbability ( ) {
		wekaTrainFile = "data/input/weka/train.csv";
		wekaTestFile = "data/input/weka/test.csv";
		workerTopicFile  = "data/input/topic/worker_topic_dis.txt";
		taskTopicFile = "data/input/topic/task_topic_dis.txt";
	}
	
	public HashMap<String, Double> obtainBugProbabilityTotal ( TestProject project, ArrayList<TestProject> historyProjectList, 
			HashMap<String, CrowdWorker> historyWorkerList, LinkedHashMap<String, CrowdWorker> candidateWorkerList ) {
		
		this.prepareWekaData(project, historyProjectList, historyWorkerList, candidateWorkerList);
		System.out.println ( "Prepare weka data is done!"); 
		WekaPrediction wekaPrediction = new WekaPrediction();
		HashMap<Integer, Double> bugProbResults = wekaPrediction.trainAndPredictProb( wekaTrainFile, wekaTestFile, Constants.LEARNER_TYPE );
		System.out.println ( "Train and predict is done!"); 
		
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
	
	public HashMap<String, Double> obtainBugProbabilityTotalWithPreparedWekaData ( TestProject project, ArrayList<TestProject> historyProjectList, 
			HashMap<String, CrowdWorker> historyWorkerList, LinkedHashMap<String, CrowdWorker> candidateWorkerList ) {
		
		WekaPrediction wekaPrediction = new WekaPrediction();
		HashMap<Integer, Double> bugProbResults = wekaPrediction.trainAndPredictProb( wekaTrainFile, wekaTestFile, Constants.LEARNER_TYPE );
		System.out.println ( "Train and predict is done!"); 
		
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
	
		TopicDataPrepare topicTool = new TopicDataPrepare();
		HashMap<String, ArrayList<Double>> topicDisForWorker = topicTool.loadTopicDistribution( workerTopicFile );
		HashMap<String, ArrayList<Double>> topicDisForTask = topicTool.loadTopicDistribution( taskTopicFile );
		
		String projectName = project.getProjectName();
		int index = projectName.indexOf( "-");
		String taskIndex = projectName.substring( 0, index );
		ArrayList<Double> topicDisForThisTask = topicDisForTask.get ( taskIndex );
		
		CapRevTopicData dataTool = new CapRevTopicData();
		ArrayList<TestCase> caseListTrain = testCaseTool.prepareTestCaseInfo_wekaTrain( historyProjectList, workerList, topicDisForWorker );
		Object[] attributeNameValue_train = dataTool.prepareAttributeData(caseListTrain , project.getTestTask(),  topicDisForThisTask);
		
		WekaDataPrepare wekaDataTool = new WekaDataPrepare ();
		wekaDataTool.generateWekaDataFile(attributeNameValue_train, wekaTrainFile );
		
		ArrayList<TestCase> caseListTest = testCaseTool.prepareTestCaseInfo_wekaTest(project, candidateWorkerList, topicDisForWorker );
		Object[] attributeNameValue_test = dataTool.prepareAttributeData(caseListTest , project.getTestTask(), topicDisForThisTask);
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
	
	public HashMap<String, Double> loadBugProbability ( String fileName ) {
		HashMap<String, Double> bugProForWorker = new HashMap<String, Double>();
		try {			
			BufferedReader reader = new BufferedReader(new FileReader(new File( fileName )));
			String line = "";
			while ((line = reader.readLine()) != null) {
				String[] temp = line.split(",");
				String userId = temp[0];
				Double prob = Double.parseDouble(temp[1]);

				bugProForWorker.put(userId, prob);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bugProForWorker;
	}
	/*
	 * 生成testSet的28个任务的每个任务的bugProb
	 */
	public void generateBugProbForPerTestSet ( int testSetId, int beginTaskId, int endTaskId , ArrayList<TestProject> historyProjectList, 
			HashMap<Integer, TestProject> testSetProjecMap ) {
		String phoneFile = Constants.WORKER_INFO_FOLDER + "/" + testSetId+ "/workerPhone.csv";
		String capFile = Constants.WORKER_INFO_FOLDER + "/" + testSetId+ "/workerCap.csv";
		String domainFile = Constants.WORKER_INFO_FOLDER + "/" + testSetId+ "/workerDomain.csv";
		
		CrowdWorkerHandler workerHandler = new CrowdWorkerHandler();
		HashMap<String, CrowdWorker> historyWorkerList = workerHandler.loadCrowdWorkerInfo( phoneFile, capFile, domainFile);
		
		CrowdWokerExtraction workerTool = new CrowdWokerExtraction();
		for ( int i = beginTaskId; i <= endTaskId; i ++ ) {
			System.out.println( "Processing " + i + " task!");
			//生成这个任务对应的bugProb
			TestProject project = testSetProjecMap.get( i );
			//首先需要看是否有new workers
			CrowdWorker defaultWorker = workerTool.obtainDefaultCrowdWorker( historyWorkerList );
			//obtain candidate worker, besides the history worker, there could be worker who join the platform for the first time in this project
			LinkedHashMap<String, CrowdWorker> candidateWorkerList = new LinkedHashMap<String, CrowdWorker>();
			for ( String userId : historyWorkerList.keySet() ) {
				CrowdWorker hisWorker = historyWorkerList.get( userId );
				
				CrowdWorker worker = new CrowdWorker ( hisWorker.getWorkerId(), hisWorker.getPhoneInfo(), hisWorker.getCapInfo(), hisWorker.getDomainKnInfo() );
				candidateWorkerList.put( userId,  worker );
			}
			for ( int j =0; j < project.getTestReportsInProj().size(); j++ ) {
				TestReport report = project.getTestReportsInProj().get(j);
				String userId = report.getUserId();
				if ( candidateWorkerList.containsKey( userId ))
					continue;
				
				Phone phoneInfo = new Phone ( report.getPhoneType(), report.getOS(), report.getNetwork(), report.getISP() );
				CrowdWorker worker = new CrowdWorker ( userId, phoneInfo, defaultWorker.getCapInfo(), defaultWorker.getDomainKnInfo() );
				
				candidateWorkerList.put( userId, worker );
			}
			System.out.println( "CandidateWorkerList  " + i + " is done!");
			
			String projectName = project.getProjectName();
			BugProbability probTool = new BugProbability( projectName );
			HashMap<String, Double> bugProbWorkerResults = probTool.obtainBugProbabilityTotal(project, historyProjectList, historyWorkerList, candidateWorkerList);
			//HashMap<String, Double> bugProbWorkerResults = probTool.obtainBugProbabilityTotalWithPreparedWekaData( project, historyProjectList, historyWorkerList, candidateWorkerList);
			probTool.storeBugProb(bugProbWorkerResults, Constants.BUG_PROB_FOLDER + "/" + i + "-bugProbability.csv" );
			
			ProbPredictEvaluation evaluationTool = new ProbPredictEvaluation();
			Double[] performance = evaluationTool.obtainProbPredictionPerformance(bugProbWorkerResults, project);
			try {
				BufferedWriter writer = new BufferedWriter( new FileWriter ( Constants.BUG_PROB_PERFORMANCE , true));
				writer.write( i + ",");
				for ( int j =0; j < performance.length; j++ ) {
					writer.write( performance[j] + ",");
				}
				writer.newLine();
				writer.flush();
				
				writer.close();
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	}
	
	public static void main ( String args[] ) {
		BugProbability probTool = new BugProbability( );
		
		String projectFolder = "data/input/experimental dataset";
		String taskFolder = "data/input/taskDescription";
		TestProjectReader projReader = new TestProjectReader();
		/*
		ArrayList<TestProject> historyProjectList = projReader.loadTestProjectAndTaskListBasedId( 1, 532, projectFolder, taskFolder);
		HashMap<Integer, TestProject> testSetProjectMap = projReader.loadTestProjectAndTaskMapBasedId(533, 562, projectFolder, taskFolder);
		probTool.generateBugProbForPerTestSet( 20, 533, 562, historyProjectList, testSetProjectMap);
		*/
		
		ArrayList<TestProject> historyProjectList = projReader.loadTestProjectAndTaskListBasedId( 1, 504, projectFolder, taskFolder);
		HashMap<Integer, TestProject> testSetProjectMap = projReader.loadTestProjectAndTaskMapBasedId(505, 532, projectFolder, taskFolder);
		probTool.generateBugProbForPerTestSet( 19, 505, 532, historyProjectList, testSetProjectMap);
	}
}
