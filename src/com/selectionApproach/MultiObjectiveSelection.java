package com.selectionApproach;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;

import com.data.Capability;
import com.data.Constants;
import com.data.CrowdWorker;
import com.data.DomainKnowledge;
import com.data.Phone;
import com.data.TestProject;
import com.performanceEvaluation.BugDetectionRateEvaluation;

import jmetal.core.Algorithm;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.core.Variable;
import jmetal.encodings.variable.Binary;
import jmetal.operators.crossover.SinglePointCrossover;
import jmetal.operators.mutation.BitFlipMutation;
import jmetal.operators.selection.Selection;
import jmetal.operators.selection.SelectionFactory;
import jmetal.util.JMException;
import jmetal.util.NonDominatedSolutionList;
import jmetal.qualityIndicator.*;
import jmetal.util.PseudoRandom;

/**
 * 
 * @author jianfeng (jchen37@ncsu.edu)
 * @version 1.0
 */

// Apply multi-objective optimization algorithms to the
// jmetalProblem.See todos to set up the parameters

public class MultiObjectiveSelection {
	//public JmetalProblem problem_ = null;
	public JmetalProblem4Obj problem_ = null;
	
	public MultiObjectiveSelection() {
		
	}
	
	public MultiObjectiveSelection ( int variableNum  ) {
		problem_ = new JmetalProblem4Obj( variableNum );
	}
	
	public SolutionSet multiObjectiveWorkerSelection(ArrayList<String> candidatesIDs, long seed, String testSetIndex, String taskId, TestProject project )
			throws ClassNotFoundException, JMException {
		//problem_ = new JmetalProblem(candidatesIDs, seed, testSetIndex, taskId, project );
		problem_ = new JmetalProblem4Obj(candidatesIDs, seed, testSetIndex, taskId, project );
		
		PseudoRandom.setRandomGenerator(new MyRandomGenerator(seed));
		NsgaiiWithDebug alg = new NsgaiiWithDebug(problem_);

		// TODO change the optimizer here
		/** for all MOEA parameters **/
		// TODO set up all parameter here
		
		/*
		 * initialize based on previously obtained data
		SolutionSet initSolution = problem_.generateGreedyInitialSet(candidatesIDs, taskId);
		int popSize = initSolution.size();
		int maxGeneration = 4;
		alg.setInputParameter("populationSize", popSize);
		alg.setInputParameter("maxEvaluations", popSize * maxGeneration);
		alg.setInputParameter("initPop", initSolution );
		*/
		
		//random initialize
		int popSize =100;
		int maxGeneration = 200;
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
		
		/*
		parameters.clear();
		parameters.put( "candidate", candidatesIDs );
		parameters.put( "taskId", taskId );
		parameters.put( "probability", 0.5);
		alg.addOperator("mutation", new PriorKnowMutation(parameters));
		*/

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

	public ArrayList<String> obtainCandidateIDs() {
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
	
	
	public void storeParetoFront (SolutionSet paretoFront, String fileName ) {
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( fileName, true ) );

			for ( int i =0; i < paretoFront.size(); i++ ) {
				Solution s = paretoFront.get( i );
				String str = "";
				for ( int j =0; j < s.getDecisionVariables().length; j++ ) {
					str += s.getDecisionVariables()[j];
				}			
				writer.write( str + ",");
				
				for ( int j =0; j < this.problem_.getNumberOfObjectives(); j++ ) {
					writer.write( s.getObjective(j)  + ",");
				}
				writer.newLine();
			}
			writer.write( "====================================================");
			writer.newLine();
			writer.flush();			
			writer.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	 
	public SolutionSet readParetoFront ( String fileName , Boolean isFirst) {
		NonDominatedSolutionList doSolutionList = new NonDominatedSolutionList();
		
		try {			
			BufferedReader reader = new BufferedReader(new FileReader(new File( fileName ) ) );
			String line = "";
			while ((line = reader.readLine()) != null) {
				int index = 0;
				
				if ( line.startsWith( "====")) {
					if ( isFirst == true ) {
						break;
					}
					else {
						continue;
					}
				}					
				
				String[] temp = line.split( ",");
				
				Solution solution = new Solution( this.problem_ );
				Variable[] variables = solution.getDecisionVariables();
				for (Variable v : variables) {
					boolean tag = true;
					char ch = temp[0].charAt( index );
					if ( ch == '0')
						tag = false;
					((Binary) v).setIth( 0, tag);
				}
				solution.setDecisionVariables( variables );
				
				for ( int i = 1; i < temp.length; i++ ) {
					solution.setObjective( i-1, Double.parseDouble( temp[i]) ); 
				}
				
				doSolutionList.add( solution );
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return doSolutionList;
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
			//System.out.println( workerNum + ": " + selectedWorkers.toString() );
			
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
			BufferedWriter writer = new BufferedWriter( new FileWriter ( Constants.SELECTION_RESULTS_FOLDER + "/" + taskId + ".txt" ));
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
	
	public static void main(String[] args) {
		MultiObjectiveSelection selectionTool = new MultiObjectiveSelection( );
		selectionTool.readParetoFront( "data/output/front.txt", true );
		
		ArrayList<String> candidateIDs = selectionTool.obtainCandidateIDs();
		SolutionSet paretoFroniter;
		try {
			paretoFroniter = selectionTool.multiObjectiveWorkerSelection(candidateIDs, 12345L, "20", "562", null);
			HashMap<Integer, ArrayList<ArrayList<String>>> selectionResults = selectionTool.obtainWorkerSelectionResults(paretoFroniter , "562");
			
			/*
			BugDetectionRateEvaluation evaTool = new BugDetectionRateEvaluation();
			TestProject project = null;
			evaTool.obtainBugDetectionRate(selectionResults, project);
			*/
		} catch (ClassNotFoundException | JMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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