package com.selectionApproach;

import com.data.Constants;

import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.qualityIndicator.GeneralizedSpread;
import jmetal.qualityIndicator.Hypervolume;
import jmetal.qualityIndicator.InvertedGenerationalDistance;

public class QualityIndicator {
	int numberOfObjectives = 4;
	
	public void obtainQualityIndicators ( SolutionSet paretoFront, String taskId ) {
		double[][] actualFront = this.obtainParetoFrontInFormat(paretoFront);
		
		Integer variableNum = this.obtainVariableNum( paretoFront );
		MultiObjectiveSelection selectionTool = new MultiObjectiveSelection(variableNum );
		SolutionSet trueParetoFront = selectionTool.readParetoFront(  Constants.PARETO_FRONT_FOLDER + "/" + taskId + "-front.txt" );
		
		double[][] trueFront = this.obtainParetoFrontInFormat( trueParetoFront );
		
		Hypervolume HVTool = new Hypervolume();
		double HVValue = HVTool.hypervolume( actualFront, trueFront, numberOfObjectives);
		
		GeneralizedSpread GSTool = new GeneralizedSpread();
		double GSValue = GSTool.generalizedSpread( actualFront, trueFront, numberOfObjectives );
		
		InvertedGenerationalDistance IGDTool = new InvertedGenerationalDistance();
		double IGDValue = IGDTool.invertedGenerationalDistance( actualFront, trueFront, numberOfObjectives );
		
		System.out.println( "========================================== HVValue is : " + HVValue + " GSValue is : " + GSValue + " IGDValue is : " + IGDValue );
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
