package preprocess;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class IndexCreator {
	
	private String indexPath = "./indexFiles";
	
	public static void main(String[] args) {
		DataImport dataImport = new DataImport();
		dataImport.buildDatabase();
		IndexCreator indexCreator = new IndexCreator();
		indexCreator.buildIndex();
	}
	
	public void buildIndex() {
		Directory dir = null;
		try {
			dir = FSDirectory.open(new File(indexPath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Analyzer analyzer = new SmartChineseAnalyzer(Version.LUCENE_48);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_48, analyzer);
		iwc.setOpenMode(OpenMode.CREATE);
		
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(dir, iwc);
			indexDocs(writer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private void indexDocs(IndexWriter writer) throws IOException {
		MongoClient mongoClient = DatabaseAgent.getInstance().getMongoClient();
		DB db = mongoClient.getDB("SentimentMiner");
		
		DBCollection allData = db.getCollection("allData");
		DBCursor cursor = allData.find();
		while(cursor.hasNext()) {
			DBObject obj = cursor.next();
			Document doc = new Document();
			doc.add(new LongField("tid", ((Long)(obj.get("tid"))).longValue(), Field.Store.YES));
			doc.add(new TextField("content", (String)(obj.get("content")), Field.Store.NO));
			writer.addDocument(doc);
		}
	}
}
