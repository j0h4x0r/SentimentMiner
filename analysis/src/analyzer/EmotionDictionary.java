package analyzer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


public class EmotionDictionary {
	
	private Map<String, Boolean> emotionDic = new HashMap<String, Boolean>();
	
	public EmotionDictionary(String dicName, int type) {
		if(type == -1) // negative words
			addDic(dicName, false);
		else if(type == 1) // positive words
			addDic(dicName, true);
	}
	
	public EmotionDictionary() {
		super();
	}
	
	public void addDic(String filename, boolean polar) {
		BufferedReader bufReader = null;
		File file = new File(filename);
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Dictionary file is not found.");
		}
		
		if(in != null) {
			InputStreamReader reader = null;
			try {
				reader = new InputStreamReader(in, "GBK");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(reader != null)
				bufReader = new BufferedReader(reader);
		}
		
		if(bufReader != null) {
			String word;
			try {
				while((word = bufReader.readLine()) != null) {
					emotionDic.put(word, polar);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					bufReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public int testWord(String word) {
		Boolean polar;
		if((polar = emotionDic.get(word)) != null)
			return polar ? 1 : -1;
		else
			return 0;
	}
	
	public int testWeibo(String weibo) {
		int polar = 0;
		Iterator<Entry<String, Boolean>> iter = emotionDic.entrySet().iterator();
		while(iter.hasNext()) {
			Entry<String, Boolean> entry = iter.next();
			if(weibo.contains(entry.getKey()))
				polar += entry.getValue() ? 1 : -1;
		}
		return polar;
	}
	
	public void clearDic() {
		emotionDic.clear();
	}
}
