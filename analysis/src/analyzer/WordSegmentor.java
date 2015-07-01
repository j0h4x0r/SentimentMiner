package analyzer;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Native;


public class WordSegmentor {
	
	private String text;
	
	public WordSegmentor(String text) {
		this.text = text;
	}
	
	public WordSegmentor() {
		super();
	}
	
	private interface CLibrary extends Library {
		CLibrary Instance = (CLibrary) Native.loadLibrary("lib/NLPIR", CLibrary.class);
		
		public int NLPIR_Init(byte[] sDataPath, int encoding, byte[] sLicenceCode);
		
		public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);
		
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public List<String> getWords(String text) {
		this.text = text;
		return getWords();
	}
	
	public List<String> getWords() {
		String libDataPath = "";
		String systemCharset = "GBK";
		//charset_type: input encoding.
		//0 -- GBK; 1 -- UTF8; 2 -- BIG5; 3 -- GBK with traditional Chinese
		int charsetType = 1;
		int initFlag = 0;
		
		try {
			initFlag = CLibrary.Instance.NLPIR_Init(libDataPath.getBytes(systemCharset), charsetType, "0".getBytes(systemCharset));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			initFlag = 0;
		} finally {
			if(initFlag == 0) {
				System.err.println("Initialization Failed!");
				return null;
			}
		}
		
		if(text == null)
			return null;
		
		String wordsString = CLibrary.Instance.NLPIR_ParagraphProcess(text, 1);
		List<String> result = processResult(wordsString);
		return result;
	}
	
	private List<String> processResult(String sString) {
		String[] words = sString.split("[ |　]+"); // continuous half or full space
		List<String> result = new ArrayList<String>();
		
		for(String word : words) {
			if(word.length() == 0)
				continue;
			if(word.charAt(0) == '/' || word.charAt(0) == '@') // "//" or username
				continue;
			String[] wordTag = word.split("/");
			if(wordTag.length == 1) // words or symbols that cannot be recognized, e.g. full-width marks
				continue;
			// filter the urls and punctuations...
			if(wordTag[1].startsWith("x") || wordTag[1].startsWith("w") || wordTag[1].startsWith("email") || wordTag[1].startsWith("url"))
				continue;
			// filter dates...
			else if(wordTag[1].startsWith("t") && word.matches("^[0-9]"))
				continue;
			// some specific kinds of words... /f,/m may be reconsidered
			else if(wordTag[1].startsWith("f") || wordTag[1].startsWith("m") || wordTag[1].startsWith("u") || wordTag[1].startsWith("c") || wordTag[1].startsWith("q") || wordTag[1].startsWith("p") || wordTag[1].startsWith("y") || wordTag[1].startsWith("h") || wordTag[1].startsWith("k"))
				continue;
			result.add(word); // One word can have multiple tags, so we record a word with tag.
		}
		
		return result;
	}
	
}
