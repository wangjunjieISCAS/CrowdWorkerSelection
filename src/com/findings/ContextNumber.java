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

public class ContextNumber {
	
	public void NumberCounter ( String projectFolder ) {
		TestProjectReader projReader = new TestProjectReader();
		ArrayList<TestProject> projList = projReader.loadTestProjectList( projectFolder );
		
		HashMap<String, Integer> contextTotalNum = new HashMap<String, Integer>();
		HashMap<String, HashSet<String>> workersPerContext = new HashMap<String, HashSet<String>>();
		HashMap<String, Integer> reportsPerContext = new HashMap<String, Integer>();
		HashMap<String, Integer> bugsPerContext = new HashMap<String, Integer>();
		
		HashMap<String, Integer> contextsPerTask = new HashMap<String, Integer>();   //number of different context each task
		
		for ( int i =0; i < projList.size(); i++ ) {
			TestProject proj = projList.get( i );
			
			ArrayList<TestReport> reportList = proj.getTestReportsInProj();
			
			HashSet<String> contextsThisProject = new HashSet<String>();
			for ( int j =0; j < reportList.size(); j++ ) {
				TestReport report = reportList.get( j );
				String device = report.getPhoneType();
				contextsThisProject.add( device );
				
				int deviceNum = 1;
				if ( contextTotalNum.containsKey( device )) {
					deviceNum += contextTotalNum.get( device );
				}
				contextTotalNum.put( device, deviceNum );
				
				deviceNum  =1;
				if ( reportsPerContext.containsKey( device)) {
					deviceNum += reportsPerContext.get( device );
				}
				reportsPerContext.put( device, deviceNum );
				
				int isBug = 0;
				if ( report.getTag().equals( "ÉóºËÍ¨¹ý")) {
					isBug = 1;
				}
				if ( bugsPerContext.containsKey( device )) {
					isBug += bugsPerContext.get( device );
				}
				bugsPerContext.put( device, isBug );
				
				HashSet<String> workers = new HashSet<String>();
				if ( workersPerContext.containsKey( device )) {
					workers = workersPerContext.get( device );
				}
				workers.add( report.getUserId());
				workersPerContext.put( device, workers );
				
			}
			contextsPerTask.put( proj.getProjectName(), contextsThisProject.size() );
		}
		
		//output to file
		BufferedWriter writer;
		try {
			writer = new BufferedWriter( new FileWriter ( "data/output/findings/reportBugNumForContext.csv" ));
			
			writer.write( "contextName" + "," + "contextTotalNum" + "," + "workersPerContext" + "," + "reportsPerContext" + "," + "bugsPerContext" );
			writer.newLine();
			for ( String contextName : contextTotalNum.keySet() ) {
				writer.write( contextName + "," + contextTotalNum.get( contextName ) +  "," + workersPerContext.get(contextName).size() + "," + reportsPerContext.get( contextName ) + 
						"," + bugsPerContext.get( contextName ));
				writer.newLine();
			}
			writer.flush();
			writer.close();
			
			writer = new BufferedWriter( new FileWriter ( "data/output/findings/contextNumForProject.csv" ));
			writer.write( "projectName" + "," + "contextPerTask" );
			writer.newLine();
			for ( String projectName : contextsPerTask.keySet() ) {
				writer.write( projectName + "," + contextsPerTask.get( projectName) );
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
		ContextNumber numberTool = new ContextNumber(  );
		numberTool.NumberCounter( projectFolder );
	}
}
