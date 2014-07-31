package org.integration.connector;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.*;

public class ReplicaSetOnlyConnectionTest {

//   @BeforeClass
//   public static void initMongoConnection() throws InterruptedException {
//      try {
//         Runtime.getRuntime().exec("cmd /c start init_replica_env.bat");
//         Thread.sleep(30000);
//      }
//      catch (IOException e) {
//         e.printStackTrace();
//      }
//   }
//
//   @AfterClass
//   public static void shutDownMongo() {
//      try {
//         Runtime.getRuntime().exec("taskkill /f /im mongod.exe");
//      }
//      catch (Exception e) {
//         e.printStackTrace();
//      }
//   }

   @Test
   public void test() throws UnknownHostException {
      MongoClient mc = new MongoClient("localhost:37017");
      DB db = mc.getDB("admin");
      CommandResult command = db.command(new BasicDBObject("replSetGetStatus",1));
      System.out.println(mc.getDatabaseNames());
      System.out.println(mc.getAllAddress());
      System.out.println(mc.getReplicaSetStatus());
      System.out.println(command);
      BasicDBList members = (BasicDBList) command.get("members");
      for(Object member : members) {
         DBObject replicaSet = (DBObject)member;
         System.out.println(replicaSet.get("name")+" "+replicaSet.get("stateStr"));
      }
      mc.close();
   }

}
