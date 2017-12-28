package com.findings;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import com.data.TestProject;
import com.data.TestReport;
import com.dataProcess.TestProjectReader;

public class WorkerLastActivity {
	//统计在某个任务中的crowd worker的上一次活动距离参加本次任务的时间；以及在该任务中发现了缺陷的crowd worker的上一次活动距离本次任务的时间
	public void workerLastActivityCounter ( String projectFolder ) {
		TestProjectReader projReader = new TestProjectReader();
		ArrayList<TestProject> projList = projReader.loadTestProjectList( projectFolder );
		
		HashMap<String, ArrayList<Date>> workerActivityDateList = this.obtainWorkersActivityRecord(projList);
			
		//对每个项目进行统计
		HashMap<String, Integer> intervalForProjects = new HashMap<String, Integer>();
		for ( int i =0; i < projList.size(); i++ ) {
			TestProject proj = projList.get( i );
			
			String projectName = proj.getProjectName();
			ArrayList<TestReport> reportList = proj.getTestReportsInProj();
			
			int intervalDays = -1;
			for ( int j =0; j < reportList.size(); j++ ) {
				TestReport report = reportList.get( j );
				String id = report.getUserId();
				Date curActDate = report.getSubmitTime();
				
				String tag = report.getTag();
				if ( !tag.equals( "审核通过")) {
					continue;
				}
				
				Date beforeActDate = null;
				ArrayList<Date> actDateList = workerActivityDateList.get( id );
				int k =0;
				for ( ; k < actDateList.size(); k++ ) {
					Date actDate = actDateList.get( k );
					if ( actDate.equals( curActDate )) {
						break;
					}
				}
				if ( k > 0 ) {
					beforeActDate = actDateList.get( k-1 );
				}
				else {
					beforeActDate = actDateList.get( k );
				}
				
				int interval = (int) (( curActDate.getTime() - beforeActDate.getTime()) / 1000 / 60 / 60 / 24);  
				if ( interval > intervalDays ) {
					intervalDays = interval;
				}
			}
			intervalForProjects.put( projectName, intervalDays );
		}
			
		//output to file
		BufferedWriter writer;
		try {
			writer = new BufferedWriter( new FileWriter ( "data/output/findings/workerLastActivity.csv" ));
			
			writer.write( "projectName" + "," + "lastActivity" );
			writer.newLine();
			for ( String projectName : intervalForProjects.keySet() ) {
				writer.write( projectName + "," + intervalForProjects.get( projectName ) );
				writer.newLine();
			}
			writer.flush();
			writer.close();
			
			writer.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public HashMap<String, ArrayList<Date>> obtainWorkersActivityRecord ( ArrayList<TestProject> projList ) {
		HashMap<String, ArrayList<Date>> workerActivityDateList = new HashMap<String, ArrayList<Date>>();
		for ( int i =0; i < projList.size(); i++ ) {
			TestProject proj = projList.get( i );			
			ArrayList<TestReport> reportList = proj.getTestReportsInProj();
			
			for ( int j =0; j < reportList.size(); j++ ) {
				TestReport report = reportList.get( j );
				String id = report.getUserId();
				Date actDate = report.getSubmitTime();
				
				ArrayList<Date> activityDate = null;
				if ( workerActivityDateList.containsKey( id )) {
					activityDate = workerActivityDateList.get( id );
				}else {
					activityDate = new ArrayList<Date>();
				}
				activityDate.add( actDate );
				
				workerActivityDateList.put( id, activityDate );
			}
		}
		//排序
		for ( String id : workerActivityDateList.keySet() ) {
			ArrayList<Date> activityDate = workerActivityDateList.get( id );
			Collections.sort( activityDate );
			
			//System.out.println( activityDate.size() );
			workerActivityDateList.put( id, activityDate );
		}
		
		return workerActivityDateList;
	}
	
	
	public static void main ( String args[] ) {
		WorkerLastActivity activityTool = new WorkerLastActivity();
		
		String projectFolder = "data/input/experimental dataset";
		activityTool.workerLastActivityCounter( projectFolder );
	}
}
