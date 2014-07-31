package org.integration.connector;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.mongo.util.SardSetFinder;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;

public class ShardedReplicaSetConnectionTest {

   // @BeforeClass
   // public static void initMongoConnection() throws InterruptedException {
   // try {
   // Runtime.getRuntime().exec("cmd /c start init_sharded_env.bat");
   // Thread.sleep(60000);
   // }
   // catch (IOException e) {
   // e.printStackTrace();
   // }
   // }
   //
   // @AfterClass
   // public static void shutDownMongo() {
   // try {
   // Runtime.getRuntime().exec("taskkill /f /im mongod.exe");
   // }
   // catch (Exception e) {
   // e.printStackTrace();
   // }
   // }

   @Test
   public void connectToMongoSandGetConnectionInfo() throws UnknownHostException, InterruptedException {
      MongoClient mc = null;
      List<MongoClient> mcs = null;
      try {
         mc = new MongoClient("localhost", 27017);

         SardSetFinder shardFinder = new SardSetFinder();
         Map<String, List<ServerAddress>> shardSets = shardFinder.findShardSets(mc);
         mcs = new ArrayList<MongoClient>();
         
         for (Entry<String, List<ServerAddress>> host : shardSets.entrySet()) {
            MongoClientOptions opts = new MongoClientOptions.Builder().readPreference(ReadPreference.primary()).build();
            MongoClient keyClient = new MongoClient(host.getValue(), opts);
            mcs.add(keyClient);
         }
         Thread.sleep(100);
         for(MongoClient m : mcs) {
            System.out.println(m.getAddress());
         }
      }
      finally {
         mc.close();
         for (MongoClient m : mcs) {
            m.close();
         }
      }
   }
}
