package com.performanceEvaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import com.data.Constants;
import com.data.TestProject;

public class BugDetPerformanceForPlot {
	public HashMap<String, HashMap<String, Double[]>> readDetectionPerformance ( String folderName ) {
		HashMap<String, HashMap<String, Double[]>> performance = new HashMap<String, HashMap<String, Double[]>>();
		
		String[] typeName = { "bugProb", "cap", "rev"};
		File projectsFolder = new File ( folderName );
		if ( projectsFolder.isDirectory() ){
			String[] projectFileList = projectsFolder.list();
			for ( int i = 0; i< projectFileList.length; i++ ){
				String fileName = projectFileList[i];
				String fullFileName = folderName + "/" + projectFileList[i];
				
				if ( new File( fullFileName).isDirectory() )
					continue;
				
				String type = "";
				for ( int j =0; j < typeName.length; j++ ) {
					if ( fileName.startsWith( typeName[j])) {
						type = typeName[j];
					}
				}
				
				HashMap<String, Double[]> result = this.readDetectionPerformancePerFile(fullFileName);
				
				if ( performance.containsKey( type )) {
					result.putAll( performance.get( type ));
				}
				performance.put( type, result);
			}				
		}	
		return performance;
	}
	
	
	public void generateFileForPlot ( HashMap<String, HashMap<String, Double[]>> performance , String fileName  ) {
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( fileName , true ) );
			
			String[] metricName = { "precision", "recall", "fMeasure"};
			for ( String type : performance.keySet() ) {
				String typeForSave = type;
				if ( type.equals( "bugProb"))
					typeForSave = "cap+rev";
				
				HashMap<String, Double[]> value = performance.get( type );
				for ( String metric : value.keySet() ) {
					Double[] perf = value.get( metric );
					
					for ( int i =0; i < perf.length; i++ ) {
						writer.write(  typeForSave  +",");
						writer.write( metricName[i] + ",");
						writer.write( perf[i].toString() );
						writer.newLine();
					}
				}
			}
			writer.flush();			
			writer.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public HashMap<String, Double[]> readDetectionPerformancePerFile ( String fileName ) {
		HashMap<String, Double[]> result = new HashMap<String, Double[]>();
		
		try {			
			BufferedReader reader = new BufferedReader(new FileReader(new File( fileName ) ) );
			String line = "";
			while ((line = reader.readLine()) != null) {
				String[] temp = line.split(",");
				
				String projectId = temp[0];
				Double precision = Double.parseDouble( temp[1]);
				Double recall = Double.parseDouble( temp[2] );
				Double fMeasure = Double.parseDouble( temp[3]);
				
				Double[] perf = new Double[3];
				perf[0] = precision;
				perf[1] = recall;
				perf[2] = fMeasure;
				
				result.put( projectId, perf );
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static void main ( String args[]) {
		BugDetPerformanceForPlot plotTool = new BugDetPerformanceForPlot();
		HashMap<String, HashMap<String, Double[]>> performance = plotTool.readDetectionPerformance( Constants.BUG_PROB_PERFORMANCE );
		
		plotTool.generateFileForPlot( performance, Constants.PLOT_DATA_FOLDER + "/bugProb.csv");
	}
}
