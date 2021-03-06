package com.testCaseDataPrepare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import com.data.CrowdWorker;
import com.data.Phone;
import com.data.TestCase;
import com.data.TestProject;
import com.data.TestReport;
import com.data.TestTask;
import com.topicModelData.TopicDataPrepare;

public class TestCasePrepare {
	
	/*
	 * prepareTestCaseInfo can be called when prepare the wekaTrain data and wekaTest data, but the workerList should be generated based on the historicalProjectList
	 */
	public ArrayList<TestCase> prepareTestCaseInfo_wekaTrain ( ArrayList<TestProject> projectList, HashMap<String, CrowdWorker> workerList, 
			HashMap<String, ArrayList<Double>> topicDisForWorker) {
		ArrayList<TestCase> testCaseList = new ArrayList<TestCase>();
		
		for ( int i =0; i < projectList.size(); i++ ) {
			TestProject project = projectList.get( i );
			
			for ( int j =0; j < project.getTestReportsInProj().size(); j++ ) {
				TestReport report = project.getTestReportsInProj().get(j);
				
				String userId = report.getUserId();
				TestTask task = project.getTestTask();
				String tag = report.getTag();
				
				CrowdWorker worker = workerList.get( userId );
				//System.out.println ( "----------------------------- " + worker.getCapInfo().getNumProject() + " " + worker.getCapInfo().getNumReport() );
				
				ArrayList<Double> topicDis = new ArrayList<Double>();
				if ( topicDisForWorker.containsKey( userId )) {
					topicDis = topicDisForWorker.get( userId );
				}
				TestCase testCase = new TestCase( task, worker, topicDis, tag );
				testCaseList.add( testCase );
			}
		}
		return testCaseList;
	}
	
	/*
	 * the parameter workerList is the candidate worker list
	 */
	public ArrayList<TestCase> prepareTestCaseInfo_wekaTest ( TestProject project, LinkedHashMap<String, CrowdWorker> workerList, 
			HashMap<String, ArrayList<Double>> topicDisForWorker ) {
		ArrayList<TestCase> testCaseList = new ArrayList<TestCase>();
		TestTask task = project.getTestTask();
		
		HashMap<String, String> userTag = new HashMap<String, String>();
		for ( int i =0; i < project.getTestReportsInProj().size(); i++  ) {
			TestReport report = project.getTestReportsInProj().get( i);
			
			String userId = report.getUserId();
			String tag = report.getTag();
			
			//one user might submit several reports
			userTag.put( userId, tag );
		}
		
		for ( String userId: workerList.keySet() ) {
			CrowdWorker worker = workerList.get( userId );
			
			String tag = "unknown";
			if ( userTag.containsKey( userId )) {
				tag = userTag.get( userId );
			}
			
			ArrayList<Double> topicDis = new ArrayList<Double>();
			if ( topicDisForWorker.containsKey( userId )) {
				topicDis = topicDisForWorker.get( userId );
			}
			TestCase testCase = new TestCase ( task, worker, topicDis, tag );
			testCaseList.add( testCase );
		}
				
		return testCaseList;
	}
}
