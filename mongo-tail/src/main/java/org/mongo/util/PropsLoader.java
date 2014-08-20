package org.mongo.util;

import java.util.Properties;

public class PropsLoader {

   public Properties loadMongoProperties() {
      Properties prop = new Properties();
      try {
         prop.load(getClass().getClassLoader().getResourceAsStream(
               "mongo-tail.properties"));
      } catch (Exception e) {
         e.printStackTrace();
      }
      return prop;
   }

}
