package com.dataProcess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;


public class TrainTestOrganize {
	/*
	 * 将全部的562个测试任务分为20份，记录每份的起始任务编号、结束任务编号、中间时间（作为该份任务的时间，得到人员的capbility）
	 */
	public void organizeTrainTestSet (String closeTimeFile, String trainTestSetFile ) {
		SimpleDateFormat formatLine = new SimpleDateFormat ("yyyy/MM/dd HH:mm");
		
		HashMap<Integer, Integer> beginIndexMap = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> endIndexMap = new HashMap<Integer, Integer>();
		int setIndex = 0;
		int i = 1;
		int taskInEachSet = 28;
		while ( i < 560 ) {
			setIndex += 1;
			beginIndexMap.put( setIndex, i);
			i += taskInEachSet - 1;
			endIndexMap.put( setIndex, i);
			i += 1;			
		}
		endIndexMap.put( setIndex, 562 );
		
		HashMap<Integer, ArrayList<Date>> timeForSetIndex = new HashMap<Integer, ArrayList<Date>>();
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader( new File ( closeTimeFile )));
			String line = "";
			
			boolean isFirstLine = true;
			while ( ( line = br.readLine() ) != null ) {
				if ( isFirstLine == true ) {
					isFirstLine = false;
					continue;
				}
				String[] temp = line.split( ",");
				Integer timeOrder = Integer.parseInt( temp[0]);
				String dateStr = temp[3];
				Date closeDate = formatLine.parse( dateStr );
				
				int index = (timeOrder-1) / taskInEachSet + 1;
				if ( index > 20 )
					index = 20;
				ArrayList<Date> dateList = null;
				if ( timeForSetIndex.containsKey( index )) {
					dateList = timeForSetIndex.get( index );
				}
				else {
					dateList = new ArrayList<Date>();
				}
				dateList.add( closeDate);
				timeForSetIndex.put( index, dateList );
			}
			
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HashMap<Integer, Date> closeTimeForSet = new HashMap<Integer, Date>();
		for ( Integer key : timeForSetIndex.keySet() ) {
			ArrayList<Date> dateList = timeForSetIndex.get( key );
			Collections.sort( dateList );
			
			int index = dateList.size() / 2;
			Date date = dateList.get(index);
			
			closeTimeForSet.put( key, date );
		}
		
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( trainTestSetFile ));
			
			writer.write( "TrainSetId" + "," + "beginIndexId" + "," + "endIndexId" + "," + "closeTimeForSet");
			writer.newLine();
			for ( Integer key : beginIndexMap.keySet() ) {
				writer.write( key + "," + beginIndexMap.get( key ) + ",");
				writer.write( endIndexMap.get( key) + ",");
				writer.write( formatLine.format( closeTimeForSet.get( key ) ) );
				writer.newLine();
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static void main ( String args[] ) {
		String closeTimeFile = "data/output/findings/closeTimeForProject.csv";
		String trainTestSetFile = "data/output/findings/trainTestSet.csv";
		TrainTestOrganize organizeTool = new TrainTestOrganize();
		organizeTool.organizeTrainTestSet(closeTimeFile, trainTestSetFile);
	}
}
