package com.testCaseDataPrepare;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import com.data.Capability;
import com.data.Constants;
import com.data.Phone;
import com.data.TestProject;
import com.data.TestReport;

public class CapabilityExtraction {
	
	public HashMap<String, Capability> obtainCapabilityInfo ( ArrayList<TestProject> projectList, Date curTime ) {
		HashMap<String, Integer[]> numProjectMap = new HashMap<String, Integer[]>();
		HashMap<String, Integer[]> numReportMap = new HashMap<String, Integer[]>();
		HashMap<String, Integer[]> numBugMap = new HashMap<String, Integer[]>();
		HashMap<String, Double[]> percBugMap = new HashMap<String, Double[]>();
		HashMap<String, Integer> durationDaysMap = new HashMap<String, Integer>();
		
		Calendar cald = Calendar.getInstance();
		cald.setTime( curTime );
		cald.add( Calendar.MONTH, -1 );
		Date oneMonthBefore = cald.getTime();
		
		cald.setTime( curTime );
		cald.add( Calendar.MONTH, -2);
		Date twoMonthBefore = cald.getTime();
		
		cald.setTime( curTime );
		cald.add( Calendar.MONTH, -4);
		Date fourMonthBefore = cald.getTime();
		
		/*
		SimpleDateFormat formatLine = new SimpleDateFormat ("yyyy/MM/dd HH:mm");
		System.out.println( formatLine.format( curTime ) + " ---- " + formatLine.format( oneMonthBefore ) + " ---- " + formatLine.format (twoMonthBefore) 
			+ " ---- " + formatLine.format( fourMonthBefore ) );
		*/
		for ( int i =0; i < projectList.size(); i++ ) {
			TestProject project = projectList.get( i );
			
			//标记该成员是否在该任务中被统计过了，用于numProjectMap
			HashSet<String> userProjectTag = new HashSet<String>();
			HashMap<String, Integer> userProjectMap = new HashMap<String, Integer>();
			for ( int j = 0; j < project.getTestReportsInProj().size(); j++ ) {
				TestReport report = project.getTestReportsInProj().get( j );
			
				String userId = report.getUserId();
				String tag = report.getTag();
				Date subTime = report.getSubmitTime();
				
				userProjectMap.put( userId, 1 );
				
				Integer[] numReport = this.newZeroArrays() ;
				if ( numReportMap.containsKey( userId )) {
					numReport = numReportMap.get( userId );
				}
				numReport[0] += 1;
				if ( subTime.compareTo( oneMonthBefore) > 0 ) {
					numReport[1] += 1;
				}
				if ( subTime.compareTo( twoMonthBefore) > 0 ) {
					numReport[2] += 1;
				}
				if ( subTime.compareTo( fourMonthBefore ) > 0 ) {
					numReport[3] += 1;
				}
				numReportMap.put( userId, numReport );
				
				Integer[] numBug = this.newZeroArrays() ;
				if ( tag.equals( "审核通过")) {
					if ( numBugMap.containsKey( userId )) {
						numBug = numBugMap.get( userId );
					}
					numBug[0] += 1;
					if ( subTime.compareTo( oneMonthBefore) > 0 ) {
						numBug[1] += 1;
					}
					if ( subTime.compareTo( twoMonthBefore) > 0 ) {
						numBug[2] += 1;
					}
					if ( subTime.compareTo( fourMonthBefore ) > 0 ) {
						numBug[3] += 1;
					}
				}	
				numBugMap.put( userId, numBug );
				
				if (!userProjectTag.contains( userId )) {
					userProjectTag.add( userId );
					
					Integer[] numProject = this.newZeroArrays() ;
					if ( numProjectMap.containsKey( userId )) {
						numProject = numProjectMap.get( userId );
					}
					numProject[0] += 1;
					if ( subTime.compareTo( oneMonthBefore) > 0 ) {
						numProject[1] += 1;
					}
					if ( subTime.compareTo( twoMonthBefore) > 0 ) {
						numProject[2] += 1;
					}
					if ( subTime.compareTo( fourMonthBefore ) > 0 ) {
						numProject[3] += 1;
					}
					numProjectMap.put( userId, numProject );
				}
				
				int durationDay = (int) ((curTime.getTime() - subTime.getTime() ) / (1000 * 3600 * 24) );
				//System.out.println( "test duration: " + durationDay );
				if ( durationDay < 0 )
					durationDay = 0;
				if ( !durationDaysMap.containsKey( userId) || durationDaysMap.get( userId ) > durationDay ) {
					durationDaysMap.put( userId , durationDay );
				}
			}
		}
		
		//generate the percBugMap based on the numReportMap and numBugMap
		for ( String key : numReportMap.keySet() ) {
			Integer[] reportNum = numReportMap.get( key );
			Integer[] bugNum = numBugMap.get( key );

			Double[] percBug = new Double[Constants.CAP_SIZE_PER_TYPE];
			for ( int i =0; i < percBug.length; i++ ) {
				percBug[i] = 0.0;
			}
			for ( int i =0; i < reportNum.length; i++ ) {
				if ( reportNum[i] != 0 )
					percBug[i] = (1.0*bugNum[i]) / (1.0*reportNum[i]);
			}
			percBugMap.put( key, percBug );
		}
		
		//generate <userId, Capability> map
		HashMap<String, Capability> capInfoList = new HashMap<String, Capability>();
		for ( String key: numReportMap.keySet() ) {
			Integer[] numProject = numProjectMap.get(key);
			Integer[] numReport = numReportMap.get( key );
			Integer[] numBug = numBugMap.get( key );
			Double[] percBug = percBugMap.get( key );
			Integer durationLastAct = durationDaysMap.get( key );
			
			Capability cap = new Capability ( numProject, numReport, numBug, percBug, durationLastAct );
			capInfoList.put( key, cap );
		}
		
		return capInfoList;
	}
	
	public Integer[] newZeroArrays (  ) {
		Integer[] arrayName = new Integer[Constants.CAP_SIZE_PER_TYPE];
		for ( int i =0; i < arrayName.length; i++ ) {
			arrayName[i] = 0;
		}
		return arrayName;
	}
}
