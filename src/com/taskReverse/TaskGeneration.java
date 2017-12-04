package com.taskReverse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.TFIDF.TFIDF;
import com.data.Constants;
import com.data.TestProject;
import com.data.TestReport;
import com.dataProcess.ReportSegment;
import com.dataProcess.TestProjectReader;
import com.dataProcess.WordSegment;

/*
 * According to the crowdsourced reports submitted by the crowd worker, generate the test task description using reverse engineering
 * The general idea is to collect all the reports together and generate the term tokens, then rank the term token based on the TF-IDF, and choose the K term tokens with the biggest TD-IDF 
 * 
 * treat each project as a document, and all the reports as all the documents
 * only in this way, the inverse document frequency can reflect whether the word occur in a large number of documents
 */
public class TaskGeneration {
	
	public ArrayList<ArrayList<String>> generateTaskDescription ( ArrayList<TestProject> projectList ) {
		ArrayList<HashMap<String, Integer>> projectWordList = new ArrayList<HashMap<String, Integer>>();
		
		ReportSegment reportSegment = new ReportSegment();
		
		for ( int i =0; i < projectList.size(); i++ ) {
			HashMap<String, Integer> projectWord = reportSegment.segmentTestProjectMap(projectList.get( i ));
			projectWordList.add( projectWord);
		}
		
		TFIDF tfidfTool = new TFIDF();
		ArrayList<HashMap<String, Double>> tfidfForProject = tfidfTool.countTFIDF( projectWordList );
		
		ArrayList<ArrayList<String>> representWordList = new ArrayList<ArrayList<String>>();
		for ( int i = 0; i < tfidfForProject.size(); i++ ) {
			HashMap<String, Double> tfidf = tfidfForProject.get( i );
			
			//rank according to the value of each term
			List<HashMap.Entry<String, Double>> infoIds = new ArrayList<HashMap.Entry<String, Double>>(tfidf.entrySet());  
			
	        // 对HashMap中的 value 进行排序  
	        Collections.sort(infoIds, new Comparator<HashMap.Entry<String, Double>>() {  
	            public int compare(HashMap.Entry<String, Double> o1,  HashMap.Entry<String, Double> o2) {  
	                return (o2.getValue()).compareTo(o1.getValue());  
	            }  
	        }); 
	        
	        ArrayList<String> representWord = new ArrayList<String>();
	        for ( int j =0; j < Constants.TASK_DES_LENGTH && j < infoIds.size(); j++ ) {
	        	String term = infoIds.get(j).getKey();
	        	
	        	representWord.add( term );
	        }
	        representWordList.add( representWord );
		}
		
		return representWordList;
	}
	
	
	public void generateTaskDescriptionForAllProjects ( String folderName ) {
		TestProjectReader projectReader = new TestProjectReader();
		ArrayList<TestProject> projectList = new ArrayList<TestProject>();
		ArrayList<String> projectNameList = new ArrayList<String>();
		
		File projectsFolder = new File ( folderName);
		if ( projectsFolder.isDirectory() ){
			String[] projectCategoryList = projectsFolder.list();
			for ( int i = 0; i< projectCategoryList.length; i++ ){
				String fileName  = folderName + "/" + projectCategoryList[i];
				
				TestProject project = projectReader.loadTestProject( fileName );
				projectList.add( project );
				
				projectNameList.add(  projectCategoryList[i] );
			}
		}
		
		ArrayList<ArrayList<String>> representWordList = this.generateTaskDescription(projectList);		
		
		for ( int i =0; i < representWordList.size(); i++ ) {
			String projectName = projectNameList.get( i );
			projectName = projectName.substring( 0, projectName.length()-4);
			
			ArrayList<String> representWord = representWordList.get( i );
			
			try {
				BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( "data/input/taskDescription/" + projectName + ".txt" )) ), 1024);
				for ( int j =0; j < representWord.size(); j++ ) {
					output.write( representWord.get( j) + " ");
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
	}
	
	public static void main ( String args[] ){
		TaskGeneration taskGeneration = new TaskGeneration();
		taskGeneration.generateTaskDescriptionForAllProjects( "data/input/experimental dataset");
	}
}
