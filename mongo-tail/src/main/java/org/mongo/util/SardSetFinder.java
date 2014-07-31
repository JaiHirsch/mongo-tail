package org.mongo.util;

import java.net.UnknownHostException;
import java.util.*;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

public class SardSetFinder {

   public Map<String, List<ServerAddress>> findShardSets(MongoClient mc) {
      
      DBCursor find = mc.getDB("admin").getSisterDB("config").getCollection("shards").find();
      Map<String, List<ServerAddress>> shardSets = new HashMap<String, List<ServerAddress>>();
      while (find.hasNext()) {
         DBObject next = find.next();
         System.out.println(next);
         String key = (String) next.get("_id");
         shardSets.put(key, buildServerAddressList(next));
      }
      find.close();
      return shardSets;
   }

   private List<ServerAddress> buildServerAddressList(DBObject next) {
      List<ServerAddress> hosts = new ArrayList<ServerAddress>();
      for(String host : Arrays.asList(((String) next.get("host")).split("/")[1].split(","))) {
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
