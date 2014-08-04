package org.mongo.runner;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.mongo.util.PropsLoader;
import org.mongo.util.ShardSetFinder;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class ShardedReplicaTailer {

   // MongoClient mc = null;
   // List<MongoClient> mcs = null;
   // try {
   // mc = new MongoClient("localhost", 27017);
   //
   // SardSetFinder shardFinder = new SardSetFinder();
   // Map<String, List<ServerAddress>> shardSets = shardFinder.findShardSets(mc);
   // mcs = new ArrayList<MongoClient>();
   //
   // for (Entry<String, List<ServerAddress>> host : shardSets.entrySet()) {
   // MongoClientOptions opts = new MongoClientOptions.Builder().readPreference(ReadPreference.primary()).build();
   // MongoClient keyClient = new MongoClient(host.getValue(), opts);
   // mcs.add(keyClient);
   // }
   // Thread.sleep(100);
   // for(MongoClient m : mcs) {
   // System.out.println(m.getAddress());
   // }
   // }
   // finally {
   // mc.close();
   // for (MongoClient m : mcs) {
   // m.close();
   // }
   // }
   private static MongoClient hostMongoS = null;
   private static MongoClient timeClient;
   private static Map<String, MongoClient> shardSets;

   public static void main(String[] args) throws UnknownHostException {

      try {
         addShutdownHookToMainThread();
         Properties mongoSInfo = loadProperties();
         hostMongoS = new MongoClient(mongoSInfo.getProperty("mongosHostInfo"));
         timeClient = new MongoClient(mongoSInfo.getProperty(("mongoReplTimeHostInfo")));
         DB timeDB = timeClient.getDB("time_d");
         ShardSetFinder finder = new ShardSetFinder();
         shardSets = finder.findShardSets(hostMongoS);

         ExecutorService executor = Executors.newFixedThreadPool(shardSets.size());
         for (Entry<String, MongoClient> client : shardSets.entrySet()) {
            Runnable worker = new OplogTail(client, timeDB);
            executor.execute(worker);
         }
         executor.shutdown();
         while (true)
            ;
      }
      finally {
         if (hostMongoS != null) hostMongoS.close();
         if (timeClient != null) timeClient.close();
         for (MongoClient repClient : shardSets.values()) {
            repClient.close();
         }
      }

   }

   private static Properties loadProperties() {
      PropsLoader propsLoader = new PropsLoader();
      return propsLoader.loadMongoProperties();
   }

   public static void addShutdownHookToMainThread() {
      final Thread mainThread = Thread.currentThread();
      Runtime.getRuntime().addShutdownHook(new Thread() {
         public void run() {
            if (hostMongoS != null) {
               hostMongoS.close();
            }
            if (timeClient != null) {
               timeClient.close();
            }
            if (shardSets != null) {
               for (MongoClient repClient : shardSets.values()) {
                  repClient.close();
               }
            }
            try {
               mainThread.join();
            }
            catch (InterruptedException e) {
               System.out.println("---------------- Unable to join main thread, attempting to shutdown MongoDB connections gracefully. --------------");
               if (hostMongoS != null) hostMongoS.close();
               if (timeClient != null) timeClient.close();
               if (shardSets != null) {
                  for (MongoClient repClient : shardSets.values()) {
                     repClient.close();
                  }
               }
               throw new RuntimeException(e);
            }
         }
      });
   }

}
