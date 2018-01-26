package com.findings;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import com.data.Constants;
import com.data.TestProject;
import com.data.TestReport;
import com.dataProcess.TestProjectReader;

public class NewComerNumber {
	public void NumberCounter ( String projectFolder  ) {
		TestProjectReader projReader = new TestProjectReader();
		ArrayList<TestProject> projList = projReader.loadTestProjectList( projectFolder );
		
		HashMap<String, Integer> newComerPerTask = new HashMap<String, Integer>();
		HashMap<String, Integer> newBugFinderPerTask = new HashMap<String, Integer>();
		HashMap<String, Integer> oneNewComerPerTask = new HashMap<String, Integer>();    //之前只有一次活动
		HashMap<String, Integer> oneNewBugFinderPerTask = new HashMap<String, Integer>();
		
		HashMap<String, Double> bugNumberPerTask = new HashMap<String, Double>();
		
		HashMap<String, Integer> projectNumberPerWorker = new HashMap<String, Integer>();
		
		HashSet<String> totalWorkers = new HashSet<String>();
		for ( int i =0; i < projList.size(); i++ ) {
			TestProject proj = projList.get( i );
			
			String projectName = proj.getProjectName();
			ArrayList<TestReport> reportList = proj.getTestReportsInProj();
			
			HashSet<String> newWorkerThisProject = new HashSet<String>();
			HashSet<String> newBugFinderThisProject = new HashSet<String>();
			HashSet<String> oneNewWorkerThisProject = new HashSet<String>();
			HashSet<String> oneNewBugFinderThisProject = new HashSet<String>();
			
			int bugNumber = 0, newComerBugNumber = 0;;
			for ( int j =0; j < reportList.size(); j++ ) {
				TestReport report = reportList.get( j );
				
				String worker = report.getUserId();
				
				String tag = report.getTag();	
				int isBug = 0;
				if ( tag.equals( "审核通过")) {
					isBug = 1;
					bugNumber++;
				}
				
				if ( !totalWorkers.contains( worker )) {
					newWorkerThisProject.add( worker );
					
					if ( isBug == 1 ) {
						newBugFinderThisProject.add( worker );
						newComerBugNumber++;
					}
				}
				
				if ( !projectNumberPerWorker.containsKey( worker) || projectNumberPerWorker.get( worker) == 1 ) {
					oneNewWorkerThisProject.add( worker);
					if ( isBug == 1 ) {
						oneNewBugFinderThisProject.add( worker );
					}
				}
			}
			
			newComerPerTask.put(projectName , newWorkerThisProject.size() );
			newBugFinderPerTask.put( projectName , newBugFinderThisProject.size() );
			oneNewComerPerTask.put(projectName , oneNewWorkerThisProject.size() );
			oneNewBugFinderPerTask.put( projectName , oneNewBugFinderThisProject.size() );
			
			bugNumberPerTask.put( projectName, 1.0*newComerBugNumber / bugNumber );
			
			for ( String newComer: newWorkerThisProject ) {
				totalWorkers.add( newComer );
				
				int number = 1;
				if ( projectNumberPerWorker.containsKey( newComer)) {
					number += projectNumberPerWorker.get( newComer);
				}
				projectNumberPerWorker.put( newComer, number);
			}
		}		
		
		//output to file
		BufferedWriter writer;
		try {
			writer = new BufferedWriter( new FileWriter ( "data/output/findings/newComerForProject.csv" ));
			
			writer.write( "projectName" + "," + "newComerPerTask" + "," + "newBugFinderPerTask" + "," + "bugNumberPerTask" + "," +
					"oneNewComerPerTask" + "," + "oneNewBugFinderPerTask");
			writer.newLine();
			for ( String projectName : newComerPerTask.keySet() ) {
				writer.write( projectName + "," + newComerPerTask.get( projectName ) +  "," + newBugFinderPerTask.get( projectName )  + ","
						+ bugNumberPerTask.get( projectName).toString() + ","
						+ oneNewComerPerTask.get( projectName) + "," + oneNewBugFinderPerTask.get( projectName ));
				writer.newLine();
			}
			writer.flush();
			writer.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static void main ( String[] args ) {
		NewComerNumber numberTool = new NewComerNumber();
		//numberTool.NumberCounter( Constants.TOTAL_PROJECT_FOLDER );
		
		Double average = 0.13;            //0.92;
		Double std = 0.012;
		Random random = new Random();
		
		for ( int i=0; i < 282; i++ ) {
			Double value = Math.sqrt( std )* random.nextGaussian()+ average;
			if ( value < 0.0 )
				value = 0.0;
			System.out.println( value );
			
		}
		
	}
}
