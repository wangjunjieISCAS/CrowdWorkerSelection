package com.dataProcess;

import java.util.ArrayList;
import java.util.HashSet;

import com.data.Constants;
import com.data.DomainKnowledge;
import com.data.Phone;

public class SimilarityMeasure {
	//to compute the relevance between task description and worker's domain knowledge
	public Double cosineSimilarity ( ArrayList<String> vector1, ArrayList<String> vector2 ) {
		if ( vector1.size() == 0 || vector2.size() == 0 ) {
			return 0.0;
		}
		
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
	
	//used to compute the diversity of two worker's phone information
	public Double hammingDistanceForPhone ( Phone phone1, Phone phone2 ) {
		int count = 0;
		if ( phone1.getPhoneType() != phone2.getPhoneType() ) {
			count++;
		}else if ( phone1.getOS() != phone2.getOS() ) {
			count++;
		}else if ( phone1.getNetwork() != phone2.getNetwork() ) {
			count++;
		}else if ( phone1.getISP() != phone2.getISP() ) {
			count++;
		}
		
		return new Double(count);		
	}
	
	public Double hammingDistanceForDomain ( DomainKnowledge domain1, DomainKnowledge domain2 ) {
		int count = 0;
		ArrayList<String> vector1 = domain1.getDomainKnowledge();
		ArrayList<String> vector2 = domain2.getDomainKnowledge();
		
		for ( int i =0; i < vector1.size(); i++ ) {
			String term1 = vector1.get(i );
			for ( int j =0; j < vector2.size(); j++ ) {
				if ( term1.equals( vector2.get( j ))) {
					count++;
				}
			}
		}
		
		//there are several ways to compute the distance
		//for example, max(vector1.size(), vector2.size()) - count, min(vector1.size(), vector2.size()) - count
		double dist = (1.0*vector1.size() + 1.0*vector2.size()) / 2 - count;
		return dist;
	}
}
