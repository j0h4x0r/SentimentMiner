package dataio;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


public class DataReader {

	private String filename;
	private BufferedReader bufReader = null;
	
	public DataReader(String filename) {
		this.filename = filename;
		initReader();
	}
	
	private void initReader() {
		File file = new File(filename);
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("DataFile is not found.");
		}
		
		if(in != null) {
			InputStreamReader reader = null;
			try {
				reader = new InputStreamReader(in, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(reader != null)
				bufReader = new BufferedReader(reader);
		}
	}
	
	public String getWeibo() {
		if(bufReader == null)
			return null;
		
		String line = null;
		try {
			line = bufReader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//get the content from a line
		String content = null;
		if(line != null) {
			String parts[] = line.split("\t");
			content = parts[3];
			for(int i = 4; i < parts.length; i++)
				content += parts[i];
		} else { //If line is null, an exception or EOF is met.
			try {
				bufReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				bufReader = null;
			}
		}
		
		return content;
	}
	
	public void closeReader() {
		if(bufReader != null) {
			try {
				bufReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				bufReader = null;
			}
		}
	}
	
}
