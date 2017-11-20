package com.data;

public class Constants {
	public final static String INPUT_FILE_STOP_WORD = "data/input/stopWordListBrief.txt";
	
	public final static Integer TASK_DES_LENGTH = 500;
	
	//filter the 5% terms with the largest document frequency or smallest document frequency 
	public final static Double THRES_FILTER_TERMS_DF = 0.05;
	
	//when to compute the diversity, the phone info and domain knowledge info should be considered, 
	public final static Double DIVERSITY_PHONE_WEIGHT = 0.5;
	
	public final static Integer DEFAULT_DOMAIN_LENGTH = 50;
	
	public final static String BUG_PROB_FILE = "data/output/bugProbability.csv";
	public final static String WORKER_PHONE_FILE = "data/output/workerPhone.csv";
	public final static String WORKER_CAP_FILE = "data/output/workerCap.csv";
	public final static String WORKER_DOMAIN_FILE = "data/output/workerDomain.csv";
}
