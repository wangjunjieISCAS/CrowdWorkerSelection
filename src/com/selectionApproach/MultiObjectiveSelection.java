package com.selectionApproach;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import jmetal.core.Algorithm;
import jmetal.core.SolutionSet;
import jmetal.operators.crossover.SinglePointCrossover;
import jmetal.operators.mutation.BitFlipMutation;
import jmetal.operators.selection.Selection;
import jmetal.operators.selection.SelectionFactory;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

public class MultiObjectiveSelection {

	public SolutionSet multiObjectiveWorkerSelection(ArrayList<String> candidatesIDs, long seed)
			throws ClassNotFoundException, JMException {
		JmetalProblem problem_ = new JmetalProblem(candidatesIDs);
		PseudoRandom.setRandomGenerator(new MyRandomGenerator(seed));
		Algorithm alg = new NsgaiiWithDebug(problem_);

		// TODO change the optimizer here
		/** for all MOEA parameters **/
		// TODO set up all parameter here
		alg.setInputParameter("populationSize", 50);
		alg.setInputParameter("maxEvaluations", 50 * 10);

		/**
		 * apply naive crossover and mutation; apply binary domination as
		 * default in jMetal
		 **/
		HashMap<String, Object> parameters = new HashMap<String, Object>();

		parameters.clear();
		parameters.put("probability", 0.8);
		alg.addOperator("crossover", new SinglePointCrossover(parameters));

		parameters.clear();
		parameters.put("probability", 0.2);
		alg.addOperator("mutation", new BitFlipMutation(parameters));

		parameters.clear();
		Selection selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters);
		alg.addOperator("selection", selection);

		SolutionSet res = alg.execute();

		return res;
	}

	public static ArrayList<String> obtainCandidateIDs() {
		ArrayList<String> candidatesIDs = new ArrayList<String>();

		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(new File("data/input/candidates.csv")));

			String line = "";
			while ((line = reader.readLine()) != null) {
				String userId = line.trim();
				candidatesIDs.add(userId);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return candidatesIDs;
	}

	public static void main(String[] args) throws ClassNotFoundException, JMException {
		MultiObjectiveSelection selectionTool = new MultiObjectiveSelection();
		ArrayList<String> candidateIDs = MultiObjectiveSelection.obtainCandidateIDs();
		selectionTool.multiObjectiveWorkerSelection(candidateIDs, 12345L);
	}
}