package com.data;

public class Constants {
	public final static String INPUT_FILE_STOP_WORD = "data/input/stopWordListBrief.txt";
	
	public final static Integer TASK_DES_LENGTH = 500;
	
	//filter the 5% terms with the largest document frequency or smallest document frequency 
	public final static Double THRES_FILTER_TERMS_DF = 0.05;
	
	public final static Integer DEFAULT_DOMAIN_LENGTH = 50;
}
