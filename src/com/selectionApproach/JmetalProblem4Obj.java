package com.selectionApproach;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import com.data.TestProject;
import com.mainMOCOS.SelectionSchema;
import com.selectionObjective.SelectionObjectives;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinarySolutionType;
import jmetal.encodings.variable.Binary;
import jmetal.util.JMException;

public class JmetalProblem4Obj extends Problem {
	private static final long serialVersionUID = -3530441329740021364L;
	private ArrayList<String> ids;
	private Random rand;
	SelectionObjectives objectiveTool;
	

	public JmetalProblem4Obj(ArrayList<String> candidatesIDs, String testSetIndex, String taskId, TestProject project ) {
		this.numberOfObjectives_ = 4;
		this.numberOfVariables_ = candidatesIDs.size();
		this.solutionType_ = new BinarySolutionType(this);
		this.ids = candidatesIDs;
		rand = new Random();
		
		objectiveTool = new SelectionObjectives ( testSetIndex, taskId, project );
	}

	public JmetalProblem4Obj(ArrayList<String> candidatesIDs, long seed, String testSetIndex, String taskId, TestProject project ) {
		this.numberOfObjectives_ = 4;
		this.numberOfVariables_ = candidatesIDs.size();
		this.solutionType_ = new BinarySolutionType(this);
		this.ids = candidatesIDs;
		rand = new Random(seed);
		
		objectiveTool = new SelectionObjectives ( testSetIndex, taskId, project );
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
		double bugProb = objectiveTool.extractBugProbability(selectionChoice);
		double relevance = objectiveTool.extractRelevance(selectionChoice );
		double diversity = objectiveTool.extractDiversity( selectionChoice );
		
		double cost = objectiveTool.extractCost(selectionChoice);

		solution.setObjective(0, -bugProb);
		solution.setObjective(1, -relevance);
		solution.setObjective(2, -diversity);
		
		solution.setObjective(3, cost);

		// System.out.println("bugProb is: " + bugProb + ". Diversity is: " +
		// diversity + ". Cost is: " + cost);
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
	
	public SolutionSet generateGreedyInitialSet ( ArrayList<String> candidatesIDs, String taskId ) {
		SelectionSchema selectionTool = new SelectionSchema();
		HashMap<Integer, ArrayList<ArrayList<String>>> selectionResults = selectionTool.readSelectionResults( "MOCOSWeight", taskId);
		
		int count = selectionResults.size();
		if ( count % 2 == 1 ) {
			count = count -1;
		}
		SolutionSet result = new SolutionSet( count );
		
		for ( Integer selectionNum : selectionResults.keySet() ) {
			//默认只取每个selectionNum的一个选择
			ArrayList<String> selection = selectionResults.get( selectionNum).get( 0 );
			//System.out.println( selection.get(0) );
			
			try {
				Solution sol = new Solution(this);
				Variable[] variables = sol.getDecisionVariables();
				//System.out.println ( "===================== " + variables.length); 
				
				int index = 0;
				for (Variable v : variables) {
					String userId = candidatesIDs.get( index );
					boolean tag = false;
					if ( selection.contains( userId )) {
						tag = true;
					}
					((Binary) v).setIth( 0, tag);
					
					index++;
				}
				
				/*
				variables = sol.getDecisionVariables();
				for ( Variable v: variables ) {
					System.out.print( (Binary)v);
				}
				System.out.println ();
				*/
				//System.out.println( sol.getDecisionVariables().toString() );
				result.add(sol);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		//System.out.println( result.toString() );
		return result;
	}
}
