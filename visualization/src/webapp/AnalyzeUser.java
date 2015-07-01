package webapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import preprocess.DatabaseAgent;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class AnalyzeUser {
	
	public List<DBObject> analyze(long uid) {
		MongoClient mongoClient = DatabaseAgent.getInstance().getMongoClient();
		DB db = mongoClient.getDB("SentimentMiner");
		
		DBCollection allData = db.getCollection("allData");
		BasicDBObject ref = new BasicDBObject("uid", uid);
		BasicDBObject keys = new BasicDBObject("_id", 0)
									.append("date", 1)
									.append("polar", 1);
		DBCursor cursor = allData.find(ref, keys);
		
		Map<String, DBObject> emoMap = new HashMap<String, DBObject>();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM Z");
		Calendar dateIt = null, dateEnd = null;
		try {
			dateIt = Calendar.getInstance();
			dateIt.setTime(sdf.parse("2009-01 +0800"));
			dateEnd = Calendar.getInstance();
			dateEnd.setTime(sdf.parse("2013-01 +0800"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(; dateIt.before(dateEnd); dateIt.add(Calendar.MONTH, 1)) {
			BasicDBObject obj = new BasicDBObject();
			obj.append("date", dateIt.getTime());
			obj.append("pos", 0);
			obj.append("posNum", 0);
			obj.append("neg", 0);
			obj.append("negNum", 0);
			String dateStr = sdf.format(dateIt.getTime()).substring(0, 7);
			emoMap.put(dateStr, obj);
		}
		
		while(cursor.hasNext()) {
			DBObject obj = cursor.next();
			Date date = (Date)obj.get("date");
			int polar = (Integer)obj.get("polar");
			String dateStr = sdf.format(date).substring(0, 7);
			String polarKey = polar > 0 ? "pos" : "neg";
			int p = (Integer)emoMap.get(dateStr).get(polarKey);
			int num = (Integer)emoMap.get(dateStr).get(polarKey + "Num");
			emoMap.get(dateStr).put(polarKey, p + polar);
			emoMap.get(dateStr).put(polarKey + "Num", num + 1);
		}
		
		ArrayList<DBObject> result = new ArrayList<DBObject>(emoMap.values());
		Collections.sort(result, new Comparator<DBObject>() {

			@Override
			public int compare(DBObject o1, DBObject o2) {
				// TODO Auto-generated method stub
				boolean flag = ((Date)o1.get("date")).after((Date)o2.get("date"));
				return flag ? 1 : -1;
			}
			
		});
		Iterator<DBObject> iter = result.iterator();
		while(iter.hasNext()) {
			DBObject obj = iter.next();
			int posNum = (Integer)obj.get("posNum");
			int negNum = (Integer)obj.get("negNum");
			if(posNum != 0 || negNum != 0)
				break;
			iter.remove();
		}
		ListIterator<DBObject> riter = result.listIterator(result.size());
		while(riter.hasPrevious()) {
			DBObject obj = riter.previous();
			int posNum = (Integer)obj.get("posNum");
			int negNum = (Integer)obj.get("negNum");
			if(posNum != 0 || negNum != 0)
				break;
			riter.remove();
		}
		
		return result;
	}
}
