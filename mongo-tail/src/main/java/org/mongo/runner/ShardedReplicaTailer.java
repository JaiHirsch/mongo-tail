package org.mongo.runner;

   import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.mongo.util.SardSetFinder;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;

public class ShardedReplicaTailer {

   
   
   
//   MongoClient mc = null;
//   List<MongoClient> mcs = null;
//   try {
//      mc = new MongoClient("localhost", 27017);
//
//      SardSetFinder shardFinder = new SardSetFinder();
//      Map<String, List<ServerAddress>> shardSets = shardFinder.findShardSets(mc);
//      mcs = new ArrayList<MongoClient>();
//      
//      for (Entry<String, List<ServerAddress>> host : shardSets.entrySet()) {
//         MongoClientOptions opts = new MongoClientOptions.Builder().readPreference(ReadPreference.primary()).build();
//         MongoClient keyClient = new MongoClient(host.getValue(), opts);
//         mcs.add(keyClient);
//      }
//      Thread.sleep(100);
//      for(MongoClient m : mcs) {
//         System.out.println(m.getAddress());
//      }
//   }
//   finally {
//      mc.close();
//      for (MongoClient m : mcs) {
//         m.close();
//      }
//   }
      public static void main(String[] args) throws UnknownHostException {



         
         ExecutorService executor = Executors.newFixedThreadPool(shardSet.size());
         
         for(Entry<String, List<String>> e : shardSet) {
            Runnable worker = new OplogTail(e.getKey(), e.getValue());
            executor.execute(worker);
         }
         
         executor.shutdown();
         System.out.println("All threads have finished");
      }

}
