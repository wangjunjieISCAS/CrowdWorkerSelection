package com.testCaseDataPrepare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.data.Capability;
import com.data.Constants;
import com.data.DomainKnowledge;
import com.data.Phone;
import com.data.CrowdWorker;
import com.data.TestProject;

public class CrowdWokerExtraction {
	
	public HashMap<String, CrowdWorker> obtainCrowdWokerInfo ( ArrayList<TestProject> projectList, ArrayList<String> finalTermList, Date curTime) {
		PhoneExtraction phoneTool = new PhoneExtraction();
		CapabilityExtraction capTool = new CapabilityExtraction();
		DomainKnowledgeExtraction domainTool = new DomainKnowledgeExtraction();
		
		HashMap<String, Phone> phoneInfo = phoneTool.obtainPhoneInfo(projectList);
		HashMap<String, Capability> capInfo = capTool.obtainCapabilityInfo(projectList, curTime);
		HashMap<String, DomainKnowledge> domainInfo = domainTool.obtainDomainKnowledgeInfo(projectList, finalTermList);
		
		HashMap<String, CrowdWorker> crowdWorkerList = new HashMap<String, CrowdWorker>();
		for ( String userId: phoneInfo.keySet() ) {
			Phone phone = phoneInfo.get( userId);
			Capability cap = capInfo.get( userId );
			DomainKnowledge domain = domainInfo.get( userId );
			
			CrowdWorker testCase = new CrowdWorker ( userId, phone, cap, domain );
			crowdWorkerList.put( userId, testCase );
		}
		return crowdWorkerList;
	}
	
	/*
	 * generate the average capability and most common domain knowledge,
	 * the alternative treatment is to generate the zero capability and none domain knowledge
	 */
	public CrowdWorker obtainDefaultCrowdWorker ( HashMap<String, CrowdWorker> crowdWorkerList) {
		Integer[] numProject = this.newZeroArrays();
		Integer[] numReport = this.newZeroArrays();
		Integer[] numBug = this.newZeroArrays();
		Integer durationLastAct = 0;
		HashMap<String, Integer> termNumList = new HashMap<String, Integer>();
		
		for (String userId: crowdWorkerList.keySet() ) {
			Capability cap = crowdWorkerList.get( userId).getCapInfo();
			for ( int i =0; i < cap.getNumProject().length; i++ ) {
				//System.out.println( userId );
				numProject[i] += cap.getNumProject()[i];
				numReport[i] += cap.getNumReport()[i];
				numBug[i] += cap.getNumBug()[i];
			}
			durationLastAct += cap.getDurationLastAct();
			
			DomainKnowledge domain = crowdWorkerList.get( userId).getDomainKnInfo();
			for ( int i =0; i < domain.getDomainKnowledge().size(); i++ ) {
				String term = domain.getDomainKnowledge().get( i );
				int num = 1;
				if ( termNumList.containsKey( term )) {
					num += termNumList.get( term );
				}
				termNumList.put( term, num );
			}
		}
		
		Integer[] aveNumProject = this.newZeroArrays();
		Integer[] aveNumReport = this.newZeroArrays();
		Integer[] aveNumBug = this.newZeroArrays();
		Double[] percBug = new Double[Constants.CAP_SIZE_PER_TYPE];
		for ( int i=0; i < percBug.length; i++ ) {
			percBug[i] = 0.0;
		}
		Integer durationDays = 0;
		for ( int i =0; i < numProject.length; i++ ) {
			aveNumProject[i] = numProject[i] / crowdWorkerList.size();
			aveNumReport[i] = numReport[i] / crowdWorkerList.size();
			aveNumBug[i] = numBug[i] / crowdWorkerList.size();
			
			percBug[i] = (1.0*aveNumBug[i]) / (1.0*aveNumReport[i]);
			durationDays = durationLastAct / crowdWorkerList.size();
		}
		
		Capability capInfo = new Capability ( aveNumProject, aveNumReport, aveNumBug, percBug, durationDays );
		
		//rank according to the value of each term
		List<HashMap.Entry<String, Integer>> infoIds = new ArrayList<HashMap.Entry<String, Integer>>(termNumList.entrySet());  
		
        // 对HashMap中的 value 进行排序  
        Collections.sort(infoIds, new Comparator<HashMap.Entry<String, Integer>>() {  
            public int compare(HashMap.Entry<String, Integer> o1,  HashMap.Entry<String, Integer> o2) {  
                return (o2.getValue()).compareTo(o1.getValue());  
            }  
        }); 
        
        ArrayList<String> defaultDomainTerms = new ArrayList<String>();
        for ( int j =0; j < Constants.DEFAULT_DOMAIN_LENGTH && j < infoIds.size(); j++ ) {
        	String term = infoIds.get(j).getKey();
        	
        	defaultDomainTerms.add( term );
        }
        DomainKnowledge domainInfo = new DomainKnowledge ( defaultDomainTerms );
        
        Phone phoneInfo = new Phone ( "phoneType", "OS", "network", "IS" );
        CrowdWorker defaultWorker = new CrowdWorker ( "0", phoneInfo, capInfo, domainInfo );
        
        return defaultWorker;
	}
	
	public Integer[] newZeroArrays (  ) {
		Integer[] arrayName = new Integer[Constants.CAP_SIZE_PER_TYPE];
		for ( int i =0; i < arrayName.length; i++ ) {
			arrayName[i] = 0;
		}
		return arrayName;
	}
}
