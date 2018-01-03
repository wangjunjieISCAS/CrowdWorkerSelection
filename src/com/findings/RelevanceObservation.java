package com.findings;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.data.Constants;
import com.data.CrowdWorker;
import com.data.TestProject;
import com.data.TestReport;
import com.dataProcess.CrowdWorkerHandler;
import com.dataProcess.SimilarityMeasure;
import com.dataProcess.TestProjectReader;

public class RelevanceObservation {
	public void obtainRelationshipRelevanceBugDetection ( String projectFolder, String taskFolder ) {
		TestProjectReader projReader = new TestProjectReader();
		ArrayList<TestProject> projList = projReader.loadTestProjectAndTaskList(projectFolder, taskFolder);
		
		String testSetIndex = "20";
		CrowdWorkerHandler workerHandler = new CrowdWorkerHandler();
		HashMap<String, CrowdWorker> workerList = workerHandler.loadCrowdWorkerInfo( Constants.WORKER_INFO_FOLDER + "/" + testSetIndex + "/workerPhone.csv", 
				Constants.WORKER_INFO_FOLDER + "/" + testSetIndex + "/workerCap.csv", Constants.WORKER_INFO_FOLDER + "/" + testSetIndex + "/workerDomain.csv" );
		
		HashMap<String, Double> bugAvgRelevance = new HashMap<String, Double>();
		HashMap<String, Double> noBugAvgRelevance = new HashMap<String, Double>();
		
		SimilarityMeasure simTool = new SimilarityMeasure();
		
		for ( int i =0; i < projList.size(); i++ ) {
			TestProject proj = projList.get( i );
			
			double bugTotalValue = 0.0, noBugTotalValue = 0.0;
			int bugReportNum = 0, noBugReportNum = 0;
			
			ArrayList<String> testTask = proj.getTestTask().getTaskDescription();
			
			//会出现同一个人既提交了含有缺陷的报告，又提交了不含缺陷的报告
			HashSet<String> bugWorkerSet = new HashSet<String>();
			HashSet<String> totalWorkerSet = new HashSet<String>();
			
			for ( int j =0; j < proj.getTestReportsInProj().size(); j++ ) {
				TestReport report = proj.getTestReportsInProj().get( j );
				String userId = report.getUserId();
				String tag = report.getTag();

				totalWorkerSet.add( userId );
				if ( tag.equals( "审核通过")) {
					bugWorkerSet.add( userId );	
				}
			}
			
			for ( String userId: totalWorkerSet ) {
				ArrayList<String> domainInfo = new ArrayList<String>();
				if ( workerList.containsKey( userId )) {
					domainInfo = workerList.get( userId).getDomainKnInfo().getDomainKnowledge();
				}
				
				double simValue = simTool.cosineSimilarity( domainInfo, testTask );
				if ( bugWorkerSet.contains( userId )) {
					bugTotalValue += simValue;
					bugReportNum ++;
				}else {
					noBugTotalValue += simValue;
					noBugReportNum ++;
				}
			}
			double bugAvgValue = bugTotalValue / bugReportNum;
			double noBugAvgValue = noBugTotalValue / noBugReportNum;
			
			bugAvgRelevance.put( proj.getProjectName(), bugAvgValue );
			noBugAvgRelevance.put( proj.getProjectName(), noBugAvgValue );
		}
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter( new FileWriter ( "data/output/findings/relevanceForProject.csv" ));
			
			writer.write( "projectName" + "," + "bugAvgRelevance" + "," + "noBugAvgRelevance" );
			writer.newLine();
			for ( String projectName : bugAvgRelevance.keySet() ) {
				writer.write( projectName + "," + bugAvgRelevance.get( projectName ) +  "," + noBugAvgRelevance.get( projectName ) );
				writer.newLine();
			}
			writer.flush();
			writer.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static void main ( String args[] ) {
		RelevanceObservation revTool = new RelevanceObservation();
		revTool.obtainRelationshipRelevanceBugDetection( Constants.TOTAL_PROJECT_FOLDER , Constants.TOTAL_TASK_DES_FOLDER);
	}
}
