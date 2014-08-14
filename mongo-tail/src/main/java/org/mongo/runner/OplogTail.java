package org.mongo.runner;

import java.util.Map.Entry;

import org.bson.types.BSONTimestamp;
import org.mongo.tail.TailType;

import com.mongodb.*;

public class OplogTail implements Runnable {

	private MongoClient client = null;
	private BSONTimestamp lastTimeStamp = null;
	private DBCollection shardTimeCollection = null;
	private final TailType tailer;

	public OplogTail(Entry<String, MongoClient> client, DB timeDB,
			TailType tailer) {
		this.tailer = tailer;
		this.client = client.getValue();
		shardTimeCollection = timeDB.getCollection(client.getKey());
		DBObject findOne = shardTimeCollection.findOne();
		if (findOne != null) {
			lastTimeStamp = (BSONTimestamp) findOne.get("ts");
		}
	}

	@Override
	public void run() {
		DBCollection fromCollection = client.getDB("local").getCollection(
				"oplog.rs");
		DBObject timeQuery = getTimeQuery();
		System.out.println("Start timestamp: " + timeQuery);
		DBCursor opCursor = fromCollection.find(timeQuery)
				.sort(new BasicDBObject("$natural", 1))
				.addOption(Bytes.QUERYOPTION_TAILABLE)
				.addOption(Bytes.QUERYOPTION_AWAITDATA)
				.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
		try {
			while (true) {
				if (!opCursor.hasNext()) {
					continue;
				} else {
					DBObject nextOp = opCursor.next();
					lastTimeStamp = ((BSONTimestamp) nextOp.get("ts"));
					shardTimeCollection.update(new BasicDBObject(),
							new BasicDBObject("$set", new BasicDBObject("ts",
									lastTimeStamp)), true, true,
							WriteConcern.SAFE);
					tailer.tailOp(nextOp);
				}
			}
		} finally {
			System.out.println("good bye");
		}
	}

	private DBObject getTimeQuery() {
		return lastTimeStamp == null ? new BasicDBObject() : new BasicDBObject(
				"ts", new BasicDBObject("$gt", lastTimeStamp));
	}

}
