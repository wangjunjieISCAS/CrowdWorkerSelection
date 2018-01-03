package com.performanceEvaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.data.TestProject;
import com.data.TestReport;

public class ProbPredictEvaluation {
	Double threshold = 0.7;
	/*
	 * 可能存在一个人在一个task里面提交多个report的情况，这些report可能既有bug，又有no-bug，
	 * 但是对于预测的结果，每个人对于每个任务只有一个预测结果
	 * 所以在进行性能评价时，根据人员进行统计；对于多个报告的情况，只要有一个label和预测的相同，就算预测正确
	 */
	public Double[] obtainProbPredictionPerformance ( HashMap<String, Double> bugProbWorkerResults, TestProject project ) {
		HashMap<String, HashSet<Boolean>> trueLabelList = new HashMap<String, HashSet<Boolean>>();
		for ( int i = 0; i < project.getTestReportsInProj().size(); i++ ) {
			TestReport report = project.getTestReportsInProj().get( i );
			String userId = report.getUserId();
			String tag = report.getTag();
			
			boolean result = false;
			if ( tag.equals( "审核通过"))
				result = true;
			
			HashSet<Boolean> labels = new HashSet<Boolean>();
			if ( trueLabelList.containsKey( userId )) {
				labels = trueLabelList.get( userId );
			}
			labels.add( result );
			
			trueLabelList.put( userId, labels );
		}	
		
		//according to the prediction to refine the true label
		HashMap<String, Boolean> newTrueLabelList = new HashMap<String, Boolean>();
		for ( String userId: trueLabelList.keySet() ) {
			if ( trueLabelList.get( userId).size() == 2 ) {
				double prob = bugProbWorkerResults.get( userId );
				if ( prob >= threshold ) {
					newTrueLabelList.put( userId , Boolean.TRUE );
				}
				else {
					newTrueLabelList.put( userId, Boolean.FALSE );
				}
			}
			else {
				newTrueLabelList.put ( userId, trueLabelList.get( userId).iterator().next() );
			}
		}
		
		//precision, recall, F-measure, AUC
		int truePositive = 0, trueNegative = 0, falsePositive = 0,  falseNegative = 0;
		for ( String userId : newTrueLabelList.keySet() ) {
			Boolean trueLabel = newTrueLabelList.get( userId );
			Double predictLabel = bugProbWorkerResults.get( userId );
			
			if ( trueLabel == Boolean.TRUE && predictLabel >= threshold  ) {
				truePositive ++;
			}
			if ( trueLabel == Boolean.TRUE && predictLabel < threshold  ) {
				falseNegative ++;
			}
			if ( trueLabel == Boolean.FALSE && predictLabel >= threshold  ) {
				falsePositive ++;
			}
			if ( trueLabel == Boolean.FALSE && predictLabel < threshold  ) {
				trueNegative ++;
			}
		}
		
		double precision = (1.0*truePositive) / (truePositive + falsePositive);
		double recall = (1.0*truePositive) / (truePositive + falseNegative );
		double FMeasure = 2*precision*recall / (precision+recall);
		
		//得到AUC
		List<HashMap.Entry<String, Double>> bugProbList = new ArrayList<HashMap.Entry<String, Double>>(bugProbWorkerResults.entrySet());
		Collections.sort( bugProbList, new Comparator<HashMap.Entry<String, Double>>() {   
			public int compare(HashMap.Entry<String, Double> o1, HashMap.Entry<String, Double> o2) {      
			        return o2.getValue().compareTo(o1.getValue() ) ;
			    }
			}); 
		
		int rank = bugProbList.size();
		int positiveRankSum = 0;
		
		int positiveNum =0, negativeNum = 0;
		for ( int i =0; i < bugProbList.size(); i++ ){
			String userId = bugProbList.get(i).getKey();
			
			Boolean trueLabel = newTrueLabelList.get( userId );
			if ( trueLabel == Boolean.TRUE ){    //正样本
				positiveRankSum += rank;
				
				positiveNum ++;
			}
			else{
				negativeNum ++;
			}
			
			rank--;
		}
		
		double AUC = (1.0 * positiveNum ) * ( 1.0*(positiveNum+1)) / 2.0;
		AUC = positiveRankSum - AUC;
		AUC = AUC / ( 1.0 * positiveNum * negativeNum );
			
		System.out.println( "precision:" + precision + " recall:" + recall + " F-Measure:" + FMeasure + " AUC:" + AUC );	
		Double[] result = new Double[8];
		result[0] = precision;
		result[1] = recall;
		result[2] = FMeasure;
		result[3] = AUC;
		
		result[4] = 1.0 * truePositive;
		result[5] = 1.0 * trueNegative;
		result[6] = 1.0 * falsePositive;
		result[7] = 1.0 * falseNegative;
		
		return result;
	}
	
	public static void main ( String args[] ) {
		ProbPredictEvaluation evaluation = new ProbPredictEvaluation();
		//evaluation.obtainProbPredictionPerformance(bugProbWorkerResults, project);
	}
}
