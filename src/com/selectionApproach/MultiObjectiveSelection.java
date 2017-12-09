package com.selectionApproach;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedMap;

import jmetal.core.Algorithm;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.operators.crossover.SinglePointCrossover;
import jmetal.operators.mutation.BitFlipMutation;
import jmetal.operators.selection.Selection;
import jmetal.operators.selection.SelectionFactory;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

/**
 * 
 * @author jianfeng (jchen37@ncsu.edu)
 * @version 1.0
 */

// Apply multi-objective optimization algorithms to the
// jmetalProblem.See todos to set up the parameters

public class MultiObjectiveSelection {
	public JmetalProblem problem_ = null;

	public SolutionSet multiObjectiveWorkerSelection(ArrayList<String> candidatesIDs, long seed)
			throws ClassNotFoundException, JMException {
		problem_ = new JmetalProblem(candidatesIDs, seed);
		PseudoRandom.setRandomGenerator(new MyRandomGenerator(seed));
		NsgaiiWithDebug alg = new NsgaiiWithDebug(problem_);

		// TODO change the optimizer here
		/** for all MOEA parameters **/
		// TODO set up all parameter here
		int popSize = 100; // 2k
		int maxGeneration = 500;
		alg.setInputParameter("populationSize", popSize);
		alg.setInputParameter("maxEvaluations", popSize * maxGeneration);
		alg.setInputParameter("initPop", problem_.generateDiverseSet(popSize));

		/**
		 * apply naive crossover and mutation; apply binary domination as
		 * default in jMetal
		 **/
		HashMap<String, Object> parameters = new HashMap<String, Object>();

		parameters.clear();
		parameters.put("probability", 0.8);
		alg.addOperator("crossover", new SinglePointCrossover(parameters));

		parameters.clear();
		parameters.put("probability", 0.8);
		alg.addOperator("mutation", new BitFlipMutation(parameters));

		parameters.clear();
		Selection selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters);
		alg.addOperator("selection", selection);

		SolutionSet res = alg.execute();
		
		alg.storeParetoFrontData();
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
	
	public HashMap<Integer, ArrayList<ArrayList<String>>>  obtainWorkerSelectionResults ( SolutionSet paretoFront ) {
		HashMap<Integer, ArrayList<ArrayList<String>>> selectionResults = new HashMap<Integer, ArrayList<ArrayList<String>>>();
		
		for ( int i =0; i < paretoFront.size(); i++ ) {
			Solution s = paretoFront.get( i );
			SortedMap<String, Boolean> result = this.problem_.candidateMap( s );
			
			ArrayList<String> selectedWorkers = new ArrayList<String>();
			for ( String worker: result.keySet() ) {
				if ( result.get( worker) == Boolean.TRUE ) {
					selectedWorkers.add( worker );
				}
			}
			int workerNum = selectedWorkers.size();
			System.out.println( workerNum + ": " + selectedWorkers.toString() );
			
			ArrayList<ArrayList<String>> selectedWorkersList = null;
			if ( selectionResults.containsKey( workerNum )) {
				selectedWorkersList = selectionResults.get( workerNum );
			}else {
				selectedWorkersList = new ArrayList<ArrayList<String>>();
			}
			selectedWorkersList.add( selectedWorkers );
			selectionResults.put( workerNum, selectedWorkersList );
		}
		return selectionResults;		
	}
	
	public static void main(String[] args) throws ClassNotFoundException, JMException {
		MultiObjectiveSelection selectionTool = new MultiObjectiveSelection();
		ArrayList<String> candidateIDs = MultiObjectiveSelection.obtainCandidateIDs();
		SolutionSet paretoFroniter = selectionTool.multiObjectiveWorkerSelection(candidateIDs, 12345L);
		selectionTool.obtainWorkerSelectionResults(paretoFroniter );
		
		/*
		System.out.println("=======");
		// demo ....
		for (int i = 0; i < paretoFroniter.size(); i++) {
			Solution s = paretoFroniter.get(i);
			System.out.println(s.getObjective(0) + " " + s.getObjective(1) + " " + s.getObjective(2));
			System.out.println(selectionTool.problem_.candidateMap(s));
		}
		*/
	}
}