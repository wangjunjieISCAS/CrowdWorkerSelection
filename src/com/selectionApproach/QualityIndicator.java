package com.selectionApproach;

import com.data.Constants;

import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.qualityIndicator.Epsilon;
import jmetal.qualityIndicator.GeneralizedSpread;
import jmetal.qualityIndicator.Hypervolume;
import jmetal.qualityIndicator.InvertedGenerationalDistance;

public class QualityIndicator {
	int numberOfObjectives = 4;
	
	public double[] obtainQualityIndicators ( SolutionSet paretoFront, String taskId ) {
		double[][] oriActualFront = this.obtainParetoFrontInFormat(paretoFront);
		double[][] actualFront = this.zeroOneNormalization( oriActualFront );
		
		Integer variableNum = this.obtainVariableNum( paretoFront );
		MultiObjectiveSelection selectionTool = new MultiObjectiveSelection(variableNum );
		SolutionSet trueParetoFront = selectionTool.readParetoFront(  Constants.PARETO_FRONT_FOLDER + "/" + taskId + "-front.txt" , false);
		
		double[][] oriTrueFront = this.obtainParetoFrontInFormat( trueParetoFront );
		double[][] trueFront = this.zeroOneNormalization( oriTrueFront );
		
		Hypervolume HVTool = new Hypervolume();
		double HVValue = HVTool.hypervolume( actualFront, trueFront, numberOfObjectives);
		
		//GeneralizedSpread GSTool = new GeneralizedSpread();
		//double GSValue = GSTool.generalizedSpread( actualFront, trueFront, numberOfObjectives );
		
		Epsilon EPTool = new Epsilon();
		double EPValue = EPTool.epsilon( trueFront, actualFront, numberOfObjectives );
		
		InvertedGenerationalDistance IGDTool = new InvertedGenerationalDistance();
		double IGDValue = IGDTool.invertedGenerationalDistance( actualFront, trueFront, numberOfObjectives );
		
		System.out.println( "========================================== HVValue is : " + HVValue + " EPValue is : " + EPValue + " IGDValue is : " + IGDValue );
		
		double[] results = new double[3];
		results[0] = HVValue;
		results[1] = EPValue;
		results[2] = IGDValue;
		
		return results;
	}
	
	public double[][] zeroOneNormalization ( double[][] frontValues ) {
		double max = frontValues[0][0], min = frontValues[0][0];
		for ( int i =0; i < frontValues.length; i++ ) {
			for ( int j =0; j < frontValues[i].length ; j++ ) {
				if ( frontValues[i][j] < 0 )
					frontValues[i][j] =  (-1)*frontValues[i][j];
			}
		}
		
		for ( int i =0; i < frontValues.length; i++ ) {
			for ( int j =0; j < frontValues[i].length; j++ ) {
				if ( frontValues[i][j] > max ) {
					max = frontValues[i][j];
				}
				if ( frontValues[i][j] < min ) {
					min = frontValues[i][j];
				}
			}
		}
		
		double[][] newFrontValues = new double[frontValues.length][frontValues[0].length];
		for ( int i =0; i < frontValues.length; i++ ) {
			for ( int j =0; j < frontValues[i].length; j++ ) {
				double newValue = (frontValues[i][j] - min ) / (max- min);
				newFrontValues[i][j] = newValue;
			}
		}
		return newFrontValues;
	}
	
	public Integer obtainVariableNum ( SolutionSet paretoFront ) {
		Solution sol = paretoFront.get(0);
		int variableNum = sol.getDecisionVariables().length;
		return variableNum;
	}
	
	public double[][] obtainParetoFrontInFormat ( SolutionSet paretoFront ){
		int resultNum = paretoFront.size();
		double[][] front = new double[resultNum][numberOfObjectives];
		for ( int i = 0; i < resultNum ; i++ ) {
			for ( int j =0; j < numberOfObjectives; j++ ) {
				front[i][j] = paretoFront.get(i).getObjective(j );
			}
		}
		return front;
	}
}
