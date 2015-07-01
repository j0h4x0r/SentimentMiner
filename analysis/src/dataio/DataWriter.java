package dataio;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;


public class DataWriter {
	protected String filename;
	protected BufferedWriter bufWriter = null;
	
	public DataWriter(String filename) {
		this.filename = filename;
		initWriter();
	}
	
	protected void initWriter() {
		File file = new File(filename);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(out != null) {
			OutputStreamWriter writer = null;
			try {
				writer = new OutputStreamWriter(out, "UTF-8");
			} catch(UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(writer != null)
				bufWriter = new BufferedWriter(writer);
		}
	}
	
	public void closeWriter() {
		if(bufWriter != null) {
			try {
				bufWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				bufWriter = null;
			}
		}
	}
	
}
