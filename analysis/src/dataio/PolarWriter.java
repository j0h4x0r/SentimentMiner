package dataio;
import java.io.IOException;

import constant.DefaultConstant;


public class PolarWriter extends DataWriter {
		
	public PolarWriter(String filename) {
		super(filename);
	}
	
	public PolarWriter() {
		super(DefaultConstant.DEFAULT_POLAR_FILE);
	}
	
	public int writePolar(int polar) {
		if(bufWriter == null)
			return -1;
		
		try {
//			if(polar > 0)
//				bufWriter.write("1");
//			else if(polar < 0)
//				bufWriter.write("-1");
//			else
//				bufWriter.write("0");
			bufWriter.write(String.valueOf(polar));
			bufWriter.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			closeWriter();
			return 0;
		}
		
		return polar;
	}

}
