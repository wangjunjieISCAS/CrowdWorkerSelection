package com.findings;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.data.TestProject;
import com.data.TestReport;
import com.dataProcess.TestProjectReader;

public class BugReportNumber {
	
	public void NumberCounter ( String projectFolder  ) {
		TestProjectReader projReader = new TestProjectReader();
		ArrayList<TestProject> projList = projReader.loadTestProjectList( projectFolder );
		
		ArrayList<String> projNameList = new ArrayList<String>();
		HashMap<String, Integer> reportsPerTask = new HashMap<String, Integer>();
		HashMap<String, Integer> bugsPerTask = new HashMap<String, Integer>();
		
		HashMap<String, Integer> projectsPerWorker = new HashMap<String, Integer>();
		HashMap<String, Integer> reportsPerWorker = new HashMap<String, Integer>();
		HashMap<String, Integer> bugsPerWorker = new HashMap<String, Integer>();
		
		for ( int i =0; i < projList.size(); i++ ) {
			TestProject proj = projList.get( i );
			
			projNameList.add( proj.getProjectName() );
			reportsPerTask.put( proj.getProjectName(),  proj.getTestReportsInProj().size() );
			
			int bugsThisTask = 0;
			ArrayList<TestReport> reportList = proj.getTestReportsInProj();
			
			HashSet<String> workersThisProject = new HashSet<String>();
			for ( int j =0; j < reportList.size(); j++ ) {
				TestReport report = reportList.get( j );
				
				String worker = report.getUserId();
				workersThisProject.add( worker );
				
				String tag = report.getTag();
				
				int isBug = 0;
				if ( tag.equals( "ÉóºËÍ¨¹ý")) {
					isBug = 1;
				}

				bugsThisTask += isBug;
				
				int bugsThisWorker = isBug;
				if ( bugsPerWorker.containsKey( worker )) {
					bugsThisWorker += bugsPerWorker.get( worker );
				}
				bugsPerWorker.put ( worker, bugsThisWorker );
				
				int reportsThisWorker = 1;
				if ( reportsPerWorker.containsKey( worker )) {
					reportsThisWorker += reportsPerWorker.get( worker);
				}
				reportsPerWorker.put( worker, reportsThisWorker );
			}
			
			bugsPerTask.put( proj.getProjectName(), bugsThisTask );
			for ( String worker: workersThisProject ) {
				int num = 1;
				if ( projectsPerWorker.containsKey( worker )) {
					num += projectsPerWorker.get( worker );
				}
				projectsPerWorker.put( worker, num );
			}
		}		
		
		//output to file
		BufferedWriter writer;
		try {
			writer = new BufferedWriter( new FileWriter ( "data/output/findings/reportBugNumForProject.csv" ));
			
			writer.write( "projectName" + "," + "reportsPerTask" + "," + "bugsPerTask" );
			writer.newLine();
			for ( String projectName : reportsPerTask.keySet() ) {
				writer.write( projectName + "," + reportsPerTask.get( projectName ) +  "," + bugsPerTask.get( projectName ));
				writer.newLine();
			}
			writer.flush();
			writer.close();
			
			writer = new BufferedWriter( new FileWriter ( "data/output/findings/reportBugNumForWorker.csv" ));
			writer.write( "workerName" + "," + "projectsPerWorker" + "," + "reportsPerWorker" + "," + "bugsPerWorker" );
			writer.newLine();
			for ( String workerName : projectsPerWorker.keySet() ) {
				writer.write( workerName + "," + projectsPerWorker.get( workerName) + "," + reportsPerWorker.get( workerName ) + "," + bugsPerWorker.get( workerName ));
				writer.newLine();
			}
			writer.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static void main ( String[] args ) {
		String projectFolder = "data/input/total crowdsourced reports";
		BugReportNumber numberTool = new BugReportNumber();
		numberTool.NumberCounter( projectFolder );
	}
}
