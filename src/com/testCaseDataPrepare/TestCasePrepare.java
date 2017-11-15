package com.testCaseDataPrepare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.data.CrowdWorker;
import com.data.Phone;
import com.data.TestCase;
import com.data.TestProject;
import com.data.TestReport;
import com.data.TestTask;

public class TestCasePrepare {
	
	/*
	 * prepareTestCaseInfo can be called when prepare the wekaTrain data and wekaTest data, but the workerList should be generated based on the historicalProjectList
	 */
	public ArrayList<TestCase> prepareTestCaseInfo_wekaTrain ( ArrayList<TestProject> projectList, HashMap<String, CrowdWorker> workerList ) {
		ArrayList<TestCase> testCaseList = new ArrayList<TestCase>();
		
		for ( int i =0; i < projectList.size(); i++ ) {
			TestProject project = projectList.get( i );
			
			for ( int j =0; j < project.getTestReportsInProj().size(); j++ ) {
				TestReport report = project.getTestReportsInProj().get(j);
				
				String userId = report.getUserId();
				TestTask task = project.getTestTask();
				String tag = report.getTag();
				
				CrowdWorker worker = workerList.get( userId );
				
				TestCase testCase = new TestCase( task, worker, tag );
				testCaseList.add( testCase );
			}
		}
		return testCaseList;
	}
	
	/*
	 * A user might have no record in workerList, so we need to create the default worker
	 * for default worker, only generate the capability and domain knowledge using average level, for phone info and userId, use its original
	 * 
	 * or can use put the trainSet and testSet together as the trainSet, however this is not so strict
	 */
	public ArrayList<TestCase> prepareTestCaseInfo_wekaTest ( TestProject project, HashMap<String, CrowdWorker> workerList, CrowdWorker defaultWorker ) {
		ArrayList<TestCase> testCaseList = new ArrayList<TestCase>();
		TestTask task = project.getTestTask();
		
		HashSet<String> workerInProject = new HashSet<String>();
		for ( int i =0; i < project.getTestReportsInProj().size(); i++  ) {
			TestReport report = project.getTestReportsInProj().get( i);
			
			String userId = report.getUserId();
			String tag = report.getTag();

			CrowdWorker worker = null;
			if ( workerList.containsKey( userId )) {
				worker = workerList.get( userId );
			}
			else {
				worker = defaultWorker;
	
				Phone phoneInfo = new Phone ( report.getPhoneType(), report.getOS(), report.getNetwork(), report.getISP() );
				worker.setPhoneInfo(phoneInfo);
				worker.setWorkerId( userId );
			}
			
			workerInProject.add( userId );
			TestCase testCase = new TestCase ( task, worker, tag );
			testCaseList.add( testCase );
		}
		
		for ( String workerId : workerList.keySet() ) {
			if ( workerInProject.contains( workerId )) {
				continue;
			}
			TestCase testCase = new TestCase ( task, workerList.get( workerId), "unknown");
			testCaseList.add ( testCase );
		}
		
		return testCaseList;
	}
}
