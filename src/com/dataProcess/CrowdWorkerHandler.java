package com.dataProcess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.data.Capability;
import com.data.Constants;
import com.data.CrowdWorker;
import com.data.DomainKnowledge;
import com.data.Phone;
import com.data.TestProject;
import com.taskReorganize.FinalTermListGeneration;
import com.testCaseDataPrepare.CrowdWokerExtraction;

/*
 * the method loadCrowdWorkerInfo and storeCrowdWorkerInfo are the tool method
 * the method generateHistoricalWorkers is to generate historical worker's information and store them
 */
public class CrowdWorkerHandler {
	public HashMap<String, CrowdWorker> loadCrowdWorkerInfo ( String phoneFile, String capFile, String domainFile ) {
		HashMap<String, CrowdWorker> candidateWorkers = new HashMap<String, CrowdWorker>();
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(new File( phoneFile)));
			String line = "";
			while ((line = reader.readLine()) != null) {
				String[] temp = line.split(",");
				String userId = temp[0];

				if (temp.length < 5)
					temp = new String[] { userId, "", "", "", "" };
				 
				Phone phoneInfo = new Phone(temp[1], temp[2], temp[3], temp[4]);
				CrowdWorker workerInfo = new CrowdWorker(userId);
				workerInfo.setPhoneInfo(phoneInfo);
				candidateWorkers.put(userId, workerInfo);
			}
			
			reader = new BufferedReader(new FileReader(new File(capFile)));
			line = "";
			while ((line = reader.readLine()) != null) {
				String[] temp = line.split(",");
				String userId = temp[0];
				Integer[] numProject = this.newZeroArrays();
				Integer[] numReport = this.newZeroArrays();
				Integer[] numBug = this.newZeroArrays();
				Double[] percBug = new Double[Constants.CAP_SIZE_PER_TYPE];
				for ( int i =0; i < percBug.length; i++ )
					percBug[i] = 0.0;
				
				int index = 0;
				for ( int i =1; i < temp.length-1; i+=4 ) {
					numProject[index] = Integer.parseInt( temp[i] );
					numReport[index] = Integer.parseInt( temp[i+1] ) ;
					numBug[index] = Integer.parseInt( temp[i+2]);
					percBug[index] = Double.parseDouble( temp[i+3]);
					index++;
				}
				Integer durationDays = Integer.parseInt( temp[temp.length-1]);
				Capability capInfo = new Capability( numProject, numReport, numBug, percBug, durationDays );

				CrowdWorker workerInfo = candidateWorkers.get(userId);
				workerInfo.setCapInfo(capInfo);

				candidateWorkers.put(userId, workerInfo);
			}

			reader = new BufferedReader(new FileReader(new File( domainFile)));
			line = "";
			while ((line = reader.readLine()) != null) {
				String[] temp = line.split(",");
				String userId = temp[0];

				ArrayList<String> domainTerms = new ArrayList<String>();
				for (int i = 1; i < temp.length; i++) {
					domainTerms.add(temp[i]);
				}
				DomainKnowledge domainInfo = new DomainKnowledge(domainTerms);

				CrowdWorker workerInfo = candidateWorkers.get(userId);
				/*
				if (workerInfo == null)
					continue;
				 */
				workerInfo.setDomainKnInfo(domainInfo);

				candidateWorkers.put(userId, workerInfo);
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return candidateWorkers;	
	}
	
	public void storeCrowdWorkerInfo ( HashMap<String, CrowdWorker> candidateWorkerList, String phoneFile, String capFile, String domainFile ) {
		try {
			BufferedWriter phoneWriter = new BufferedWriter( new FileWriter ( phoneFile ));
			BufferedWriter capWriter = new BufferedWriter( new FileWriter ( capFile ));
			BufferedWriter domainWriter = new BufferedWriter( new FileWriter ( domainFile ));
			
			for ( String userId : candidateWorkerList.keySet() ) {
				CrowdWorker worker = candidateWorkerList.get( userId );
				
				phoneWriter.write( userId + ",");
				Phone phoneInfo = worker.getPhoneInfo();
				phoneWriter.write( phoneInfo.getPhoneType() + "," );
				phoneWriter.write( phoneInfo.getOS() + ",");
				phoneWriter.write( phoneInfo.getNetwork() + "," );
				phoneWriter.write( phoneInfo.getISP() );
				phoneWriter.newLine();
				
				capWriter.write( userId + ",");
				Capability capInfo = worker.getCapInfo();
				for ( int i =0; i < capInfo.getNumProject().length; i++ ) {
					capWriter.write( capInfo.getNumProject()[i] + ",");
					capWriter.write( capInfo.getNumReport()[i] +",");
					capWriter.write( capInfo.getNumBug()[i] +",");
					capWriter.write( capInfo.getPercBug()[i].toString() + "," );
				}
				capWriter.write( capInfo.getDurationLastAct().toString() );
				capWriter.newLine();
				
				domainWriter.write( userId + ",");
				DomainKnowledge domainInfo = worker.getDomainKnInfo();
				for ( int i =0; i < domainInfo.getDomainKnowledge().size(); i++ ) {
					domainWriter.write( domainInfo.getDomainKnowledge().get( i ) + ",");
				}
				domainWriter.newLine();
			}
			
			phoneWriter.flush();
			phoneWriter.close();
			
			capWriter.flush();
			capWriter.close();
			
			domainWriter.flush();
			domainWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void generateHistoricalWorkers ( String projectFolder, int beginProjectIndex, int endProjectIndex, int setIndex, ArrayList<String> finalTermList, String curTimeStr ) {
		ArrayList<TestProject> historyProjectList = new ArrayList<TestProject>();
		TestProjectReader projReader = new TestProjectReader();
		
		File projectsFolder = new File ( projectFolder );
		if ( projectsFolder.isDirectory() ){
			String[] projectFileList = projectsFolder.list();
			for ( int i = 0; i< projectFileList.length; i++ ){
				String projectFileName = projectFolder + "/" + projectFileList[i];
				
				String[] temp = projectFileList[i].split( "-");
				int index = Integer.parseInt( temp[0].trim() );
				
				if ( index >= beginProjectIndex && index <= endProjectIndex ) {
					TestProject project = projReader.loadTestProject( projectFileName ); 
					historyProjectList.add( project );
				}				
			}				
		}			
		
		SimpleDateFormat formatLine = new SimpleDateFormat ("yyyy/MM/dd HH:mm");
		Date curTime = null;
		try {
			curTime = formatLine.parse( curTimeStr );
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		CrowdWokerExtraction workerTool = new CrowdWokerExtraction( );
		HashMap<String, CrowdWorker> historyWorkerList = workerTool.obtainCrowdWokerInfo( historyProjectList, finalTermList, curTime );
		
		this.storeCrowdWorkerInfo( historyWorkerList, Constants.WORKER_INFO_FOLDER + "/" + setIndex+ "/workerPhone.csv", 
				Constants.WORKER_INFO_FOLDER + "/" + setIndex+ "/workerCap.csv", Constants.WORKER_INFO_FOLDER + "/" + setIndex+ "/workerDomain.csv" );
		System.out.println ( "HistoryWorkerList is done!");
	}
	
	
	/*
	 * 生成第11-20个testSet的trainSet对应的crowd worker的信息，根据在TrainTestOrganize中对于train set和test set的划分
	 */
	public void generateHistoricalWorkerForAllTrainSet ( String projectFolder, ArrayList<String> finalTermList, String trainTestSetFile ) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader( new File ( trainTestSetFile )));
			String line = "";
			Integer beginTaskId = 1;
			
			boolean isFirstLine = true;
			while ( ( line = br.readLine() ) != null ) {
				if ( isFirstLine == true ) {
					isFirstLine = false;
					continue;
				}
				
				String[] temp = line.split( ",");
				Integer trainSetId = Integer.parseInt( temp[0].trim( ));
				if ( trainSetId < 11 )
					continue;
				
				Integer endTaskId = Integer.parseInt( temp[2].trim() ) -1 ;
				String curTimeStr = temp[3].trim();
				this.generateHistoricalWorkers( projectFolder, beginTaskId, endTaskId, trainSetId, finalTermList, curTimeStr);
			}				
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Integer[] newZeroArrays (  ) {
		Integer[] arrayName = new Integer[Constants.CAP_SIZE_PER_TYPE];
		for ( int i =0; i < arrayName.length; i++ ) {
			arrayName[i] = 0;
		}
		return arrayName;
	}
	
	public static void main ( String args[] ) throws ParseException {
		CrowdWorkerHandler workerTool = new CrowdWorkerHandler();
		
		String projectFolder = "data/input/experimental dataset";
		String trainTestSetFile = "data/output/findings/trainTestSet.csv";
		FinalTermListGeneration termTool = new FinalTermListGeneration();
		ArrayList<String> finalTermList = termTool.loadFinalTermList();
		//workerTool.generateHistoricalWorkers( projectFolder, 1, 532, 20, finalTermList, "2016/08/04 10:00");
		workerTool.generateHistoricalWorkerForAllTrainSet(projectFolder, finalTermList, trainTestSetFile);
	}
}
