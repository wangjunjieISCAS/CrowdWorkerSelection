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
		
		Pattern pattern = Pattern.compile( "[0-9]*(.*)");
		try {			
			BufferedReader reader = new BufferedReader(new FileReader(new File( fileName ) ) );
			String line = "";
			boolean isFirstLine = true;
			while ((line = reader.readLine()) != null) {
				String[] temp = line.split(",");
				
				if ( isFirstLine == true ) {
					for ( int i =1; i < temp.length; i++ ) {
						if ( temp[i].length() == 1 || temp[i].length() == 0 || temp[i].equals( "") )
							continue;
						
						String name = temp[i].trim();
						
						Matcher isFeature = pattern.matcher( name );
						if ( isFeature.matches() ) {
							nameIndex.put( i, name );
							//System.out.println ( name );
							
							int index = name.indexOf( "-");
							Integer sepIndex = Integer.parseInt( name.substring( 0, index) );
							String type = name.substring( index+1 );
							
							ArrayList<Double> value = new ArrayList<Double>();
							HashMap<Integer, ArrayList<Double>> typeValue = new HashMap<Integer, ArrayList<Double>>();
							typeValue.put( sepIndex, value);
							
							result.put( type , typeValue );
						}
					}
					isFirstLine = false;			
					continue;
				}
				
				for ( int i =1; i < temp.length; i++ ) {
					if ( temp[i].length() == 1 || temp[i].length() == 0 || temp[i].equals( "") )
						continue;
					Double prob = Double.parseDouble( temp[i]);
					if ( !nameIndex.containsKey( i)) {
						continue;
					}
					String name = nameIndex.get( i );
					
					int index = name.indexOf( "-");
					Integer sepIndex = Integer.parseInt( name.substring( 0, index) );
					String type = name.substring( index+1 );
					
					ArrayList<Double> value = new ArrayList<Double>();
					if ( result.containsKey( type )) {
						if ( result.get( type).containsKey( sepIndex )) {
							value = result.get( type).get( sepIndex );
						}
					}
					value.add( prob );
					
					if ( result.containsKey( type )) {
						result.get( type).put( sepIndex, value );
					}else {
						HashMap<Integer, ArrayList<Double>> newValue = new HashMap<Integer, ArrayList<Double>>();
						newValue.put( sepIndex, value);
						result.put( type, newValue );
					}					
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
		
		return result;
	}
	
	public void generatePerformanceForPlot( HashMap<String, HashMap<Integer, ArrayList<Double>>> performance, String fileName, String[] types ) {
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( fileName ) );
			
			writer.write( " " + "," + "  " + "," + "BDR");
			writer.newLine();
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
		
		//String[] types = { "MOCOSWeight-all", "MOCOSWeight-no cap", "MOCOSWeight-no rev", "MOCOSWeight-no div" };
		//String[] types = { "MOCOSWeight-all", "baselineSTRING", "baselineISSRE", "baselineSEKE",  "baselineTOPIC", "baselineCOMPASC"  };
		String[] types = {"MOCOSWeight-all", "baselineSEKE", "baselineCOMPASC", "baselineISSRE", "baselineSTRING", "TRUE"};
		plotTool.generatePerformanceForPlot( performance, Constants.PLOT_DATA_FOLDER + "/RQ1-20.csv", types);
	}
}
