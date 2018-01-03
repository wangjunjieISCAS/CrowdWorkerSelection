package com.learner;

import java.util.ArrayList;
import java.util.HashMap;

import com.data.Capability;
import com.data.Constants;
import com.data.DomainKnowledge;
import com.data.TestCase;
import com.data.TestTask;
import com.dataProcess.SimilarityMeasure;

public class CapRevTopicData {
	public Object[] prepareAttributeData ( ArrayList<TestCase> caseList, TestTask task, ArrayList<Double> topicDisForThisTask ) {
		ArrayList<String> attributeName = new ArrayList<String>();
		
		for ( int i=0; i < Constants.CAP_SIZE_PER_TYPE; i++ ) {
			String attrName = "numProject-" + i ;
			attributeName.add( attrName );
			
			attrName = "numReport-" + i ;
			attributeName.add( attrName );
			
			attrName = "numBug-" + i;
			attributeName.add( attrName );
			
			attrName = "percBug-" + i ;
			attributeName.add( attrName );
		}
		attributeName.add( "durationLastAct");		
		attributeName.add( "relevant");
		
		for ( int i =0; i < Constants.TOPIC_NUMBER; i++ ) {
			attributeName.add( "topic-" + i );
		}
		
		attributeName.add( "category");
		
		ArrayList<String[]> attributeValue = new ArrayList<String[]>();
		
		for ( int i =0;  i < caseList.size(); i++ ) {
			if ( i % 1000 == 0 )
				System.out.println( "processing " + i + " test case!");
			TestCase testCase = caseList.get( i);
			
			Capability capInfo = testCase.getWorker().getCapInfo();
			
			int index =0;
			String[] value = new String[attributeName.size()];
			for ( int j =0; j < Constants.CAP_SIZE_PER_TYPE; j++ ) {
				value[index++] = capInfo.getNumProject()[j].toString();
				value[index++] = capInfo.getNumReport()[j].toString();
				value[index++] = capInfo.getNumBug()[j].toString();
				value[index++] = capInfo.getPercBug()[j].toString();
			}
			value[index++] = capInfo.getDurationLastAct().toString();
			
			DomainKnowledge domainInfo = testCase.getWorker().getDomainKnInfo();
			SimilarityMeasure similarityTool = new SimilarityMeasure();
			Double sim = similarityTool.cosineSimilarity( domainInfo.getDomainKnowledge(), task.getTaskDescription() );
			value[index++] = sim.toString();
			
			ArrayList<Double> topicForWorker = testCase.getTopicDistribution();
			ArrayList<Double> topicDif = similarityTool.topicDistance( topicForWorker, topicDisForThisTask );
			for ( int j =0; j < Constants.TOPIC_NUMBER; j++) {
				value[index++] = topicDif.get(j).toString();
			}
			
			if ( testCase.getTestOracle().equals( "ÉóºËÍ¨¹ý")) {
				value[index] = "yes";
			}else {
				value[index] = "no";
			}
			attributeValue.add( value );
		}
		
		Object[] result = new Object[2];
		result[0] = attributeName;
		result[1] = attributeValue;
		
		return result;
	}
}
