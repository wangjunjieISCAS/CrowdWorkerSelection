package TFIDF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.data.Constants;

public class TFIDF {
	
	public ArrayList<HashMap<String, Double>> countTFIDF ( ArrayList<HashMap<String, Integer>> totalDataSet ){
		InverseDocFrequency docFreqTool = new InverseDocFrequency();
		HashMap<String, Double> docFreqList = docFreqTool.getInverseDocumentFrequency(totalDataSet);
		
		ArrayList<HashMap<String, Double>> tfIdfList = new ArrayList<HashMap<String, Double>>();
		
		for ( int i =0; i < totalDataSet.size(); i++ ) {
			HashMap<String, Integer> dataInstance = totalDataSet.get( i );
			
			HashMap<String, Double> tfIdf = new HashMap<String, Double>();
			
			for ( String word : dataInstance.keySet() ) {
				int termFreq = dataInstance.get( word);
				double docFreq = docFreqList.get( word );
				
				double tfIdfValue = termFreq * docFreq;
				tfIdf.put( word, tfIdfValue );				
			}
			
			tfIdfList.add( tfIdf );
		}
		
		return tfIdfList;
	}
	
	
	//use to filter the terms with too large or too small document frequency, and obtain the final set of technical terms
	public ArrayList<String> obtainFinalTermList ( ArrayList<HashMap<String, Integer>> totalDataSet ){
		InverseDocFrequency docFreqTool = new InverseDocFrequency();
		HashMap<String, Integer> docFreqList = docFreqTool.countDocumentFrequencyTotal ( totalDataSet );
		
		ArrayList<String> finalTermList = new ArrayList<String>();
		
		//rank according to the value of each term
		List<HashMap.Entry<String, Integer>> infoIds = new ArrayList<HashMap.Entry<String, Integer>>(docFreqList.entrySet());  
		
        // 对HashMap中的 value 进行排序  
        Collections.sort(infoIds, new Comparator<HashMap.Entry<String, Integer>>() {  
            public int compare(HashMap.Entry<String, Integer> o1,  HashMap.Entry<String, Integer> o2) {  
                return (o2.getValue()).compareTo(o1.getValue());  
            }  
        }); 
        
        int filterNum = (int) (infoIds.size() * Constants.THRES_FILTER_TERMS_DF);
        int begin = filterNum;
        int end = infoIds.size() - begin;
        for ( int j = begin; j < end; j++ ) {
        	String term = infoIds.get(j).getKey();
        	
        	finalTermList.add( term);
        }
		
		return finalTermList;
		
	}
}
