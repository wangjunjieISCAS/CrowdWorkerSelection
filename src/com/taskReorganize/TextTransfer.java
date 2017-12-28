package com.taskReorganize;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class TextTransfer {
	//将其中的英文全部转换为小写。如果既有大写又有小写，会得到不同的term
	public void transferToLowercase ( String projectFolder, String newProjectFolder ) {
		File projectsFolder = new File ( projectFolder );
		if ( projectsFolder.isDirectory() ){
			String[] projectFileList = projectsFolder.list();
			for ( int i = 0; i< projectFileList.length; i++ ){
				String projectFileName = projectFolder + "/" + projectFileList[i];
				String newProjectFileName = newProjectFolder + "/" + projectFileList[i];
				this.transferToLowercaseOneProject ( projectFileName , newProjectFileName);
			}
		}
		
	}
	
	public void transferToLowercaseOneProject ( String projectFile, String newProjectFile ) {
		try {
			BufferedReader br = new BufferedReader(new FileReader( new File ( projectFile )));
			
			CsvReader reader = new CsvReader( br, ',');
			ArrayList<String[]> reportList = new ArrayList<String[]>();
	        while ( reader.readRecord() ){
	        	int column = reader.getColumnCount();
	        	
	        	String[] report = new String[column+1];
	        	for ( int i = 0; i < column; i++ ) {
	        		report[i] = reader.get( i );
	        		
	        		if ( i ==9 || i == 10 ) {
	        			String temp = report[i];
	        			temp = temp.toLowerCase();
	        			report[i] = temp;
	        		}
	        	}
	        	
	        	reportList.add( report );
	        }
	        reader.close();
			br.close();
			
			CsvWriter csvOutput = new CsvWriter(new FileWriter( newProjectFile), ',');
			
			for ( int i =0; i < reportList.size(); i++  ) {
				String[] report = reportList.get( i );
				
				for ( int j =0; j < report.length; j++ ) {
					csvOutput.write( report[j]);
				}
				csvOutput.endRecord();
			}
			csvOutput.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public static void main ( String args[] ) {
		TextTransfer transferTool = new TextTransfer();
		transferTool.transferToLowercase( "data/input/experimental dataset" , "data/input/experimental dataset-2" );
	}
}	
