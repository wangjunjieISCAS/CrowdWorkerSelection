package com.performanceEvaluation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import com.data.Constants;
import com.data.TestProject;
import com.data.TestReport;
import com.dataProcess.TestProjectReader;

public class BugDetectionRateGroundTruth {
	int maxWorkerNum = 120;
	
	public void obtainBugDetectionRate ( TestProject project ) {
		LinkedHashMap<Integer, Double> bugDetectionRate = new LinkedHashMap<Integer, Double>();
		
		HashSet<String> noDupBug = new HashSet<String>();
		//HashSet<String> noDupWorkers = new HashSet<String>();
		ArrayList<String> noDupWorkers = new ArrayList<String>();
		for ( int i =0; i < project.getTestReportsInProj().size(); i++ ) {
			TestReport report = project.getTestReportsInProj().get( i );
			
			noDupWorkers.add( report.getUserId() );
			if ( report.getTag().equals( "ÉóºËÍ¨¹ý")) {
				String noDup = report.getDuplicate();
				noDupBug.add( noDup );
			}
			bugDetectionRate.put( noDupWorkers.size(), noDupBug.size()*1.0 );
		}
		
		Integer totalBugs = noDupBug.size();
		int maxIndex = 0;
		for ( Integer index : bugDetectionRate.keySet() ) {
			Double value = bugDetectionRate.get( index );
			value = value / totalBugs;
			bugDetectionRate.put( index, value);
			if ( index > maxIndex )
				maxIndex = index;
		}
		
		double maxProb = bugDetectionRate.get( maxIndex );
		for ( int i = maxIndex+1; i < this.maxWorkerNum; i++ ) {
			bugDetectionRate.put( i, maxProb);
		}
		
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( Constants.BUG_DETECTION_RATE_PERFORMANCE_FOLDER + "/TRUE-2/" + "TRUE-" + project.getProjectName() + ".csv" ));
			for ( Integer index : bugDetectionRate.keySet() ) {
				Double rate = bugDetectionRate.get( index );
				writer.write( index +"," + rate);
				writer.newLine();
			}
			
			writer.flush();
			
			writer.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static void main ( String args[]) {
		BugDetectionRateGroundTruth performanceTool = new BugDetectionRateGroundTruth();
		
		TestProjectReader projReader = new TestProjectReader();
		ArrayList<TestProject> projectList = projReader.loadTestProjectAndTaskList( Constants.TOTAL_PROJECT_FOLDER, Constants.TOTAL_TASK_DES_FOLDER );
		
		for ( int i =0; i < projectList.size(); i++ ) {
			performanceTool.obtainBugDetectionRate( projectList.get( i ) );
		}
	}
}
