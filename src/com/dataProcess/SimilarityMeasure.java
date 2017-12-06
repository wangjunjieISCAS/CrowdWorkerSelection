package com.dataProcess;

import java.util.ArrayList;
import java.util.Collections;
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
		
		Collections.sort( vector1 );
		Collections.sort( vector2 );
		Integer[][] dis = new Integer[vector1.size()+1][vector2.size()+1];
		
		for ( int i =0; i <= vector1.size(); i++ ) {
			dis[i][0] = i;
		}
		for ( int j =0; j <= vector2.size(); j++ ) {
			dis[0][j] = j;
		}
		for ( int i =1; i <= vector1.size(); i++ ) {
			for ( int j =1; j <= vector2.size(); j++ ) {
				if ( vector1.get( i-1).equals( vector2.get( j-1))) {
					dis[i][j] = dis[i-1][j-1];
				}
				else {
					dis[i][j] = Math.min( dis[i-1][j]+1, dis[i][j-1] + 1);
					dis[i][j] = Math.min( dis[i][j], dis[i-1][j-1] + 1 );
				}
			}
		}
		
		double dist = dis[vector1.size()][vector2.size()];
		return dist;
	}
}
