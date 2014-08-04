package org.mongo.util;

import java.net.UnknownHostException;
import java.util.*;

import com.mongodb.*;

public class ShardSetFinder {

   public Map<String, MongoClient> findShardSets(MongoClient mc) {

      DBCursor find = mc.getDB("admin").getSisterDB("config").getCollection("shards").find();
      Map<String, MongoClient> shardSets = new HashMap<String, MongoClient>();
      while (find.hasNext()) {
         DBObject next = find.next();
System.out.println(next);
         String key = (String) next.get("_id");
         shardSets.put(key, getMongoClient(buildServerAddressList(next)));
      }
      find.close();
      return shardSets;
   }

   public MongoClient getMongoClient(List<ServerAddress> shardSet) {
      MongoClient ShardSetClient = null;
      try {
         MongoClientOptions opts = new MongoClientOptions.Builder().readPreference(ReadPreference.primary()).build();
         ShardSetClient = new MongoClient(shardSet, opts);
         Thread.sleep(100);  // allow the client to establish prior to being returned for use
      }
      catch (InterruptedException e) {
         e.printStackTrace();
      }
      return ShardSetClient;
   }

   private List<ServerAddress> buildServerAddressList(DBObject next) {
      List<ServerAddress> hosts = new ArrayList<ServerAddress>();
      for (String host : Arrays.asList(((String) next.get("host")).split("/")[1].split(","))) {
         try {
            hosts.add(new ServerAddress(host));
         }
         catch (UnknownHostException e) {
            e.printStackTrace();
         }
      }
      return hosts;
   }
}
