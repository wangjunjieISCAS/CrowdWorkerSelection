package com.dataProcess;

import java.util.ArrayList;
import java.util.HashSet;

public class SimilarityMeasure {
	//to compute the relevance between task description and worker's domain knowledge
	public Double cosinSimilarity ( ArrayList<String> vector1, ArrayList<String> vector2 ) {
		HashSet<String> totalTermList = new HashSet<String>();
		
		for ( int i =0; i < vector1.size(); i++ ) {
			totalTermList.add( vector1.get(i));
		}
		for ( int i =0; i < vector2.size(); i++ ) {
			totalTermList.add( vector2.get( i ));
		}
		
		int v1sum = 0, v2sum = 0, multiply = 0;
		for ( String term: totalTermList ) {
			int v1 = 0, v2 = 0;
			if ( vector1.contains( term ))
				v1 = 1;
			if ( vector2.contains( term ))
				v2 = 1;
			
			v1sum += v1*v1;
			v2sum += v2*v2;
			multiply += v1*v2;
		}
		
		double sim = (1.0*multiply) / (Math.sqrt( 1.0*v1sum ) * Math.sqrt( 1.0*v2sum ));
		return sim;
	}
}
