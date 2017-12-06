package com.selectionApproach;

import java.io.*;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import com.selectionObjective.SelectionObjectives;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinarySolutionType;
import jmetal.encodings.variable.Binary;
import jmetal.util.JMException;

public class JmetalProblem extends Problem {
	private static final long serialVersionUID = -3530441329740021364L;
	private ArrayList<String> ids;

	public JmetalProblem(ArrayList<String> candidatesIDs) {
		this.numberOfObjectives_ = 3;
		this.numberOfVariables_ = candidatesIDs.size();
		this.solutionType_ = new BinarySolutionType(this);
		this.ids = candidatesIDs;
	}

	private SortedMap<String, Boolean> candidateMap(Solution solution) {
		Variable[] dec = solution.getDecisionVariables();
		SortedMap<String, Boolean> res = new TreeMap<String, Boolean>();

		for (int i = 0; i < this.numberOfVariables_; i++)
			res.put(ids.get(i), ((Binary) (dec[i])).getIth(0) ? true : false);
		return res;
	}

	@Override
	public void evaluate(Solution solution) throws JMException {
		SortedMap<String, Boolean> selectionChoice = this.candidateMap(solution);
		double bugProb = SelectionObjectives.extractBugProbability(selectionChoice);
		double diversity = SelectionObjectives.extractDiversity(selectionChoice);
		double cost = SelectionObjectives.extractCost(selectionChoice);

		solution.setObjective(0, bugProb);
		solution.setObjective(1, diversity);
		solution.setObjective(2, cost);

		// System.out.println("bugProb is: " + bugProb + ". Diversity is: " +
		// diversity + ". Cost is: " + cost);
	}

	public int getLength(int var) {
		return 1;
	}

	public static void main(String[] args) throws ClassNotFoundException, JMException {
		File f = new File("data/input/candidates.csv");
		String line;
		BufferedReader br;
		ArrayList<String> candidates = new ArrayList<String>();

		try {
			br = new BufferedReader(new FileReader(f));
			while ((line = br.readLine()) != null) {
				candidates.add(line.trim());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		JmetalProblem google = new JmetalProblem(candidates);
		Solution randS = new Solution(google);
		google.evaluate(randS);
	}
}
