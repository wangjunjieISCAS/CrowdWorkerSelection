package com.performanceEvaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.data.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PerformanceSummarize {
	//String[] folderNameList = { "MOCOSWeight-all", "MOCOSWeight-no cap", "MOCOSWeight-no rev", "MOCOSWeight-no div", "baselineSTRING", "baselineTOPIC" };
	String[] folderNameList = { "MOCOSWeight-all", "baselineSEKE", "baselineCOMPASC", "baselineISSRE", "baselineSTRING", "TRUE" };
	
	Integer[] insPoints = {3, 5, 10, 20, 50, 100 };
	
	public void summarizeBugDetectionRate ( ) {
		ArrayList<Integer> insPointList = new ArrayList<>(Arrays.asList( insPoints ) );
		
		HashMap<String, HashMap<Integer,Double>> bugRateList0 = new HashMap<String, HashMap<Integer,Double>>();
		HashMap<String, HashMap<Integer,Double>> bugRateList1 = new HashMap<String, HashMap<Integer,Double>>();
		HashMap<String, HashMap<Integer,Double>> bugRateList2 = new HashMap<String, HashMap<Integer,Double>>();
		HashMap<String, HashMap<Integer,Double>> bugRateList3 = new HashMap<String, HashMap<Integer,Double>>();
		HashMap<String, HashMap<Integer,Double>> bugRateList4 = new HashMap<String, HashMap<Integer,Double>>();
		HashMap<String, HashMap<Integer,Double>> bugRateList5 = new HashMap<String, HashMap<Integer,Double>>();
		
		for ( int i =0; i < folderNameList.length; i++ ) {
			String folderName = Constants.BUG_DETECTION_RATE_PERFORMANCE_FOLDER + "/" + folderNameList[i] ;
			File projectsFolder = new File ( folderName );
			String[] projectFileList = projectsFolder.list();
			
			String type = folderNameList[i];
			System.out.println( type );
			for ( int j = 0; j< projectFileList.length; j++ ){
				String projectName = projectFileList[j];
				String projectFileName = folderName + "/" + projectName;
				int index = projectFileList[j].indexOf( "-");
				projectName = projectName.substring( index+1 );
				//System.out.println( projectName );
				
				HashMap<Integer,Double> bugRate = new HashMap<Integer,Double>();
				try {			
					BufferedReader reader = new BufferedReader(new FileReader(new File( projectFileName )));
					String line = "";
					while ((line = reader.readLine()) != null) {
						String[] temp = line.split(",");
						int selectedNum = Integer.parseInt( temp[0]);
						Double prob = Double.parseDouble(temp[1]);

						//之所以需要用HashMap，不能用ArrayList，是因为可能有些insPoint没有（对于多目标优化的情况）
						if ( insPointList.contains( selectedNum )) {
							bugRate.put( selectedNum, prob );
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
				
				if ( type.equals( folderNameList[0] )) {
					bugRateList0.put( projectName,  bugRate );
				}else if ( type.equals( folderNameList[1])) {
					bugRateList1.put( projectName, bugRate );
				}else if ( type.equals( folderNameList[2])) {
					bugRateList2.put( projectName, bugRate );
				}else if ( type.equals( folderNameList[3])) {
					bugRateList3.put( projectName, bugRate );
				}else if ( type.equals( folderNameList[4])) {
					bugRateList4.put( projectName, bugRate );
				}else if ( type.equals( folderNameList[5])) {
					bugRateList5.put( projectName, bugRate );
				}
			}			
		}	
		
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( Constants.BUG_DETECTION_RATE_PERFORMANCE_FOLDER + "/" + "comparison.csv" ));
			
			writer.write( "ProjectName" + ",");
			for ( int i =0; i < insPointList.size() ; i++ ) {
				int ins = insPointList.get( i );
				writer.write( new Integer(ins).toString() + "-" + folderNameList[0]  + ",");
				writer.write( new Integer(ins).toString() + "-" + folderNameList[1]  + ",");
				writer.write( new Integer(ins).toString() + "-" + folderNameList[2]  + ",");
				writer.write( new Integer(ins).toString() + "-" + folderNameList[3]  + ",");
				writer.write( new Integer(ins).toString() + "-" + folderNameList[4]  + ",");
				writer.write( new Integer(ins).toString() + "-" + folderNameList[5]  + ",");
				writer.write( " " + ",");
			}
			writer.newLine();
			
			for ( String projectName : bugRateList0.keySet() ) {
				writer.write( projectName + ",");
				HashMap<Integer,Double> bugRate0 = bugRateList0.get( projectName );
				HashMap<Integer,Double> bugRate1 = bugRateList1.get( projectName );
				HashMap<Integer,Double> bugRate2 = bugRateList2.get( projectName );
				HashMap<Integer,Double> bugRate3 = bugRateList3.get( projectName );
				HashMap<Integer,Double> bugRate4 = bugRateList4.get( projectName );
				HashMap<Integer,Double> bugRate5 = bugRateList5.get( projectName );
				
				for (int i =0; i < insPointList.size(); i++ ) {
					int ins = insPointList.get( i );
					//System.out.println( ins );
					double value = 0.0;
					if ( bugRate0.containsKey( ins))
						value = bugRate0.get( ins );
					writer.write( value + ",");
					
					value = 0.0;
					if ( bugRate1.containsKey( ins )) {
						value = bugRate1.get( ins);
					}
					writer.write( value + ",");
					
					value = 0.0;
					if ( bugRate2.containsKey( ins)) {
						value = bugRate2.get( ins );
					}
					writer.write( value + ",");
					
					value = 0.0;
					if ( bugRate3.containsKey( ins)) {
						value = bugRate3.get( ins);
					}
					writer.write( value +",");
						
					value = 0.0;
					if ( bugRate4.containsKey( ins)) {
						value = bugRate4.get( ins );
					}
					writer.write( value + ",");
					
					value = 0.0;
					if ( bugRate5.containsKey( ins)) {
						value = bugRate5.get( ins );
					}
					writer.write( value + ",");
			
					writer.write( " " + "," );
				}
				
				writer.newLine();
				writer.flush();
			}
			
			writer.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static void main(String args[] ) {
		PerformanceSummarize performanceTool = new PerformanceSummarize();
		performanceTool.summarizeBugDetectionRate();
	}
}
