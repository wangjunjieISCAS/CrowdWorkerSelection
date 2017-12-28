package com.selectionApproach;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;

import com.data.Constants;
import com.data.TestProject;

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

public class SEKEMultiObjectiveSelection {
	public SEKEJmetalProblem problem_ = null;

	public SolutionSet multiObjectiveWorkerSelection(ArrayList<String> candidatesIDs, long seed, String testSetIndex, String taskId, TestProject project )
			throws ClassNotFoundException, JMException {
		problem_ = new SEKEJmetalProblem(candidatesIDs, seed, testSetIndex, taskId, project );
		PseudoRandom.setRandomGenerator(new MyRandomGenerator(seed));
		NsgaiiWithDebug alg = new NsgaiiWithDebug(problem_);

		// TODO change the optimizer here
		/** for all MOEA parameters **/
		// TODO set up all parameter here
		int popSize = 100; // 2k
		int maxGeneration = 100;
		alg.setInputParameter("populationSize", popSize);
		alg.setInputParameter("maxEvaluations", popSize * maxGeneration);
		alg.setInputParameter("initPop", problem_.generateDiverseSet(popSize));

		/**
		 * apply naive crossover and mutation; apply binary domination as
		 * default in jMetal
		 **/
		HashMap<String, Object> parameters = new HashMap<String, Object>();

		parameters.clear();
		parameters.put("probability", 0.9);
		alg.addOperator("crossover", new SinglePointCrossover(parameters));

		parameters.clear();
		parameters.put("probability", 0.9);
		alg.addOperator("mutation", new BitFlipMutation(parameters));

		parameters.clear();
		Selection selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters);
		alg.addOperator("selection", selection);

		SolutionSet res = alg.execute();
		
		alg.storeParetoFrontData();
		return res;
	}

	public HashMap<Integer, ArrayList<ArrayList<String>>>  obtainWorkerSelectionResults ( SolutionSet paretoFront, String taskId ) {
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
		
		List<HashMap.Entry<Integer, ArrayList<ArrayList<String>>>> selectionResultsList = new ArrayList<HashMap.Entry<Integer, ArrayList<ArrayList<String>>>>(selectionResults.entrySet() );

		Collections.sort( selectionResultsList, new Comparator<HashMap.Entry<Integer, ArrayList<ArrayList<String>>>>() {   
			public int compare(HashMap.Entry<Integer, ArrayList<ArrayList<String>>> o1, HashMap.Entry<Integer, ArrayList<ArrayList<String>>> o2) {      
			        //return (o2.getValue() - o1.getValue()); 
			        return o1.getKey().compareTo(o2.getKey() ) ;
			    }
			}); 
			
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( Constants.SELECTION_RESULTS_FOLDER + "/baselineSEKE/" + taskId + ".txt" ));
			for ( int i =0; i < selectionResultsList.size(); i++ ) {
				HashMap.Entry<Integer, ArrayList<ArrayList<String>>> entry = selectionResultsList.get( i );
				Integer setSize = entry.getKey();
				ArrayList<ArrayList<String>> workersList = entry.getValue();
				for ( int j =0; j < workersList.size(); j++ ) {
					ArrayList<String> workers = workersList.get( j );
					writer.write( setSize + ":");
					for ( int k =0; k < workers.size(); k++ ) {
						writer.write( workers.get( k ) + " ");
					}
					writer.newLine();
				}
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return selectionResults;		
	}
}