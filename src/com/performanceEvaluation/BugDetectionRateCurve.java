package com.performanceEvaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.data.Constants;

public class BugDetectionRateCurve {
	int maxIndex = 100;
	
	public void obtainBugDetectionRateCurve ( String folderName, String outFile   ) {
		File projectsFolder = new File ( folderName );
		String[] projectFileList = projectsFolder.list();
		
		HashMap<Integer, ArrayList<Double>> bugRate = new HashMap<Integer, ArrayList<Double>>();
		for ( int i =0; i <= maxIndex; i++ ) {
			ArrayList<Double> value = new ArrayList<Double>();
			bugRate.put( i, value);
		}
		
		for ( int i = 0; i< projectFileList.length; i++ ){
			String projectFileName = folderName + "/" + projectFileList[i];
			HashMap<Integer, Double> bugRateProject = this.obtainBudDetectionRateProject( projectFileName );
			
			for ( Integer index : bugRate.keySet() ) {
				ArrayList<Double> value = bugRate.get( index );
				
				if ( !bugRateProject.containsKey( index )) {
					value.add( 0.0);
				}else {
					value.add ( bugRateProject.get( index ) );
				}
			}
		}
		
		HashMap<Integer, Double> medianBugRate = new HashMap<Integer, Double>();
		int midIndex = bugRate.get( maxIndex ).size() / 2;
		for ( Integer index : bugRate.keySet() ) {
			ArrayList<Double> value = bugRate.get( index );
			Collections.sort( value );
			
			Double median = value.get( midIndex );
			medianBugRate.put( index , median );
		}
		
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( outFile ) );
			
			for ( Integer index : medianBugRate.keySet() ) {
				writer.write( index  + "," + medianBugRate.get( index ));
				writer.newLine();
			}
			
			writer.flush();			
			writer.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public HashMap<Integer,Double> obtainBudDetectionRateProject ( String fileName ) {
		HashMap<Integer,Double> bugRate = new HashMap<Integer,Double>();
		try {			
			BufferedReader reader = new BufferedReader(new FileReader(new File( fileName )));
			String line = "";
			while ((line = reader.readLine()) != null) {
				String[] temp = line.split(",");
				int selectedNum = Integer.parseInt( temp[0]);
				Double prob = Double.parseDouble(temp[1]);
				
				bugRate.put( selectedNum, prob );
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bugRate;
	}
	
	public static void main ( String args[]) {
		BugDetectionRateCurve curveTool = new BugDetectionRateCurve();
		
		String type = "MOCOSWeight-all";
		curveTool.obtainBugDetectionRateCurve( Constants.BUG_DETECTION_RATE_PERFORMANCE_FOLDER + "/" + type , Constants.BUG_DETECTION_RATE_PERFORMANCE_FOLDER + "/" + type + "-detectionRateCurve.csv");
	}
}
