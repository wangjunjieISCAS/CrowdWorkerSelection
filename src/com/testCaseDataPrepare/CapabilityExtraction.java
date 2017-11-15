package com.testCaseDataPrepare;

import java.util.ArrayList;
import java.util.HashMap;

import com.data.Capability;
import com.data.Phone;
import com.data.TestProject;
import com.data.TestReport;

public class CapabilityExtraction {
	
	public HashMap<String, Capability> obtainCapabilityInfo ( ArrayList<TestProject> projectList ) {
		HashMap<String, Integer> numProjectMap = new HashMap<String, Integer>();
		HashMap<String, Integer> numReportMap = new HashMap<String, Integer>();
		HashMap<String, Integer> numBugMap = new HashMap<String, Integer>();
		HashMap<String, Double> percBugMap = new HashMap<String, Double>();
		
		for ( int i =0; i < projectList.size(); i++ ) {
			TestProject project = projectList.get( i );
			
			HashMap<String, Integer> userProjectMap = new HashMap<String, Integer>();
			for ( int j = 0; j < project.getTestReportsInProj().size(); j++ ) {
				TestReport report = project.getTestReportsInProj().get( j );
			
				String userId = report.getUserId();
				String tag = report.getTag();
				
				userProjectMap.put( userId, 1 );
				
				int count = 1;
				if ( numReportMap.containsKey( userId )) {
					count += numReportMap.get( userId );
				}
				numReportMap.put( userId, count );
				
				if ( tag.equals( "ÉóºËÍ¨¹ý")) {
					count = 1;
				}else {
					count = 0;
				}
				
				if ( numBugMap.containsKey( userId)) {
					count += numBugMap.get( userId );
				}
				numBugMap.put( userId, count );
			}
			
			//update the numProjectMap for each project
			for ( String key : userProjectMap.keySet() ) {
				int value = userProjectMap.get( key );
				if ( numProjectMap.containsKey( key )) {
					value += numProjectMap.get( key );
				}
				numProjectMap.put( key, value);
			}
		}
		
		//generate the percBugMap based on the numReportMap and numBugMap
		for ( String key : numReportMap.keySet() ) {
			int reportNum = numReportMap.get( key );
			int bugNum = numBugMap.get( key );

			double percBug = (1.0*bugNum) / (1.0*reportNum);
			percBugMap.put( key, percBug );
		}
		
		//generate <userId, Capability> map
		HashMap<String, Capability> capInfoList = new HashMap<String, Capability>();
		for ( String key: numReportMap.keySet() ) {
			int numProject = numProjectMap.get(key);
			int numReport = numReportMap.get( key );
			int numBug = numBugMap.get( key );
			double percBug = percBugMap.get( key );
			
			Capability cap = new Capability ( numProject, numReport, numBug, percBug );
			capInfoList.put( key, cap );
		}
		
		return capInfoList;
	}
}
