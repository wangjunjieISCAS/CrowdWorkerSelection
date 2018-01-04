package com.dataProcess;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GaussianGeneration {
	public void generateGaussianDistribution ( ) {
		//Math.sqrt(b)*random.nextGaussian()+a
		Random random = new Random();
		double a = 0.0;
		double b = 100;
		ArrayList<Double> randValues = new ArrayList<Double>();
		for ( int i =0; i < 2335; i++ ) {
			double value = Math.sqrt(b)*random.nextGaussian()+a;
			randValues.add( value );
		}
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter ( "data/output/test.csv" ));
			for ( int i =0; i < randValues.size(); i++ ) {
				writer.write( randValues.get( i ).toString() );
				writer.newLine();
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
	}
}
