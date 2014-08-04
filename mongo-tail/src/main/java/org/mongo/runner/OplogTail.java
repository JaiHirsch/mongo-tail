package org.mongo.runner;

import java.util.Map.Entry;

import org.bson.types.BSONTimestamp;

import com.mongodb.*;

public class OplogTail implements Runnable {

   private MongoClient client;
   private BSONTimestamp lastTimeStamp = null;
   private DBCollection shardTimeCollection;

   public OplogTail(Entry<String, MongoClient> client, DB timeDB) {
      this.client = client.getValue();
      shardTimeCollection = timeDB.getCollection(client.getKey());
   }

   @Override
   public void run() {
      DBCollection fromCollection = client.getDB("local").getCollection("oplog.rs");
      DBCursor opCursor = fromCollection.find(getTimeQuery()).sort(new BasicDBObject("$natural", 1))
                                        .addOption(Bytes.QUERYOPTION_TAILABLE).addOption(Bytes.QUERYOPTION_AWAITDATA)
                                        .addOption(Bytes.QUERYOPTION_NOTIMEOUT);
      while (true) {
         if (!opCursor.hasNext())
            continue;
         else {
            DBObject nextOp = opCursor.next();
            lastTimeStamp = ((BSONTimestamp) nextOp.get("ts"));
            shardTimeCollection.update(new BasicDBObject(),
                                       new BasicDBObject("$set", new BasicDBObject("timestamp", lastTimeStamp)), true,
                                       true, WriteConcern.SAFE);
            switch ((String) nextOp.get("op")) {
               case "u":
                  if ("repl.time".equals((String) nextOp.get("ns"))) continue;
            }
            System.out.println(nextOp);
         }
      }

   }

   private DBObject getTimeQuery() {
      return lastTimeStamp == null ? getTimeFromDB() : new BasicDBObject("ts", new BasicDBObject("$gt", lastTimeStamp));
   }

   private DBObject getTimeFromDB() {
      return shardTimeCollection.findOne();
   }

}
