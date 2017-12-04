package com.learner;

import java.util.ArrayList;

import com.data.Capability;
import com.data.DomainKnowledge;
import com.data.TestCase;
import com.data.TestTask;
import com.dataProcess.SimilarityMeasure;

public class RevData {
	public Object[] prepareAttributeData ( ArrayList<TestCase> caseList, TestTask task ) {
		ArrayList<String> attributeName = new ArrayList<String>();
		attributeName.add( "relevant");
		attributeName.add( "category");
		
		ArrayList<String[]> attributeValue = new ArrayList<String[]>();
		
		for ( int i =0;  i < caseList.size(); i++ ) {
			TestCase testCase = caseList.get( i);
			
			int index =0;
			String[] value = new String[attributeName.size()];
			
			DomainKnowledge domainInfo = testCase.getWorker().getDomainKnInfo();
			SimilarityMeasure similarityTool = new SimilarityMeasure();
			Double sim = similarityTool.cosineSimilarity( domainInfo.getDomainKnowledge(), task.getTaskDescription() );
			value[index++] = sim.toString();
			
			if ( testCase.getTestOracle().equals( "���ͨ��")) {
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
