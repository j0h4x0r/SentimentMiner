package analyzer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import constant.DefaultConstant;
import dataio.DataReader;
import dataio.PolarWriter;
import dataio.VectorWriter;
import jgibblda.Estimator;
import jgibblda.LDACmdOption;


public class FeatureExtractor {

	private Map<String, Integer> wordsIndexMap = new HashMap<String, Integer>();
	private WordSegmentor wordSeg = new WordSegmentor();
	private EmotionDictionary emoDic = new EmotionDictionary();
	private DataReader reader;
	private VectorWriter vwriter;
	private PolarWriter pwriter;
	private int dataNum = 0;
	
	public FeatureExtractor() {
		reader = new DataReader(DefaultConstant.DEFAULT_DATA_FILE);
		vwriter = new VectorWriter();
		pwriter = new PolarWriter();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FeatureExtractor extractor = new FeatureExtractor();
		
		System.out.println("Adding emotion dictionary....");
		extractor.getEmoDic().addDic(DefaultConstant.DEFAULT_POSITIVE_DIC, true);
		extractor.getEmoDic().addDic(DefaultConstant.DEFAULT_NEGATIVE_DIC, false);
		
		System.out.println("Building text vector....");
		extractor.buildPolarVector();
//		extractor.dimensionReduce();
//		extractor.buildSVMFile();
//		
//		System.out.println("Regressing....");
//		SentimentAnalyst sa = new SentimentAnalyst();
//		sa.regression();
//		
		System.out.println("Done!");
	}
	
	public void buildSVMFile() {
		VectorWriter out = new VectorWriter(DefaultConstant.DEFAULT_TVECTOR_FILE);
		try {
			BufferedReader polarIn = new BufferedReader(new FileReader(DefaultConstant.DEFAULT_POLAR_FILE));
			BufferedReader vectorIn = new BufferedReader(new FileReader(DefaultConstant.DEFAULT_LDA_THETA_FILE));
			String vector;
			while((vector = vectorIn.readLine()) != null) {
				int polar = Integer.parseInt(polarIn.readLine());
				out.writeSVMVector(polar, vector);
			}
			polarIn.close();
			vectorIn.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.closeWriter();
	}
	
	public void buildPolarVector() {
		String weibo = null;
		int polar = 0;
		
		while((weibo = reader.getWeibo()) != null) {
			this.dataNum++;
			List<String> words = wordSeg.getWords(weibo);
			if(words == null)
				break;
			
			polar = judgePolarByDic(weibo); // EmotionDictionary.testWeibo() can also be used here, but much slower.
			//polar = emoDic.testWeibo(weibo);
			pwriter.writePolar(polar);
			
			// build vector
			List<Integer> vector = new ArrayList<Integer>();
			for(String word : words) {
				if(wordsIndexMap.containsKey(word)) {
					int index = wordsIndexMap.get(word);
					// avoid duplication
					if(!vector.contains(index))
						vector.add(index);
				} else {
					int index = wordsIndexMap.size();
					wordsIndexMap.put(word, index);
					vector.add(index);
				}
			}
			
			//write vector
			Collections.sort(vector);
			vwriter.writeLDAVector(vector);
		}
		
		closeIOStream();
		
		// record total numbers of data for LDA model
		String content = dataNum + System.getProperty("line.separator");
		try {
			BufferedReader in = new BufferedReader(new FileReader(DefaultConstant.DEFAULT_VECTOR_FILE));
			String line;
			while((line = in.readLine()) != null)
				content += line + System.getProperty("line.separator");
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			FileWriter out = new FileWriter(DefaultConstant.DEFAULT_LDA_VECTOR_FILE);
			out.write(content);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void dimensionReduce() {
		LDACmdOption ldaOption = new LDACmdOption();
		ldaOption.dir = DefaultConstant.DEFAULT_LDA_DIR;
		ldaOption.niters = 2000;
		ldaOption.savestep = 2000;
		ldaOption.est = true;
		ldaOption.dfile = DefaultConstant.DEFAULT_VECTOR_FILENAME;
		
		Estimator estimator = new Estimator();
		estimator.init(ldaOption);
		estimator.estimate();
	}
	
	public int judgePolarByDic(String weibo) {
		/* TODO We can expand the dictionary here.
		 * The basic idea is to find word-pairs that usually appear during this loop and then expand dic.
		 * And then we can recalculate the polar again.
		 */
		int flag, polar = 0;
		List<String> words = wordSeg.getWords(weibo);
		for(String word : words) {
			if((flag = emoDic.testWord(word.split("/")[0])) != 0) {
				polar += flag;
			}
		}
		
		return polar;
	}
	
	public void closeIOStream() {
		reader.closeReader();
		vwriter.closeWriter();
		pwriter.closeWriter();
	}

	public Map<String, Integer> getWordsIndexMap() {
		return wordsIndexMap;
	}

	public WordSegmentor getWordSeg() {
		return wordSeg;
	}

	public EmotionDictionary getEmoDic() {
		return emoDic;
	}
	
}
