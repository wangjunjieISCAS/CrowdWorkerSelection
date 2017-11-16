package com.testCaseDataPrepare;

import java.util.ArrayList;
import java.util.HashMap;

import com.TFIDF.TFIDF;
import com.data.DomainKnowledge;
import com.data.TestProject;
import com.data.TestReport;
import com.dataProcess.ReportSegment;

public class DomainKnowledgeExtraction {
	
	public HashMap<String, DomainKnowledge> obtainDomainKnowledgeInfo ( ArrayList<TestProject> projectList, ArrayList<String> finalTermList ) {
		//obtain all the reports submitted by each user
		HashMap<String, ArrayList<TestReport>> userReportList = new HashMap<String, ArrayList<TestReport>>();
		
		for ( int i =0; i < projectList.size(); i++ ) {
			TestProject project = projectList.get( i );
			
			for ( int j = 0; j < project.getTestReportsInProj().size(); j++ ) {
				TestReport report = project.getTestReportsInProj().get( j );
			
				String userId = report.getUserId();
				
				ArrayList<TestReport> reportList = new ArrayList<TestReport>();
				if ( userReportList.containsKey( userId )) {
					reportList = userReportList.get( userId );
				}
				reportList.add( report );
				userReportList.put( userId, reportList );
			}
		}
		
		HashMap<String, DomainKnowledge> domainInfoList = new HashMap<String, DomainKnowledge>();
		
		ReportSegment segTool = new ReportSegment();
		//generate the domain terms of all the historical reports by each user
		for ( String userId: userReportList.keySet() ) {
			ArrayList<TestReport> reportList = userReportList.get( userId );
			HashMap<String, Integer> domainTerms = segTool.segmentTestReportListMap(reportList);
			
			domainTerms = this.filterUninformativeTerms(domainTerms, finalTermList);
			
			//here, can store the HashMap<String, Integer>, the value is the term frequency
			ArrayList<String> domainTermList = new ArrayList<String>();
			domainTermList.addAll( domainTerms.keySet() );
			DomainKnowledge domainInfo = new DomainKnowledge( domainTermList );
			domainInfoList.put( userId, domainInfo );
		}
		
		return domainInfoList;
	}
	
	public HashMap<String, Integer> filterUninformativeTerms ( HashMap<String, Integer> domainTerms, ArrayList<String> finalTermList) {
		HashMap<String, Integer> newDomainTerms = new HashMap<String, Integer>();
		
		for ( String term: domainTerms.keySet() ) {
			if ( finalTermList.contains( term )) {
				newDomainTerms.put( term, domainTerms.get( term ));
			}
		}
		
		return newDomainTerms;		
	}
}
