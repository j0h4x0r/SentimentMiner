package preprocess;

import java.net.UnknownHostException;

import com.mongodb.MongoClient;

public class DatabaseAgent {
	
	private static DatabaseAgent instance = null;
	private MongoClient mongoClient;
	
	private DatabaseAgent() {
		
	}
	
	public static DatabaseAgent getInstance() {
		if(instance == null) {
			instance = new DatabaseAgent();
			instance.initDatabase();
		}
		return instance;
	}
	
	private void initDatabase() {
		try {
			mongoClient = new MongoClient();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public MongoClient getMongoClient() {
		return mongoClient;
	}
	
}
