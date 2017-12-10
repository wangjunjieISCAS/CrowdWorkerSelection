package com.findings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.data.TestProject;
import com.data.TestReport;
import com.dataProcess.TestProjectReader;


/*
 * 为了对重复缺陷报告进行标注
 */
public class ClusterTestReport {
	Map<Integer, ArrayList<Integer>> clusterContent;
	Map<Integer, Integer> clusterInfo;    //the key is the id of the report, begin from 1
	protected TestProject testProject;
	
	public ClusterTestReport( String fileName ){
		TestProjectReader projReader = new TestProjectReader();
		testProject = projReader.loadTestProject( fileName );
		
		clusterContent = new HashMap<Integer, ArrayList<Integer >>();
		clusterInfo = new HashMap<Integer, Integer>();
	}
	
	public ClusterTestReport ( ) {
		
	}
	
	public void obtainReportsEachCluster ( String clusterInfoFile ){
		int index = 1;    //projectforcluster.xls文件中的初始编号
		ArrayList<Integer> feedbackIds = null;
		
		BufferedReader br;
		try {
			br = new BufferedReader ( new FileReader ( clusterInfoFile ));
			String str = "";
			
			while ( (str = br.readLine() ) != null ){
				if ( !str.trim().equals( "") ){
					int clusterId = Integer.parseInt( str.trim() ) ;
					
					if ( !clusterContent.containsKey( clusterId )){
						feedbackIds = new ArrayList<Integer>();
						clusterContent.put( clusterId, feedbackIds );
					}
					feedbackIds = clusterContent.get( clusterId );
					feedbackIds.add( index);
					clusterContent.put( clusterId, feedbackIds );
					clusterInfo.put( index , clusterId );
					index++;
				}				
			}
			br.close();
			
			//for test
			/*
			Iterator iter = clusterContent.entrySet().iterator();
			while ( iter.hasNext() ){
				Map.Entry<Integer, ArrayList<Integer>> entry = (Entry<Integer, ArrayList<Integer>>) iter.next();
				System.out.println ( entry.getKey() + " size :" + entry.getValue().size() + " " + entry.getValue().toString() );				
			}
			*/
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	
	public void clusterReports ( String clusterInfoFile, String projectFile , String newProjectFile ){
		this.obtainReportsEachCluster(clusterInfoFile);
		try {
			BufferedReader br = new BufferedReader(new FileReader( new File ( projectFile )));
			
			CsvReader reader = new CsvReader( br, ',');
			
			int index = 0;
			ArrayList<String[]> reportList = new ArrayList<String[]>();
	        while ( reader.readRecord() ){
	        	int column = reader.getColumnCount();
	        	
	        	String[] report = new String[column+1];
	        	for ( int i = 0; i < column; i++ ) {
	        		report[i] = reader.get( i );
	        	}
	        	
	        	if ( index != 0 ) {
	        		report[column] = clusterInfo.get( index ).toString();
	        	}else
	        	{
	        		report[column] = "重复情况";
	        	}
	        	index++;
	        	
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
	
	public void duplicateReportsGeneration ( String projectFolder, String targetProjectFolder ) {
		File projectsFolder = new File ( projectFolder );
		if ( projectsFolder.isDirectory() ){
			String[] projectFileList = projectsFolder.list();
			for ( int i = 0; i< projectFileList.length; i++ ){
				String projectFileName = projectFolder + "/" + projectFileList[i];
				
				String clusterFile = "data/output/cluster/cluster.txt";
				String rowLabelFile = "data/output/cluster/row.txt";
				String columnLabelFile = "data/output/cluster/column.txt";
				String clusterInfoFile = "data/output/cluster/clusterInfo.txt";
				
				ClutoData clutoData = new ClutoData( projectFileName );
				clutoData.genereateClutoData( clusterFile );
				clutoData.generateRowColumnData( rowLabelFile, columnLabelFile );
				
				int reportNumber = clutoData.getRowNumber();
				
				//进行聚类
				int optimalClusterNumber = 0;
				if ( reportNumber > 200 )
					optimalClusterNumber = reportNumber / 8;
				else if ( reportNumber > 100 )
					optimalClusterNumber = reportNumber / 7;
				else if ( reportNumber > 50 )
					optimalClusterNumber = reportNumber / 5 ;
				else if ( reportNumber > 30  )
					optimalClusterNumber = reportNumber / 4;
				else
					optimalClusterNumber = reportNumber / 3;
				System.out.println ( "optimalClusterNumber: " + optimalClusterNumber);
				
				String dir = "G:\\eclipse-workspace\\CrowdWorkerSelection\\data\\output\\cluster\\";
				String commandCmd = "cmd /k ";
				String commandCluto = "G:\\eclipse-workspace\\cluto-2.1.1\\Win32\\vcluster.exe ";
				String commandLabel = "-clabelfile=\"" + dir + "columnLabel.txt\" -showfeatures -nfeatures=30 ";
				String commandResult = "-clustfile=\"" + dir + "clusterInfo.txt\" ";
				String commandData = dir + "cluster.txt " + optimalClusterNumber + " ";
				String commandOutput = ">" + dir + "clusterResults.txt";

				String command = commandCmd + commandCluto + commandLabel + commandResult + commandData + commandOutput;
				System.out.println ( command );
				
				try {
					Process clutoProcess = Runtime.getRuntime().exec( command);
					Thread.sleep( 3000 );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//分析结果
				ClusterTestReport clusterReport = new ClusterTestReport( projectFileName );
				
				clusterReport.clusterReports(clusterInfoFile, projectFileName, targetProjectFolder + "/" + projectFileList[i] );
			}				
		}			

	}
	
	public static void main ( String args[] ){
		//String projectFolder = "data/input/total crowdsourced reports";
		String projectFolder = "data/input/experimental dataset";
		String targetProjectFolder = "data/input/experimental dataset-2";
		
		ClusterTestReport clusterReport = new ClusterTestReport(  );
		clusterReport.duplicateReportsGeneration(projectFolder, targetProjectFolder);
	} 
}
