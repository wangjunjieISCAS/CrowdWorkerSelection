package com.data;

public class Constants {
	public final static String INPUT_FILE_STOP_WORD = "data/input/stopWordListBrief.txt";
	
	public final static Integer TASK_DES_LENGTH = 5000;
	
	//filter the 5% terms with the largest document frequency or smallest document frequency 
	public final static Double THRES_FILTER_TERMS_DF = 0.05;
	
	//when to compute the diversity, the phone info and domain knowledge info should be considered, 
	public final static Double DIVERSITY_PHONE_WEIGHT = 0.5;
	
	public final static Integer DEFAULT_DOMAIN_LENGTH = 50;
	
	public final static String BUG_PROB_FILE = "data/output/bugProb/bugProbability.csv";
	public final static String WORKER_PHONE_FILE = "data/output/workerInfo/workerPhone.csv";
	public final static String WORKER_CAP_FILE = "data/output/workerInfo/workerCap.csv";
	public final static String WORKER_DOMAIN_FILE = "data/output/workerInfo/workerDomain.csv";
	
	public final static String WORKER_INFO_FOLDER  = "data/output/workerInfo";
	public final static String BUG_PROB_FOLDER  = "data/output/bugProb";
	
	public final static String BUG_PROB_PERFORMANCE = "data/output/performance";    //bugProb.csv";
	public final static String BUG_DETECTION_RATE_PERFORMANCE_FOLDER  = "data/output/performance-bugDetRate";
	public final static String SELECTION_RESULTS_FOLDER = "data/output/selectionResults";
	
	public final static String PLOT_DATA_FOLDER  = "data/output/plot"; 
	
	public final static Integer CAP_SIZE_PER_TYPE = 4;
	public final static Integer TOPIC_NUMBER  = 30;
	
	public final static String CLOSE_TIME_FOR_PROJECT_FILE  = "data/output/findings/closeTimeForProject.csv";
	public final static String TRAIN_TEST_SET_SETTING_FILE = "data/output/findings/trainTestSet.csv";
	
	public final static String TOTAL_PROJECT_FOLDER = "data/input/experimental dataset";
	public final static String TOTAL_TASK_DES_FOLDER = "data/input/taskDescription";
	
	public final static String LEARNER_TYPE = "Logistic";
	
	public final static Integer TEST_SET_INDEX = 20;
}
