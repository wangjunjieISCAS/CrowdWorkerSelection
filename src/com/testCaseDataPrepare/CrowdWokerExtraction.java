package com.testCaseDataPrepare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.data.Capability;
import com.data.Constants;
import com.data.DomainKnowledge;
import com.data.Phone;
import com.data.CrowdWorker;
import com.data.TestProject;

public class CrowdWokerExtraction {
	
	public HashMap<String, CrowdWorker> obtainCrowdWokerInfo ( ArrayList<TestProject> projectList, ArrayList<String> finalTermList ) {
		PhoneExtraction phoneTool = new PhoneExtraction();
		CapabilityExtraction capTool = new CapabilityExtraction();
		DomainKnowledgeExtraction domainTool = new DomainKnowledgeExtraction();
		
		HashMap<String, Phone> phoneInfo = phoneTool.obtainPhoneInfo(projectList);
		HashMap<String, Capability> capInfo = capTool.obtainCapabilityInfo(projectList);
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
		Integer numProject = 0, numReport = 0, numBug = 0;
		HashMap<String, Integer> termNumList = new HashMap<String, Integer>();
		
		for (String userId: crowdWorkerList.keySet() ) {
			Capability cap = crowdWorkerList.get( userId).getCapInfo();
			numProject += cap.getNumProject();
			numReport += cap.getNumReport();
			numBug += cap.getNumBug();
			
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
		
		Integer aveNumProject = numProject / crowdWorkerList.size() ;
		Integer aveNumReport = numReport / crowdWorkerList.size();
		Integer aveNumBug = numBug / crowdWorkerList.size();
		Double percBug = (1.0*aveNumBug) / (1.0*aveNumReport);
		Capability capInfo = new Capability ( aveNumProject, aveNumReport, aveNumBug, percBug );
		
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
}
