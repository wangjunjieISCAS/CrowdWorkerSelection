package com.learner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.data.Capability;
import com.data.DomainKnowledge;
import com.data.TestCase;
import com.data.TestTask;
import com.dataProcess.SimilarityMeasure;

public class PartAttributeData {
	/*
	 * CapRevTopicData是自己生成的，
	 * 对于PartAttributeData是根据全部attributes的文件，提取出来的数据
	 */
	//attributeList需要包含category
	public void prepareAttributeData ( ArrayList<String> attributeList, String projectName,  Integer testSetIndex, String type ) {
		String totalWekaTrainFile = "data/input/weka/" + testSetIndex + "/train-" + projectName + ".csv";
		String totalWekaTestFile = "data/input/weka/" + testSetIndex + "/test-" + projectName + ".csv";
		
		HashMap<String, ArrayList<String>> trainTotalAttrValues = this.loadWekaData( totalWekaTrainFile );
		HashMap<String, ArrayList<String>> testTotalAttrValues = this.loadWekaData( totalWekaTestFile );
		
		ArrayList<String[]> trainAttrValue = this.generateNewWekaData( trainTotalAttrValues, attributeList);
		ArrayList<String[]> testAttrValue = this.generateNewWekaData( testTotalAttrValues, attributeList);
		
		WekaDataPrepare dataTool = new WekaDataPrepare();
		
		String wekaTrainFile = "data/input/weka/" + testSetIndex.toString() + "/part/" + type + "-" + "train-" + projectName + ".csv";
		Object[] trainResult = new Object[2];
		trainResult[0] = attributeList;
		trainResult[1] = trainAttrValue;		
		dataTool.generateWekaDataFile( trainResult, wekaTrainFile );
		
		String wekaTestFile = "data/input/weka/" + testSetIndex.toString() + "/part/" + type + "-" + "test-" + projectName + ".csv";
		Object[] testResult = new Object[2];
		testResult[0] = attributeList;
		testResult[1] = testAttrValue;		
		dataTool.generateWekaDataFile( testResult, wekaTestFile );
	}
	
	public ArrayList<String[]> generateNewWekaData ( HashMap<String, ArrayList<String>> totalAttrValues, ArrayList<String> attributeList ) {
		ArrayList<String[]> attributeValue = new ArrayList<String[]>();
		
		String name = attributeList.get(0);
		//data instance 的数目
		for ( int i =0; i < totalAttrValues.get(name).size(); i++ ) {
			String[] values = new String[attributeList.size()];
			for ( int j =0; j < attributeList.size(); j++ ) {
				String attrName = attributeList.get( j );
				ArrayList<String> attrValue = totalAttrValues.get( attrName );
				values[j] = attrValue.get( i );
			}
			attributeValue.add( values );
		}
		
		return attributeValue;
	}
	
	public HashMap<String, ArrayList<String>> loadWekaData ( String fileName ) {
		HashMap<String, ArrayList<String>> attributeValues = new HashMap<String, ArrayList<String>>();
		ArrayList<String> attributeName = new ArrayList<String>();
		
		try {			
			BufferedReader reader = new BufferedReader(new FileReader(new File( fileName )));
			String line = "";
			
			boolean isTitle = true;
			while ((line = reader.readLine()) != null) {
				String[] temp = line.split(",");
				
				if ( isTitle == true ) {
					for ( int i =0; i < temp.length; i++ ) {
						String name = temp[i].trim();
						attributeName.add( name );
						
						ArrayList<String> value = new ArrayList<String>();
						attributeValues.put( name, value );
					}
					isTitle = false;
					continue;
				}
				
				for ( int i =0; i < temp.length; i++ ) {
					String name = attributeName.get( i );
					ArrayList<String> value = attributeValues.get( name );
					
					value.add( temp[i].trim() );
					attributeValues.put( name, value);
				}				
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return attributeValues;
	}
}
