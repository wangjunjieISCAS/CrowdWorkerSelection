package com.taskReorganize;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		
		finalTermList = this.filterAbnormalTerms(finalTermList);
		System.out.println ( "FinalTermList is done!");
		
		try {
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( "data/output/finalTermList.txt" )) ), 1024);
			for ( int i =0; i < finalTermList.size(); i++ ) {
				output.write( finalTermList.get( i ) );
				output.newLine();
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
	
	/*
	 * 过滤掉的term 规则如下：
	 * 1. 长度 = 1
	 * 2. 包含标点的
	 */
	public ArrayList<String> filterAbnormalTerms ( ArrayList<String> finalTermList) {
		ArrayList<String> newFinalTermList = new ArrayList<String>();
		for ( int i =0; i < finalTermList.size(); i++ ) {
			String term = finalTermList.get( i );
			if ( term.length() == 1 && this.isContainDigitalEnglish(term))
				continue;
			if ( this.isContainPunctuation(term))
				continue;
			
			newFinalTermList.add( term );
		}
		return newFinalTermList;
	}
	
	public Boolean isContainPunctuation ( String term) {
		Pattern p = Pattern.compile( "[\\p{P}]");
		Matcher m = p.matcher( term );
		if ( m.find() ) {
			return true;
		}
		return false;
	}
	public Boolean isContainDigitalEnglish ( String term) {
		Pattern p = Pattern.compile( "^[A-Za-z0-9]+$");
		Matcher m = p.matcher( term );
		if ( m.find() ) {
			return true;
		}
		return false;
	}
	
	public ArrayList<String> loadFinalTermList ( ) {
		ArrayList<String> finalTermList  = new ArrayList<String>();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader( new File ( "data/output/finalTermList.txt" )));
			String line = "";
			
			while ( ( line = br.readLine() ) != null ) {
				String temp = line.trim();
				
				finalTermList.add( temp );
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
		
		String str = "看见了--";
		Pattern p = Pattern.compile( "^[A-Za-z0-9]+$");
		Matcher m = p.matcher(str);
		if ( m.find() ) {
			System.out.println( "true");
		}
	}
}
