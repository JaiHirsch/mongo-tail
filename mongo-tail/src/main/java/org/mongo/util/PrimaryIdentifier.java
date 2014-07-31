package org.mongo.util;

import com.mongodb.CommandResult;
import com.mongodb.DB;

public class PrimaryIdentifier {
   
   public boolean areYouThePrimary(DB adminDB) {
      CommandResult masterStatus = adminDB.command("isMaster");
      System.out.println(masterStatus);
      return (boolean) masterStatus.get("ismaster");
   }

}
