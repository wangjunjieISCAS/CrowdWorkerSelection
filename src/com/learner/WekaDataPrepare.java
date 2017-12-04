package com.learner;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.data.Capability;
import com.data.DomainKnowledge;
import com.data.TestCase;
import com.data.TestTask;
import com.dataProcess.SimilarityMeasure;

public class WekaDataPrepare {
	
	public void generateWekaDataFile ( Object[] attributeNameValue, String fileName ) {
		ArrayList<String> attributeName = (ArrayList<String>) attributeNameValue[0];
		ArrayList<String[]> attributeValue = (ArrayList<String[]>) attributeNameValue[1];
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter( new FileWriter ( fileName ));
			
			for ( int i =0; i < attributeName.size() -1 ; i ++) {
				writer.write( attributeName.get( i ) + ",");		
			}
			writer.write( attributeName.get( attributeName.size()-1 ));
			writer.newLine();
			
			for ( int i =0; i < attributeValue.size() ; i++ ) {
				String[] value = attributeValue.get( i );
				for ( int j =0; j < value.length -1 ; j++ ) {
					writer.write( value[j] + ",");
				}
				writer.write( value[value.length-1]);
				writer.newLine();
				writer.flush();
			}
			writer.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
