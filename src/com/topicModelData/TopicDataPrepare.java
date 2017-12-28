package com.topicModelData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.data.Constants;
import com.data.DomainKnowledge;
import com.data.TestProject;
import com.data.TestReport;
import com.dataProcess.ReportSegment;
import com.dataProcess.TestProjectReader;
import com.taskReorganize.FinalTermListGeneration;

/*
 * 生成运行python lda topic model 需要的数据集
 */
public class TopicDataPrepare {
	/*
	 * topic train data 是所有的task description组成的文档
	 */
	public void prepareTopicTrainData (String termFreqFile, String termFile, String indexFile ) {
		FinalTermListGeneration termTool = new FinalTermListGeneration();
		ArrayList<String> finalTermList = termTool.loadFinalTermList();
		
		TestProjectReader projReader = new TestProjectReader();
		ArrayList<TestProject> historyProjectList = projReader.loadTestProjectAndTaskList( Constants.TOTAL_PROJECT_FOLDER, Constants.TOTAL_TASK_DES_FOLDER );
		
		ReportSegment segmentTool = new ReportSegment();
		
		LinkedHashMap<Integer, LinkedHashMap<Integer, Integer>> termFreqList = new LinkedHashMap<Integer, LinkedHashMap<Integer, Integer>>();
		LinkedHashMap<Integer, String> indexList = new LinkedHashMap<Integer, String>();
		
		for ( int i =0; i < historyProjectList.size(); i++ ) {
			TestProject project = historyProjectList.get( i );
			int index = project.getProjectName().indexOf( "-");
			String projIndexStr = project.getProjectName().substring(0, index);
			int projIndex = Integer.parseInt( projIndexStr );
			
			HashMap<String, Integer> termFreq = segmentTool.segmentTestProjectMap(project);
			
			LinkedHashMap<Integer, Integer> termFreqSeq = this.transferToTermListRank(finalTermList, termFreq);
			termFreqList.put( i, termFreqSeq );
			indexList.put( i, new Integer(projIndex).toString() );
		}
		this.storeTopicData(termFreqFile, termFile, indexFile, termFreqList, finalTermList, indexList);
	}
	
	//存储成按照finalTermList的term出现顺序的linked hash map
	public LinkedHashMap<Integer, Integer> transferToTermListRank ( ArrayList<String> finalTermList, HashMap<String, Integer> termFreq) {
		LinkedHashMap<Integer, Integer> termFreqSeq = new LinkedHashMap<Integer, Integer>();
		for ( int j =0; j < finalTermList.size(); j++ ) {
			String term = finalTermList.get( j);
			if ( termFreq.containsKey( term )) {
				termFreqSeq.put( j, termFreq.get(term ) );
			}
		}
		return termFreqSeq;
	}
	
	public void prepareTopicTestData (String termFreqFile, String termFile, String indexFile , String testSetIndex ) {
		int endProjectIndex = -1;
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader( new File ( Constants.TRAIN_TEST_SET_SETTING_FILE )));
			String line = "";
			
			while ( ( line = br.readLine() ) != null ) {
				String[] temp = line.split( ",");
				if ( temp[0].trim().equals( testSetIndex )) {
					endProjectIndex = Integer.parseInt( temp[1].trim() ) -1;
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
		
		HashMap<String, ArrayList<TestReport>> userReportList = new HashMap<String, ArrayList<TestReport>>();
		
		TestProjectReader projReader = new TestProjectReader();
		ArrayList<TestProject> projectList = projReader.loadTestProjectAndTaskList( Constants.TOTAL_PROJECT_FOLDER, Constants.TOTAL_TASK_DES_FOLDER );
		
		FinalTermListGeneration termTool = new FinalTermListGeneration ();
		ArrayList<String> finalTermList = termTool.loadFinalTermList();
		
		for ( int i =0; i < projectList.size(); i++  ) {
			TestProject project = projectList.get( i );
			
			for ( int j = 0; j < project.getTestReportsInProj().size(); j++ ) {
				TestReport report = project.getTestReportsInProj().get( j );
			
				String userId = report.getUserId();
				
				ArrayList<TestReport> reportList = new ArrayList<TestReport>();
				if ( userReportList.containsKey( userId )) {
					reportList = userReportList.get( userId );
				}
				reportList.add( report );
				userReportList.put( userId, reportList );
			}
		}
		
		LinkedHashMap<Integer, LinkedHashMap<Integer, Integer>> domainInfoList = new LinkedHashMap<Integer, LinkedHashMap<Integer, Integer>>();
		LinkedHashMap<Integer, String> indexList = new LinkedHashMap<Integer, String>();
		
		ReportSegment segTool = new ReportSegment();
		//generate the domain terms of all the historical reports by each user
		int index = 0;
		for ( String userId: userReportList.keySet() ) {
			ArrayList<TestReport> reportList = userReportList.get( userId );
			HashMap<String, Integer> domainTerms = segTool.segmentTestReportListMap(reportList);
			
			LinkedHashMap<Integer, Integer> domainTermsSeq = this.transferToTermListRank(finalTermList, domainTerms);
			if ( domainTermsSeq.size() != 0 ) {
				domainInfoList.put( index, domainTermsSeq );
				indexList.put( index,  userId  );
				index++;
			}
		}
		
		this.storeTopicData(termFreqFile, termFile, indexFile, domainInfoList, finalTermList, indexList);
	}
	
	public void storeTopicData  ( String termFreqFile, String termFile, String indexFile, LinkedHashMap<Integer, LinkedHashMap<Integer, Integer>> termFreqList, 
			ArrayList<String> finalTermList, LinkedHashMap<Integer, String> indexList ) {
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( termFreqFile ));
			for ( Integer index : termFreqList.keySet() ) {
				LinkedHashMap<Integer, Integer> item = termFreqList.get( index );
				
				writer.write( item.size() + " ");
				for ( Integer key : item.keySet() ) {
					writer.write( key + ":" + item.get( key ) + " ");
				}
				writer.newLine();
			}			
			writer.flush();
			writer.close();
			
			writer = new BufferedWriter( new FileWriter ( termFile ));
			for ( int i =0; i < finalTermList.size(); i++ ) {
				writer.write( finalTermList.get( i ) );
				writer.newLine();
			}			
			writer.flush();
			writer.close();
			
			//为了适应python lda的逻辑
			writer = new BufferedWriter( new FileWriter ( indexFile ));
			for ( int index : indexList.keySet() ) {
				String value = indexList.get( index );
				writer.write( index + " " + value );
				
				writer.newLine();
			}			
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public HashMap<String, Integer> filterUninformativeTerms ( HashMap<String, Integer> domainTerms, ArrayList<String> finalTermList) {
		HashMap<String, Integer> newDomainTerms = new HashMap<String, Integer>();
		
		for ( String term: domainTerms.keySet() ) {
			if ( finalTermList.contains( term )) {
				newDomainTerms.put( term, domainTerms.get( term ));
			}
		}
		return newDomainTerms;		
	}
	
	public HashMap<String, ArrayList<Double>> loadTopicDistribution ( String fileName) {
		HashMap<String, ArrayList<Double>> topicDisForWorker = new HashMap<String, ArrayList<Double>>();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader( new File ( fileName )));
			String line = "";
			
			while ( ( line = br.readLine() ) != null ) {
				String[] temp = line.split( ":");
				String userId = temp[0];
				String[] dis = temp[1].split( " ");
				
				ArrayList<Double> topicDis = new ArrayList<Double>();
				for ( int i =0; i < dis.length; i++ ) {
					Double value = Double.parseDouble( dis[i] );
					topicDis.add( value );
				}
				topicDisForWorker.put( userId, topicDis );
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return topicDisForWorker;
	}
	
	public static void main ( String args[] ) {
		String topicFolderTrain = "data/output/topic/train/";
		String topicFolderTest = "data/output/topic/test/";
		TopicDataPrepare topicTool = new TopicDataPrepare();
		topicTool.prepareTopicTrainData( topicFolderTrain + "termFreq.txt", topicFolderTrain + "terms.txt",  topicFolderTrain + "index.txt");
		System.out.println( "train data is done!");
		
		topicTool.prepareTopicTestData( topicFolderTest + "termFreq.txt", topicFolderTest + "terms.txt",  topicFolderTest + "index.txt", "19");
	}
}
