package com.learner;

import java.util.ArrayList;

import com.data.Capability;
import com.data.DomainKnowledge;
import com.data.TestCase;
import com.data.TestTask;
import com.dataProcess.SimilarityMeasure;

public class CapRevData {
	public Object[] prepareAttributeData ( ArrayList<TestCase> caseList, TestTask task ) {
		ArrayList<String> attributeName = new ArrayList<String>();
		attributeName.add( "numProject" );
		attributeName.add( "numReport" );
		attributeName.add( "numBug" );
		attributeName.add( "percBug" );
		
		attributeName.add( "numProject-1" );
		attributeName.add( "numReport-1" );
		attributeName.add( "numBug-1" );
		attributeName.add( "percBug-1" );
		
		attributeName.add( "numProject-2" );
		attributeName.add( "numReport-2" );
		attributeName.add( "numBug-2" );
		attributeName.add( "percBug-2" );
		
		attributeName.add( "numProject-4" );
		attributeName.add( "numReport-4" );
		attributeName.add( "numBug-4" );
		attributeName.add( "percBug-4" );
		
		attributeName.add( "durationLastAct");		
		attributeName.add( "relevant");
		
		attributeName.add( "category");
		
		ArrayList<String[]> attributeValue = new ArrayList<String[]>();
		
		for ( int i =0;  i < caseList.size(); i++ ) {
			if ( i % 1000 == 0 )
				System.out.println( "processing " + i + " test case!");
			TestCase testCase = caseList.get( i);
			
			Capability capInfo = testCase.getWorker().getCapInfo();
			
			int index =0;
			String[] value = new String[attributeName.size()];
			for ( int j =0; j < capInfo.getNumProject().length; j++ ) {
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
