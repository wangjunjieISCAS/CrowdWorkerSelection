package com.performanceEvaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.data.Constants;

import java.util.ArrayList;
/*
 * 从PerformanceSummarize的结果中读取
 */
public class ObjectivePerformanceForPlot {
	
	public HashMap<String, HashMap<Integer, ArrayList<Double>>> readPerformance ( String fileName ) {
		//type, separate index
		HashMap<String, HashMap<Integer, ArrayList<Double>>> result = new HashMap<String, HashMap<Integer, ArrayList<Double>>>();
		
		HashMap<Integer, String> nameIndex = new HashMap<Integer, String>();
		
		Pattern pattern = Pattern.compile( "[0-9]*");
		try {			
			BufferedReader reader = new BufferedReader(new FileReader(new File( fileName ) ) );
			String line = "";
			boolean isFirstLine = true;
			while ((line = reader.readLine()) != null) {
				String[] temp = line.split(",");
				
				if ( isFirstLine == true ) {
					for ( int i =0; i < temp.length; i++ ) {
						String name = temp[i];
						
						Matcher isFeature = pattern.matcher( name );
						if ( isFeature.matches() ) {
							nameIndex.put( i, name );
							
							String[] nameTemp = name.split( "-");
							Integer sepIndex = Integer.parseInt( nameTemp[0]);
							String type = nameTemp[1];
							
							ArrayList<Double> value = new ArrayList<Double>();
							HashMap<Integer, ArrayList<Double>> typeValue = new HashMap<Integer, ArrayList<Double>>();
							typeValue.put( sepIndex, value);
							
							result.put( type , typeValue );
						}
					}
				}
				
				for ( int i =0; i < temp.length; i++ ) {
					Double prob = Double.parseDouble( temp[i]);
					if ( !nameIndex.containsKey( i)) {
						continue;
					}
					String name = nameIndex.get( i );
					String[] nameTemp = name.split( "-");
					Integer sepIndex = Integer.parseInt( nameTemp[0]);
					String type = nameTemp[1];
					
					ArrayList<Double> value = result.get( type).get( sepIndex );
					value.add( prob );
					
					result.get( type).put( sepIndex, value );
				}
				
				isFirstLine = false;				
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
	
	public void generatePerformanceForPlot( HashMap<String, HashMap<Integer, ArrayList<Double>>> performance, String fileName, String[] types ) {
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( fileName , true ) );
			
			for ( int i =0; i < types.length; i++ ) {
				String type = types[i];
				if ( !performance.containsKey( type )) {
					System.out.println ( "No value for " + type );
					continue;
				}
				HashMap<Integer, ArrayList<Double>> perfValues = performance.get( type );
				for ( Integer sepIndex : perfValues.keySet() ) {
					ArrayList<Double> value = perfValues.get( sepIndex );
					for ( int j =0; j < value.size(); j++ ) {
						writer.write( type  + ",");
						writer.write( sepIndex.toString() + ",");
						writer.write( value.get(j).toString() );
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
	
	public static void main ( String args[]) {
		ObjectivePerformanceForPlot plotTool = new ObjectivePerformanceForPlot();
		HashMap<String, HashMap<Integer, ArrayList<Double>>> performance = plotTool.readPerformance( Constants.BUG_DETECTION_RATE_PERFORMANCE_FOLDER + "/comparison.csv");
		
		String[] types = { "MOCOSWeight-3 factors", "MOCOSWeight-no div"};
		plotTool.generatePerformanceForPlot( performance, Constants.PLOT_DATA_FOLDER + "/RQ2.csv", types);
	}
}
