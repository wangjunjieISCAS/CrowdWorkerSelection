package com.selectionApproach;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.mainMOCOS.SelectionSchema;

import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.variable.Binary;
import jmetal.operators.mutation.Mutation;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

public class PriorKnowMutation extends Mutation {
	
	private ArrayList<String> candidatesIDs;
	private String taskId;
	private Double probability;
	
	public PriorKnowMutation(HashMap<String, Object> parameters) {
		super(parameters);
		if (parameters.get("candidate") != null) {
			candidatesIDs = (ArrayList<String>) parameters.get("candidate") ;  		
		}
		if (parameters.get("taskId") != null) {
			taskId = (String) parameters.get("taskId") ;  		
		}
		if (parameters.get("probability") != null) {
			probability = (Double) parameters.get("probability") ;  		
		}
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(Object object) throws JMException {
		// TODO Auto-generated method stub
		SelectionSchema selectionTool = new SelectionSchema();
		HashMap<Integer, ArrayList<ArrayList<String>>> selectionResults = selectionTool.readSelectionResults( "MOCOSWeight", taskId);
		HashSet<String> selectedWorkers = new HashSet<String>();
		for ( Integer selectNum : selectionResults.keySet() ) {
			ArrayList<String> workers =  selectionResults.get( selectNum).get( 0);
			for ( int i=0; i < workers.size(); i++ ) {
				selectedWorkers.add( workers.get( i ));
			}
		}
		
		Solution solution = (Solution) object;
		Variable[] variables = solution.getDecisionVariables();
		/*
		System.out.print ( "before mutation!");
		for ( Variable v: variables ) {
			System.out.print( (Binary)v);
		}
		System.out.println ();
		*/
		
		int index = 0;
		for (Variable v : variables) {
			String userId = candidatesIDs.get( index );
			
			if (PseudoRandom.randDouble() < probability) {
				if ( selectedWorkers.contains( userId )) {
					((Binary) v).setIth( 0, true);
				}
				else {
					((Binary)v).bits_.flip( index );
				}
			}			
			index++;
		}
		
		/*
		variables = solution.getDecisionVariables();
		System.out.print ( "after  mutation!");
		for ( Variable v: variables ) {
			System.out.print( (Binary)v);
		}
		System.out.println ();
		*/
		return solution;
	}

}
