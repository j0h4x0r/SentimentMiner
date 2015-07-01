package dataio;
import java.io.IOException;
import java.util.List;

import constant.DefaultConstant;


public class VectorWriter extends DataWriter {
	
	private int linesCount = 0;
	
	public VectorWriter(String filename) {
		super(filename);
	}
	
	public VectorWriter() {
		super(DefaultConstant.DEFAULT_VECTOR_FILE);
	}
	
	public int writeVector(String vector) {
		if(bufWriter == null)
			return -1;
		
		try {
			bufWriter.write(vector);
			bufWriter.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			closeWriter();
			return -1;
		}
		
		return ++linesCount;
	}
	
	public int writeLDAVector(List<Integer> vector) {
		String sv = "";
		for(int attr : vector) {
			sv += (attr + 1) + " ";
		}
		
		return writeVector(sv);
	}
	
	public int writeSVMVector(int polar, String vector) {
		String sv = polar + " ";
		String[] vectors = vector.split(" ");
		for(int i = 0; i < vectors.length; i++) {
			sv += (i + 1) + ":" + vectors[i] + " ";
		}
		
		return writeVector(sv);
	}
	
}
