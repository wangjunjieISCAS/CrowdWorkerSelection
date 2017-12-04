package com.findings;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.data.TestProject;
import com.data.TestReport;
import com.dataProcess.ReportSegment;
import com.dataProcess.TestProjectReader;



public class ClutoData {
	protected TestProject testProject;
	/*
	 * 选用Sparse Matrix Format，第一行row number，column number。non-zero value number。
	 * 记录每个column的feature的名称，每个row的测试报告编号
	 */
	protected List<String> columnLabel;
	protected List<Integer> rowLabel;
	protected Integer columnNumber;
	protected Integer rowNumber;
	protected Integer nonZeroNumber;
	
	public ClutoData(String fileName ){
		TestProjectReader projReader = new TestProjectReader();
		testProject = projReader.loadTestProject( fileName );
	
		columnLabel = new ArrayList<String>();
		rowLabel = new ArrayList<Integer>();
	}
	
	public void obtainColumnLabel ( ){
		columnNumber = 0;
		rowNumber = 0;
		nonZeroNumber = 0;
		
		ReportSegment segmentTool = new ReportSegment();
		rowNumber = testProject.getTestReportsInProj().size();
		for ( int i =0; i < testProject.getTestReportsInProj().size(); i++ ) {
			TestReport report = testProject.getTestReportsInProj().get( i );
			
			Map<String, Integer> termFreq = segmentTool.segmentTestReportMap(report);
			
			nonZeroNumber += termFreq.size();
			
			Iterator iter = termFreq.entrySet().iterator();
			while ( iter.hasNext() ) {
				Map.Entry<String, Integer> entry = (Entry<String, Integer>) iter.next();
				String term = entry.getKey();
				if ( !columnLabel.contains( term )) {
					columnLabel.add( term );
				}
			}
		}
		
		columnNumber = columnLabel.size();
	}	
	
	
	public void genereateClutoData ( String clusterFile ){
		obtainColumnLabel();
		ReportSegment segmentTool = new ReportSegment();
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter( new FileWriter ( clusterFile ) ) ;
			writer.write( rowNumber  + " " + columnNumber + " " + nonZeroNumber );
			writer.newLine();
			
			for ( int i =0; i < testProject.getTestReportsInProj().size() ; i++ ) {
				TestReport report = testProject.getTestReportsInProj().get( i );
				Map<String, Integer> featureValue = segmentTool.segmentTestReportMap(report);
				
				for ( int k =0; k < columnLabel.size(); k++ ) {
					String term = columnLabel.get(k);
					if ( featureValue.containsKey( term )) {
						int columnIndex = k+1;
						writer.write( columnIndex + " " + featureValue.get( term ) + " ");
					}
				}
				
				writer.newLine();
				writer.flush();
				
				rowLabel.add( report.getId() );
			}				
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public int getRowNumber ( ) {
		return rowNumber;
	}
	
	public void generateRowColumnData ( String rowLabelFile, String columnLabelFile ){
		BufferedWriter writer;
		try {
			writer = new BufferedWriter( new FileWriter ( rowLabelFile ) ) ;
			for ( int i = 0; i< rowLabel.size(); i++ ){
				writer.write( rowLabel.get( i ).toString() );
				writer.newLine();
				writer.flush();
			}				
			writer.close();
			
			writer = new BufferedWriter( new FileWriter ( columnLabelFile ) ) ;
			for ( int i = 0; i< columnLabel.size(); i++ ){
				writer.write( columnLabel.get( i ));
				writer.newLine();
				writer.flush();
			}	
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	public static void main ( String args[] ){
		String dataFile = "data/output/cluster/98仙游测试_1463737840.csv";
		String clusterFile = "data/output/cluster/cluster.txt";
		String rowLabelFile = "data/output/cluster/row.txt";
		String columnLabelFile = "data/output/cluster/column.txt";
		
		ClutoData clutoData = new ClutoData( dataFile );
		
		//ClusterWord.obtainClusterRepresentWord(  );
		clutoData.genereateClutoData( clusterFile );
		clutoData.generateRowColumnData( rowLabelFile, columnLabelFile );
	}
}
