package com.performanceEvaluation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.data.Constants;
import com.data.TestProject;
import com.data.TestReport;

/*
 * bug detection rate: number of bugs detected by the selected workers among all the bugs detected in original projects
 * could not use average precision because this is not a rank problem
 */
public class BugDetectionRateEvaluation {
	public HashMap<Integer, Double> obtainBugDetectionRate ( HashMap<Integer, ArrayList<ArrayList<String>>> selectionResults, TestProject project, boolean isNonDuplicate, String folderName  ) {
		//选择不同人数时的bug detection rate
		HashMap<Integer, Double> bugDetectionRate = new HashMap<Integer, Double>();
		
		if ( isNonDuplicate == true ) {
			int totalCount = this.obtainTotalBugCountNonDuplicate(project);
			
			for ( Integer countNum : selectionResults.keySet() ) {
				ArrayList<ArrayList<String>> workersList = selectionResults.get( countNum );
				int count = 0;
				for ( int i =0; i < workersList.size(); i++ ) {
					int temp = this.obtainBugCountNonDuplicate( workersList.get( i ), project);
					if ( count < temp )
						count = temp;
				}
				bugDetectionRate.put( countNum, (1.0*count)/(1.0*totalCount) );
			}
		}
		else {
			int totalCount = this.obtainTotalBugCount(project);
			
			for ( Integer countNum : selectionResults.keySet() ) {
				ArrayList<ArrayList<String>> workersList = selectionResults.get( countNum );
				int count = 0;
				for ( int i =0; i < workersList.size(); i++ ) {
					int temp = this.obtainBugCount( workersList.get( i ), project);
					if ( count < temp )
						count = temp;
				}
				bugDetectionRate.put( countNum, (1.0*count)/(1.0*totalCount) );
			}
		}		
		
		List<HashMap.Entry<Integer, Double>> bugDetectionRateList = new ArrayList<HashMap.Entry<Integer, Double>>(bugDetectionRate.entrySet() );

		Collections.sort( bugDetectionRateList, new Comparator<HashMap.Entry<Integer, Double>>() {   
			public int compare(HashMap.Entry<Integer, Double> o1, HashMap.Entry<Integer, Double> o2) {      
			        //return (o2.getValue() - o1.getValue()); 
			        return o1.getKey().compareTo(o2.getKey() ) ;
			    }
			}); 
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( Constants.BUG_DETECTION_RATE_PERFORMANCE_FOLDER + "/" + folderName + "/" + folderName  + "-" + project.getProjectName() + ".csv" ));
			for ( int i =0; i < bugDetectionRateList.size(); i++  ) {
				HashMap.Entry<Integer, Double> entry = bugDetectionRateList.get( i );
				int userNum = entry.getKey();
				Double rate = entry.getValue();
				writer.write( userNum +"," + rate);
				writer.newLine();
			}
			writer.flush();
			
			writer.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return bugDetectionRate;
	}
	
	public Integer obtainTotalBugCount ( TestProject project) {
		int totalCount = 0;
		for ( int i =0; i < project.getTestReportsInProj().size(); i++ ) {
			TestReport report = project.getTestReportsInProj().get( i );
			String tag = report.getTag();
			if ( tag.equals( "审核通过") ) {
				totalCount ++;
			}
		}
		return totalCount;
	}	
	public Integer obtainBugCount ( ArrayList<String> selectedWorkers, TestProject project ) {
		int count = 0;
		for ( int i =0; i < project.getTestReportsInProj().size(); i++ ) {
			TestReport report = project.getTestReportsInProj().get( i );
			String userId = report.getUserId();
			String tag = report.getTag();
			if ( tag.equals( "审核通过") && selectedWorkers.contains( userId )) {
				count++;
			}
		}
		return count;
	}
	
	public Integer obtainTotalBugCountNonDuplicate ( TestProject project ) {
		HashSet<String> nonDupSet = new HashSet<String>();
		for ( int i =0; i < project.getTestReportsInProj().size(); i++ ) {
			TestReport report = project.getTestReportsInProj().get( i );
			String tag = report.getTag();
			if ( tag.equals( "审核通过") ) {
				String dupTag = report.getDuplicate();
				nonDupSet.add( dupTag );
			}
		}
		
		int totalCount = nonDupSet.size();
		return totalCount;
	}
	public Integer obtainBugCountNonDuplicate ( ArrayList<String> selectedWorkers, TestProject project ) {
		HashSet<String> nonDupSet = new HashSet<String>();
		for ( int i =0; i < project.getTestReportsInProj().size(); i++ ) {
			TestReport report = project.getTestReportsInProj().get( i );
			String userId = report.getUserId();
			String tag = report.getTag();
			if ( tag.equals( "审核通过") && selectedWorkers.contains( userId )) {
				String dupTag = report.getDuplicate();
				nonDupSet.add( dupTag );
			}
		}
		
		int count = nonDupSet.size();
		return count;
	}
}
