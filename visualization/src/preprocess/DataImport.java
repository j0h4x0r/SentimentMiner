package preprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class DataImport {
	
	private MongoClient mongoClient;
	private String polarFile = "polar.txt";
	private String dataFile = "data.txt";
	private BufferedReader polarReader = null;
	private BufferedReader dataReader = null;
	
	public DataImport() {
		initFileReader();
		mongoClient = DatabaseAgent.getInstance().getMongoClient();
	}
	
	private void initFileReader() {
		File pFile = new File(polarFile);
		File dFile = new File(dataFile);
		FileInputStream pIn = null;
		FileInputStream dIn = null;
		try {
			pIn = new FileInputStream(pFile);
			dIn = new FileInputStream(dFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("DataFile is not found.");
		}
		
		if(pIn != null) {
			InputStreamReader pReader = null;
			try {
				pReader = new InputStreamReader(pIn, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(pReader != null)
				polarReader = new BufferedReader(pReader);
		}
		
		if(dIn != null) {
			InputStreamReader dReader = null;
			try {
				dReader = new InputStreamReader(dIn, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(dReader != null)
				dataReader = new BufferedReader(dReader);
		}
	}
	
	public void closeFileReader() {
		if(polarReader != null) {
			try {
				polarReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				polarReader = null;
			}
		}
		if(dataReader != null) {
			try {
				dataReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				dataReader = null;
			}
		}
	}
	
	public void buildDatabase() {
		mongoClient.dropDatabase("SentimentMiner");
		DB db = mongoClient.getDB("SentimentMiner");
		
		DBCollection allData = db.getCollection("allData");
		String line;
		int polar = 0;
		try {
			while((line = dataReader.readLine()) != null) {
				try {
					polar = Integer.valueOf(polarReader.readLine());
				} catch (NumberFormatException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String[] parts = line.split("\t");
				long tid = Double.valueOf(parts[0]).longValue();
				long uid = Double.valueOf(parts[1]).longValue();
				SimpleDateFormat formater = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy", Locale.US);
				Date date = null;
				try {
					date = formater.parse(parts[2]);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String content = parts[3];
				BasicDBObject doc = new BasicDBObject("tid", tid)
						.append("uid", uid)
						.append("date", date)
						.append("content", content)
						.append("polar", polar);
				allData.insert(doc);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		closeFileReader();
	}
}
