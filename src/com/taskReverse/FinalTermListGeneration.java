package com.taskReverse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import com.TFIDF.TFIDF;
import com.data.TestProject;
import com.dataProcess.DataSetPrepare;
import com.dataProcess.TestProjectReader;

public class FinalTermListGeneration {
	public void generateFinalTermList ( ArrayList<TestProject> historyProjectList ) {
		DataSetPrepare dataTool = new DataSetPrepare ( );
		ArrayList<HashMap<String, Integer>> totalDataSet = dataTool.prepareDataSet( historyProjectList );
		System.out.println ( "TotalDataSet is done!");
		
		TFIDF tfidfTool = new TFIDF();
		ArrayList<String> finalTermList = tfidfTool.obtainFinalTermList(totalDataSet);
		System.out.println ( "FinalTermList is done!");
		
		try {
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( "data/output/finalTermList.txt" )) ), 1024);
			for ( int i =0; i < finalTermList.size(); i++ ) {
				output.write( finalTermList.get( i ) + " ");
			}
			output.flush();
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public ArrayList<String> loadFinalTermList ( ) {
		ArrayList<String> finalTermList  = new ArrayList<String>();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader( new File ( "data/output/paretoFrontData.txt" )));
			String line = "";
			
			while ( ( line = br.readLine() ) != null ) {
				String[] temp = line.split( " ");
				
				for ( int i =0; i < temp.length; i++ ) {
					finalTermList.add( temp[i].trim() );
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return finalTermList;
	}
	
	public static void main ( String args[] ) {
		TestProjectReader projReader = new TestProjectReader();
		ArrayList<TestProject> historyProjectList = projReader.loadTestProjectAndTaskList( "data/input/experimental dataset", "data/input/taskDescription");
		
		FinalTermListGeneration termTool = new FinalTermListGeneration ();
		termTool.generateFinalTermList(historyProjectList);
	}
}
