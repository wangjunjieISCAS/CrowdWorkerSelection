package com.selectionApproach;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import com.baseline.SEKESelectionObjectives;
import com.data.TestProject;
import com.selectionObjective.SelectionObjectives;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinarySolutionType;
import jmetal.encodings.variable.Binary;
import jmetal.util.JMException;

public class SEKEJmetalProblem extends Problem {
	private static final long serialVersionUID = -3530441329740021364L;
	private ArrayList<String> ids;
	private Random rand;
	SEKESelectionObjectives objectiveTool;
	

	public SEKEJmetalProblem(ArrayList<String> candidatesIDs, String testSetIndex, String taskId, TestProject project ) {
		this.numberOfObjectives_ = 3;
		this.numberOfVariables_ = candidatesIDs.size();
		this.solutionType_ = new BinarySolutionType(this);
		this.ids = candidatesIDs;
		rand = new Random();
		
		objectiveTool = new SEKESelectionObjectives ( testSetIndex, taskId, project );
	}

	public SEKEJmetalProblem(ArrayList<String> candidatesIDs, long seed, String testSetIndex, String taskId,TestProject project  ) {
		this.numberOfObjectives_ = 3;
		this.numberOfVariables_ = candidatesIDs.size();
		this.solutionType_ = new BinarySolutionType(this);
		this.ids = candidatesIDs;
		rand = new Random(seed);
		
		objectiveTool = new SEKESelectionObjectives ( testSetIndex, taskId, project );
	}

	public SortedMap<String, Boolean> candidateMap(Solution solution) {
		Variable[] dec = solution.getDecisionVariables();
		SortedMap<String, Boolean> res = new TreeMap<String, Boolean>();

		for (int i = 0; i < this.numberOfVariables_; i++)
			res.put(ids.get(i), ((Binary) (dec[i])).getIth(0) ? true : false);
		return res;
	}

	@Override
	public void evaluate(Solution solution) throws JMException {
		SortedMap<String, Boolean> selectionChoice = this.candidateMap(solution);
		double reqCoverage = objectiveTool.extractReqCoverage( selectionChoice);
		double bugExperience = objectiveTool.extractDetExperience(selectionChoice);
		double cost = objectiveTool.extractCost(selectionChoice);

		solution.setObjective(0, -reqCoverage);
		solution.setObjective(1, -bugExperience);
		solution.setObjective(2, cost);

		//System.out.println("reqCoverage is: " + reqCoverage + ". bugExperience is: " +  bugExperience + ". Cost is: " + cost);
	}

	public int getLength(int var) {
		return 1;
	}

	public SolutionSet generateDiverseSet(int popSize) {
		SolutionSet result = new SolutionSet(popSize);
		for (int i = 0; i < popSize; i++) {
			try {
				Solution sol = new Solution(this);
				Variable[] variables = sol.getDecisionVariables();
				for (Variable v : variables) {
					((Binary) v).setIth(0, rand.nextDouble() < i / (double) popSize ? true : false);
				}
				result.add(sol);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
