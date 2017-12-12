package com.topicModelData;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.data.Constants;
import com.data.TestProject;
import com.dataProcess.ReportSegment;
import com.dataProcess.TestProjectReader;
import com.taskReverse.FinalTermListGeneration;

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
		
		HashMap<Integer, LinkedHashMap<Integer, Integer>> termFreqList = new HashMap<Integer, LinkedHashMap<Integer, Integer>>();
		for ( int i =0; i < historyProjectList.size(); i++ ) {
			TestProject project = historyProjectList.get( i );
			int index = project.getProjectName().indexOf( "-");
			String projIndexStr = project.getProjectName().substring(0, index);
			int projIndex = Integer.parseInt( projIndexStr );
			
			HashMap<String, Integer> termFreq = segmentTool.segmentTestProjectMap(project);
			
			//存储成按照finalTermList的term出现顺序的linked hash map
			LinkedHashMap<Integer, Integer> termFreqSeq = new LinkedHashMap<Integer, Integer>();
			for ( int j =0; j < finalTermList.size(); j++ ) {
				String term = finalTermList.get( j);
				if ( termFreq.containsKey( term )) {
					termFreqSeq.put( j, termFreq.get(term ) );
				}
			}
			termFreqList.put( projIndex-1, termFreqSeq );
		}
		
		List<HashMap.Entry<Integer, LinkedHashMap<Integer, Integer>>> newTermFreqList = new ArrayList<HashMap.Entry<Integer, LinkedHashMap<Integer, Integer>>>(termFreqList.entrySet());

		Collections.sort( newTermFreqList, new Comparator<HashMap.Entry<Integer, LinkedHashMap<Integer, Integer>>>() {   
			public int compare(HashMap.Entry<Integer, LinkedHashMap<Integer, Integer>> o1, HashMap.Entry<Integer, LinkedHashMap<Integer, Integer>> o2) {      
			        //return (o2.getValue() - o1.getValue()); 
			        return o1.getKey().compareTo(o2.getKey()) ;
			    }
			}); 
		
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( termFreqFile ));
			for ( int i =0; i < newTermFreqList.size(); i++ ) {
				Integer index  = newTermFreqList.get( i ).getKey();
				LinkedHashMap<Integer, Integer> item = newTermFreqList.get( i).getValue();
				
				writer.write( index + " ");
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
			for ( int i =0; i < newTermFreqList.size(); i++ ) {
				Integer index  = newTermFreqList.get( i ).getKey();
				writer.write( index + " " + index );
				
				writer.newLine();
			}			
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void prepareTopicTestData (String termFreqFile, String termFile, String indexFile ) {
		
	}
	
	public static void main ( String args[] ) {
		String topicFolder = "data/output/topic/";
		TopicDataPrepare topicTool = new TopicDataPrepare();
		topicTool.prepareTopicTrainData( topicFolder + "termFreq.txt", topicFolder + "terms.txt",  topicFolder + "index.txt");
	}
}
