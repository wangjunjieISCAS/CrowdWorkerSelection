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
	 * ���ܴ���һ������һ��task�����ύ���report���������Щreport���ܼ���bug������no-bug��
	 * ���Ƕ���Ԥ��Ľ����ÿ���˶���ÿ������ֻ��һ��Ԥ����
	 * �����ڽ�����������ʱ��������Ա����ͳ�ƣ����ڶ������������ֻҪ��һ��label��Ԥ�����ͬ������Ԥ����ȷ
	 */
	public Double[] obtainProbPredictionPerformance ( HashMap<String, Double> bugProbWorkerResults, TestProject project ) {
		HashMap<String, HashSet<Boolean>> trueLabelList = new HashMap<String, HashSet<Boolean>>();
		for ( int i = 0; i < project.getTestReportsInProj().size(); i++ ) {
			TestReport report = project.getTestReportsInProj().get( i );
			String userId = report.getUserId();
			String tag = report.getTag();
			
			boolean result = false;
			if ( tag.equals( "���ͨ��"))
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
		
		//�õ�AUC
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
			if ( trueLabel == Boolean.TRUE ){    //������
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
