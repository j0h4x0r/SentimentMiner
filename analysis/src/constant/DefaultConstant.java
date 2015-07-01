package constant;

public class DefaultConstant {
	
	public static String DEFAULT_DATA_FILE = "data.txt";
	public static String DEFAULT_TRAIN_FILE = "train.txt";
	public static String DEFAULT_TEST_FILE = "test.txt";
	public static String DEFAULT_RESULT_FILE = "result.txt";
	
	public static String DEFAULT_FILES_DIR = "./DataFiles";
	public static String DEFAULT_VECTOR_FILENAME = "vector.txt";
	public static String DEFAULT_VECTOR_FILE = DefaultConstant.DEFAULT_FILES_DIR + "/" + DefaultConstant.DEFAULT_VECTOR_FILENAME;
	public static String DEFAULT_POLAR_FILENAME = "polar.txt";
	public static String DEFAULT_POLAR_FILE = DefaultConstant.DEFAULT_FILES_DIR + "/" + DefaultConstant.DEFAULT_POLAR_FILENAME;
	public static String DEFAULT_TVECTOR_FILENAME = "tvector.libsvm";
	public static String DEFAULT_TVECTOR_FILE = DefaultConstant.DEFAULT_FILES_DIR + "/" + DefaultConstant.DEFAULT_TVECTOR_FILENAME;
//	public static String DEFAULT_RESULT_FILENAME = "result.txt";
//	public static String DEFAULT_RESULT_FILE = DefaultConstant.DEFAULT_FILES_DIR + "/" + DefaultConstant.DEFAULT_RESULT_FILENAME;
	
	public static String DEFAULT_LDA_DIR = "./DataFiles/LDA";
	public static String DEFAULT_LDA_VECTOR_FILE = DefaultConstant.DEFAULT_LDA_DIR + "/" + DefaultConstant.DEFAULT_VECTOR_FILENAME;
	public static String DEFAULT_LDA_THETA_FILE = "DataFiles/LDA/model-final.theta";
	
	public static String DEFAULT_POSITIVE_DIC = "DataFiles/dictionary/positive.txt";
	public static String DEFAULT_NEGATIVE_DIC = "DataFiles/dictionary/negative.txt";
	
}
