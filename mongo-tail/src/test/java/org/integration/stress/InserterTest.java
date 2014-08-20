package org.integration.stress;

import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.mongo.util.PropsLoader;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class InserterTest {

   @Test
   public void loadRandomStuff() throws UnknownHostException {
      Properties p = new PropsLoader().loadMongoProperties();
      MongoClient mc = new MongoClient(p.getProperty("mongosHostInfo"));
      DBCollection collection = mc.getDB("test").getCollection("test_data");

      for (int i = 0; i < 500; i++)
         collection.insert(new BasicDBObject("test_data", RandomStringUtils
               .randomAlphanumeric(24)));
   }

}
