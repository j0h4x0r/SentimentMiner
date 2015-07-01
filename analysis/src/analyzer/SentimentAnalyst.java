package analyzer;
//import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
//import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
//import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import constant.DefaultConstant;
import weka.classifiers.functions.LibSVM;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.NumericToNominal;
import weka.filters.unsupervised.instance.SubsetByExpression;


public class SentimentAnalyst {
	
//	private String trainFile = DefaultConstant.DEFAULT_TRAIN_FILE;
//	private String testFile = DefaultConstant.DEFAULT_TEST_FILE;
	private String vectorFile = DefaultConstant.DEFAULT_TVECTOR_FILE;
	private String resultFile = DefaultConstant.DEFAULT_RESULT_FILE;
	
//	private BufferedReader testReader = null;
	private BufferedWriter resultWriter = null;
	
//	public void setTrainFile(String trainFile) {
//		this.trainFile = trainFile;
//	}
//	
//	public void setTestFile(String testFile) {
//		this.testFile = testFile;
//	}
	
	public void setVectorFile(String vectorFile) {
		this.vectorFile = vectorFile;
	}
	
	public void setResultFile(String resultFile) {
		this.resultFile = resultFile;
	}
	
	public SentimentAnalyst() {
		//initTestReader();
		initResultWriter();
		if(resultWriter == null) {
			System.err.println("I/O initialization failed.");
		}
	}
	
//	private void initTestReader() {
//		File file = new File(testFile);
//		FileInputStream in = null;
//		try {
//			in = new FileInputStream(file);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.err.println("Test file is not found.");
//		}
//		
//		if(in != null) {
//			InputStreamReader reader = null;
//			try {
//				reader = new InputStreamReader(in, "UTF-8");
//			} catch (UnsupportedEncodingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			if(reader != null)
//				testReader = new BufferedReader(reader);
//		}
//	}
	
	private void initResultWriter() {
		File file = new File(resultFile);
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
				resultWriter = new BufferedWriter(writer);
		}
	}
	
	public void classify() {
		// read data
		Instances data = null;
		try {
			DataSource source = new DataSource(vectorFile);
			data = source.getDataSet();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Training file is not found or file format is not supported.");
		}
		if(data == null)
			return;
		if(data.classIndex() == -1)
			data.setClassIndex(data.numAttributes() - 1);
		
		// use filter to change class from numeric to nominal
		NumericToNominal attrFilter = new NumericToNominal();
		attrFilter.setAttributeIndices("last");
		try {
			attrFilter.setInputFormat(data);
			data = Filter.useFilter(data, attrFilter);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// use filter to split train & test file
		Instances trainData = null, testData = null;
		SubsetByExpression instanceFilter = new SubsetByExpression();
		
		instanceFilter.setExpression("not CLASS is '0'");
		try {
			instanceFilter.setInputFormat(data);
			trainData = Filter.useFilter(data, instanceFilter);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Filter Error.");
		}
		
		instanceFilter.setExpression("CLASS is '0'");
		try {
			instanceFilter.setInputFormat(data);
			testData = Filter.useFilter(data, instanceFilter);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Filter Error.");
		}
		if(trainData == null || testData == null)
			return;
		
		// build classifier
		LibSVM svm = new LibSVM();
		svm.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_LINEAR, LibSVM.TAGS_KERNELTYPE));
		try {
			svm.buildClassifier(trainData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Classifier building failed.");
		}
		
		// classification
		try {
			for(int i = 0; i < testData.numInstances(); i++) {
				Instance item = testData.instance(i);
				double classLabel = svm.classifyInstance(item);
				testData.instance(i).setClassValue(classLabel);
				
				resultWriter.write(String.valueOf(testData.classAttribute().value((int)classLabel)));
				resultWriter.newLine();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Classifying failed.");
		}
		
		closeResultWriter();
	}
	
	public void regression() {
		// read data
		Instances data = null;
		try {
			DataSource source = new DataSource(vectorFile);
			data = source.getDataSet();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Training file is not found or file format is not supported.");
		}
		if(data == null)
			return;
		if(data.classIndex() == -1)
			data.setClassIndex(data.numAttributes() - 1);
		
		// normalize data
		Normalize norm = new Normalize();
		norm.setScale(2.0);;
		norm.setTranslation(-1.0);
		norm.setIgnoreClass(true);
		try {
			norm.setInputFormat(data);
			data = Filter.useFilter(data, norm);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// use filter to split train & test file
		Instances trainData = null, testData = null;
		SubsetByExpression instanceFilter = new SubsetByExpression();
		
		instanceFilter.setExpression("not CLASS = 0");
		try {
			instanceFilter.setInputFormat(data);
			trainData = Filter.useFilter(data, instanceFilter);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Filter Error.");
		}
		
		instanceFilter.setExpression("CLASS = 0");
		try {
			instanceFilter.setInputFormat(data);
			testData = Filter.useFilter(data, instanceFilter);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Filter Error.");
		}
		if(trainData == null || testData == null)
			return;
		
		// build classifier
		LibSVM svm = new LibSVM();
		svm.setSVMType(new SelectedTag(LibSVM.SVMTYPE_EPSILON_SVR, LibSVM.TAGS_SVMTYPE));
		//svm.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_LINEAR, LibSVM.TAGS_KERNELTYPE));
		// This value is obtained by Parameter GridSearch
		svm.setGamma(100); // 0.1++
		//svm.setCost(0.1); // 0.1
		try {
			svm.buildClassifier(trainData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Classifier building failed.");
		}
		
		// regression. Here all data is re-tested.
		try {
			for(int i = 0; i < data.numInstances(); i++) {
				Instance item = data.instance(i);
				double classLabel = svm.classifyInstance(item);
				data.instance(i).setClassValue(classLabel);
				
				resultWriter.write(String.valueOf(classLabel));
				resultWriter.newLine();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Classifying failed.");
		}
		
		closeResultWriter();
	}
	
	public void closeResultWriter() {
		if(resultWriter != null) {
			try {
				resultWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				resultWriter = null;
			}
		}
	}
}
