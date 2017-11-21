package com.selectionApproach;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.SortedMap;

import com.data.Constants;
import com.data.CrowdWorker;
import com.data.Phone;

public class MultiObjectiveSelection {

	public void multiObjectiveWorkerSelection ( ArrayList<String> candidatesIDs ) {
		
	}
	
	public ArrayList<String> obtainCandidateIDs ( ) {
		ArrayList<String> candidatesIDs = new ArrayList<String>();
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader( new File ( "data/input/candidates.csv" )));
			
			String line = "";
			while ( ( line = reader.readLine() ) != null ) {
				String userId = line.trim();
				candidatesIDs.add( userId );				
			}		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return candidatesIDs;
	}
	
	public static void main ( String[] args ) {
		MultiObjectiveSelection selectionTool = new MultiObjectiveSelection();
		ArrayList<String> candidateIDs = selectionTool.obtainCandidateIDs();
		System.out.println( candidateIDs.size() );
		
		
	}
}
