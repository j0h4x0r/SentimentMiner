package webapp;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
//import org.apache.lucene.queryparser.classic.ParseException;
//import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import preprocess.DatabaseAgent;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class SearchTopic {
	
	private String indexPath = SearchTopic.class.getResource("/").getPath() + "../indexFiles"; // for tomcat
	//private String indexPath = "E:/workspace/SentimentMiner/indexFiles"; // for debugging in eclipse
	
	public List<DBObject> searchIndex(String topic) {
		IndexReader reader = null;
		try {
			reader = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new SmartChineseAnalyzer(Version.LUCENE_48);
		
//		QueryParser parser = new QueryParser(Version.LUCENE_48, "content", analyzer);
//		Query query = null;
//		try {
//			query = parser.parse(topic);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		Query query = new TermQuery(new Term("content", topic));
		
		BooleanQuery query = new BooleanQuery();
		TokenStream ts = null;
		try {
			ts = analyzer.tokenStream("content", new StringReader(topic));
			ts.reset();
			CharTermAttribute chara = ts.addAttribute(CharTermAttribute.class);
			while(ts.incrementToken()) {
				String word = chara.toString();
				Query q = new TermQuery(new Term("content", word));
				query.add(q, BooleanClause.Occur.MUST);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			analyzer.close();
		}
		
		TopDocs results = null;
		BasicDBList tidList = new BasicDBList();
		try {
			results = searcher.search(query, reader.numDocs());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(ScoreDoc hit : results.scoreDocs) {
			Document doc = null;
			try {
				doc = searcher.doc(hit.doc);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tidList.add(Long.valueOf(doc.get("tid")).longValue());
		}
		
		MongoClient mongoClient = DatabaseAgent.getInstance().getMongoClient();
		DB db = mongoClient.getDB("SentimentMiner");
		
		DBCollection allData = db.getCollection("allData");
		BasicDBObject ref = new BasicDBObject("tid", new BasicDBObject("$in", tidList));
		BasicDBObject keys = new BasicDBObject("_id", 0);
		DBCursor cursor = allData.find(ref, keys);
		return cursor.toArray();
	}
}
