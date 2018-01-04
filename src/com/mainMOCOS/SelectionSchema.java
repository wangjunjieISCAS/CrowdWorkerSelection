package com.mainMOCOS;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.data.Constants;
import com.data.CrowdWorker;
import com.data.Phone;
import com.data.TestProject;
import com.data.TestReport;
import com.dataProcess.CrowdWorkerHandler;
import com.dataProcess.TestProjectReader;
import com.testCaseDataPrepare.CrowdWokerExtraction;


public class SelectionSchema {
	protected LinkedHashMap<String, CrowdWorker> candidateWorkerList;
	protected ArrayList<String> candidateIDs;
	
	public SelectionSchema ( ) {
		candidateWorkerList = new LinkedHashMap<String, CrowdWorker>();
		candidateIDs = new ArrayList<String>();
	}
	
	public void workSelectionApproach ( TestProject project, ArrayList<TestProject> historyProjectList, String testSetIndex, String taskId ) {
		candidateWorkerList  = this.obtainCandidateWorkers(project, historyProjectList, testSetIndex);
		
		CandidateIDChoose chooseTool = new CandidateIDChoose();
		//ArrayList<String> candidateIDs = chooseTool.obtainCandidateIDsBasedLastActivity(historyProjectList, candidateWorkerList, project);
		candidateIDs = chooseTool.obtainCandidateIDsBasedLastActivityAndRelevance(historyProjectList, candidateWorkerList, project);
		//ArrayList<String> candidateIDs = chooseTool.obtainCandidateIDsRandom(candidateWorkerList);
		
		System.out.println ( "CandidateIDs size is: " + candidateIDs.size() ); 	
	}	
	
	public LinkedHashMap<String, CrowdWorker> obtainCandidateWorkers (  TestProject project, ArrayList<TestProject> historyProjectList, String testSetIndex ) {
		CrowdWorkerHandler workerHandler = new CrowdWorkerHandler();
		HashMap<String, CrowdWorker> historyWorkerList = workerHandler.loadCrowdWorkerInfo( Constants.WORKER_INFO_FOLDER + "/" + testSetIndex + "/workerPhone.csv", 
				Constants.WORKER_INFO_FOLDER + "/" + testSetIndex + "/workerCap.csv", Constants.WORKER_INFO_FOLDER + "/" + testSetIndex + "/workerDomain.csv" );
		System.out.println ( "HistoryWorkerList is done! " );
		
		CrowdWokerExtraction workerTool = new CrowdWokerExtraction();
		CrowdWorker defaultWorker = workerTool.obtainDefaultCrowdWorker( historyWorkerList );
		//obtain candidate worker, besides the history worker, there could be worker who join the platform for the first time in this project
		LinkedHashMap<String, CrowdWorker> candidateWorkerList = new LinkedHashMap<String, CrowdWorker>();
		for ( String userId : historyWorkerList.keySet() ) {
			CrowdWorker hisWorker = historyWorkerList.get( userId );
			
			CrowdWorker worker = new CrowdWorker ( hisWorker.getWorkerId(), hisWorker.getPhoneInfo(), hisWorker.getCapInfo(), hisWorker.getDomainKnInfo() );
			candidateWorkerList.put( userId,  worker );
		}
		for ( int i =0; i < project.getTestReportsInProj().size(); i++ ) {
			TestReport report = project.getTestReportsInProj().get(i);
			String userId = report.getUserId();
			if ( candidateWorkerList.containsKey( userId ))
				continue;
			
			Phone phoneInfo = new Phone ( report.getPhoneType(), report.getOS(), report.getNetwork(), report.getISP() );
			CrowdWorker worker = new CrowdWorker ( userId, phoneInfo, defaultWorker.getCapInfo(), defaultWorker.getDomainKnInfo() );
			
			candidateWorkerList.put( userId, worker );
		}
		
		//workerHandler.storeCrowdWorkerInfo(candidateWorkerList, Constants.WORKER_PHONE_FILE, Constants.WORKER_CAP_FILE, Constants.WORKER_DOMAIN_FILE );
		System.out.println ( "CandidateWorkerList is done! " );
		
		return candidateWorkerList;
	}
	
	public void storeSelectionResults ( HashMap<Integer, ArrayList<ArrayList<String>>> selectionResults, String taskId, String type ) {
		List<HashMap.Entry<Integer, ArrayList<ArrayList<String>>>> selectionResultsList = new ArrayList<HashMap.Entry<Integer, ArrayList<ArrayList<String>>>>(selectionResults.entrySet() );
		Collections.sort( selectionResultsList, new Comparator<HashMap.Entry<Integer, ArrayList<ArrayList<String>>>>() {   
			public int compare(HashMap.Entry<Integer, ArrayList<ArrayList<String>>> o1, HashMap.Entry<Integer, ArrayList<ArrayList<String>>> o2) {      
			        //return (o2.getValue() - o1.getValue()); 
			        return o1.getKey().compareTo(o2.getKey() ) ;
			    }
			}); 
		
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( Constants.SELECTION_RESULTS_FOLDER + "/" + type + "/" + taskId + ".txt"));
			
			for ( int i =0; i < selectionResultsList.size(); i++ ) {
				int selectionNum = selectionResultsList.get(i).getKey();
				writer.write( selectionNum + ":");
				
				ArrayList<String> workerList = selectionResultsList.get(i).getValue().get(0);
				for ( int j=0; j < workerList.size(); j++ ) {
					writer.write( workerList.get( j) + " ");
				}
				writer.newLine();
			}	
			
			writer.flush();
			writer.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public HashMap<Integer, ArrayList<ArrayList<String>>> readSelectionResults ( String type, String taskId ) {
		HashMap<Integer, ArrayList<ArrayList<String>>> selectionResults = new HashMap<Integer, ArrayList<ArrayList<String>>>();
		try {			
			BufferedReader reader = new BufferedReader(new FileReader(new File( Constants.SELECTION_RESULTS_FOLDER + "/"+ type + "/" + taskId + ".txt" )));
			String line = "";
			while ((line = reader.readLine()) != null) {
				String[] temp = line.split(":");
				int selectionNum  = Integer.parseInt( temp[0] );
				
				String[] workerInfo = temp[1].split( ",");
				ArrayList<String> workerList = new ArrayList<String>( Arrays.asList( workerInfo ));
				
				ArrayList<ArrayList<String>> resultInfo = new ArrayList<ArrayList<String>>();
				if ( selectionResults.containsKey( selectionNum )) {
					resultInfo = selectionResults.get( selectionNum );
				}
				resultInfo.add( workerList );
				selectionResults.put( selectionNum, resultInfo );
			}
			reader.close();
			
			return selectionResults;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void workerSelectionForMultipleProjects ( Integer testSetIndex ) {
		int beginTestProjIndex =0, endTestProjIndex = 0;
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader( new File ( Constants.TRAIN_TEST_SET_SETTING_FILE)));
			String line = "";
			
			boolean isFirstLine = true;
			while ( ( line = br.readLine() ) != null ) {
				if ( isFirstLine == true ) {
					isFirstLine = false;
					continue;
				}
				String[] temp = line.split( ",");
				Integer testSetNum = Integer.parseInt( temp[0] );
				if ( testSetNum.equals( testSetIndex )) {
					beginTestProjIndex = Integer.parseInt(  temp[1]);
					endTestProjIndex = Integer.parseInt(  temp[2] );
					
					break;
				}
			}			
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TestProjectReader projReader = new TestProjectReader();
		ArrayList<TestProject> historyProjectList = projReader.loadTestProjectAndTaskListBasedId( 1, beginTestProjIndex -1, Constants.TOTAL_PROJECT_FOLDER, Constants.TOTAL_TASK_DES_FOLDER );
		System.out.println( "historyProjectList is : " + historyProjectList.size() );
		
		for ( int i = beginTestProjIndex; i <= endTestProjIndex; i++  ) {
			//System.out.println( "=================================================================\nWorker Selection for project: " + i  );
			TestProject project = projReader.loadTestProjectAndTaskBasedId( i , Constants.TOTAL_PROJECT_FOLDER, Constants.TOTAL_TASK_DES_FOLDER  );
			this.workSelectionApproach(project, historyProjectList, testSetIndex.toString(),  new Integer(i).toString()  );
		}		
	}
}
