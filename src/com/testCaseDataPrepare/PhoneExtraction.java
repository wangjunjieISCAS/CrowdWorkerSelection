package com.testCaseDataPrepare;

import java.util.ArrayList;
import java.util.HashMap;

import com.data.Phone;
import com.data.TestProject;
import com.data.TestReport;


//generate the <user id, Phone> data

public class PhoneExtraction {
	
	public HashMap<String, Phone> obtainPhoneInfo ( ArrayList<TestProject> projectList ) {
		HashMap<String, Phone> phoneInfoList = new HashMap<String, Phone>();
		
		for ( int i =0; i < projectList.size(); i++ ) {
			TestProject project = projectList.get( i );
			
			for ( int j = 0; j < project.getTestReportsInProj().size(); j++ ) {
				TestReport report = project.getTestReportsInProj().get( j );
				
				String phoneType = report.getPhoneType();
				String OS = report.getOS();
				String network = report.getNetwork();
				String ISP = report.getISP();
				
				String userId = report.getUserId();
				
				Phone phone = new Phone ( phoneType, OS, network, ISP );
				phoneInfoList.put( userId, phone );
			}
		}
		return phoneInfoList;
	}
}
