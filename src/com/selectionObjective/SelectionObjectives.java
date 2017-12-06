package com.selectionObjective;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.SortedMap;

import com.data.Capability;
import com.data.Constants;
import com.data.CrowdWorker;
import com.data.DomainKnowledge;
import com.data.Phone;
import com.dataProcess.SimilarityMeasure;

public class SelectionObjectives {
	private static HashMap<String, CrowdWorker> candidateWorkers;
	private static HashMap<String, Double> bugProForWorker;

	// read the file to initiate the candidateWorkers, and the bugProbForWorker
	static {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(Constants.WORKER_PHONE_FILE)));
			candidateWorkers = new HashMap<String, CrowdWorker>();
			bugProForWorker = new HashMap<String, Double>();

			String line = "";
			while ((line = reader.readLine()) != null) {
				String[] temp = line.split(",");
				String userId = temp[0];

				if (temp.length < 5)
					temp = new String[] { userId, "", "", "", "" };

				Phone phoneInfo = new Phone(temp[1], temp[2], temp[3], temp[4]);
				CrowdWorker workerInfo = new CrowdWorker(userId);
				workerInfo.setPhoneInfo(phoneInfo);
				candidateWorkers.put(userId, workerInfo);
			}

			reader = new BufferedReader(new FileReader(new File(Constants.WORKER_CAP_FILE)));
			line = "";
			while ((line = reader.readLine()) != null) {
				String[] temp = line.split(",");
				String userId = temp[0];
				Capability capInfo = new Capability(Integer.parseInt(temp[1]), Integer.parseInt(temp[2]),
						Integer.parseInt(temp[3]), Double.parseDouble(temp[4]));

				CrowdWorker workerInfo = candidateWorkers.get(userId);
				workerInfo.setCapInfo(capInfo);

				candidateWorkers.put(userId, workerInfo);
			}

			reader = new BufferedReader(new FileReader(new File(Constants.WORKER_DOMAIN_FILE)));
			line = "";
			while ((line = reader.readLine()) != null) {
				String[] temp = line.split(",");
				String userId = temp[0];

				ArrayList<String> domainTerms = new ArrayList<String>();
				for (int i = 1; i < temp.length; i++) {
					domainTerms.add(temp[i]);
				}
				DomainKnowledge domainInfo = new DomainKnowledge(domainTerms);

				CrowdWorker workerInfo = candidateWorkers.get(userId);

				if (workerInfo == null)
					continue;

				workerInfo.setDomainKnInfo(domainInfo);

				candidateWorkers.put(userId, workerInfo);
			}

			// obtain bugProForWorker
			reader = new BufferedReader(new FileReader(new File(Constants.BUG_PROB_FILE)));
			line = "";
			while ((line = reader.readLine()) != null) {
				String[] temp = line.split(",");
				String userId = temp[0];
				Double prob = Double.parseDouble(temp[1]);

				bugProForWorker.put(userId, prob);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Double extractBugProbability(SortedMap<String, Boolean> selection) {
		double score = 0.0;
		
		int count = 0;
		for (String userId : selection.keySet()) {
			Boolean isSelect = selection.get(userId);
			if (isSelect == false) {
				continue;
			}
			count ++;
			Double prob = bugProForWorker.get(userId);
			score += prob;
		}
		
		score = score / count;
		score = score * 100;
		return score;
	}

	public static Double extractDiversity(SortedMap<String, Boolean> selection) {
		double score = 0.0;
		ArrayList<CrowdWorker> workerList = new ArrayList<CrowdWorker>();
		for (String userId : selection.keySet()) {
			Boolean isSelect = selection.get(userId);
			if (isSelect == false) {
				continue;
			}
			workerList.add(candidateWorkers.get(userId));
		}

		SimilarityMeasure simTool = new SimilarityMeasure();
		// compute the distance between each pair of workers
		for (int i = 0; i < workerList.size(); i++) {
			for (int j = i + 1; j < workerList.size(); j++) {
				CrowdWorker workerI = workerList.get(i);
				CrowdWorker workerJ = workerList.get(j);

				double phoneDis = simTool.hammingDistanceForPhone(workerI.getPhoneInfo(), workerJ.getPhoneInfo());
				double domainDis = simTool.hammingDistanceForDomain(workerI.getDomainKnInfo(),
						workerJ.getDomainKnInfo());

				double dis = phoneDis * Constants.DIVERSITY_PHONE_WEIGHT + domainDis * (1.0 - Constants.DIVERSITY_PHONE_WEIGHT);

				score += dis;
			}
		}
		
		score = score / (workerList.size() * workerList.size() / 2);
		
		return score;
	}

	public static Double extractCost(SortedMap<String, Boolean> selection) {
		double score = 0.0;
		for (String userId : selection.keySet()) {
			Boolean isSelect = selection.get(userId);
			if (isSelect == false) {
				continue;
			}
			score += 1.0;
		}
		return score;
	}
}
