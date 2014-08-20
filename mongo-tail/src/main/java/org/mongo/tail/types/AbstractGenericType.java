package org.mongo.tail.types;

import org.mongo.tail.TailType;

import com.mongodb.DBObject;

public abstract class AbstractGenericType implements TailType {

   @Override
   public void tailOp(DBObject op) {
      switch ((String) op.get("op")) {
         case "u":
            if ("repl.time".equals((String) op.get("ns"))) {
            } else
               handleUpdates(op);
            break;
         case "i":
            handleInserts(op);
            break;
         case "d":
            handleDeletes(op);
            break;
         default:
            handleOtherOps(op);
            break;
      }

   }

   protected void handleOtherOps(DBObject op) {
      System.out.println("Non-handled operation: " + op);

   }

   protected abstract void handleDeletes(DBObject op);

   protected abstract void handleInserts(DBObject op);

   protected abstract void handleUpdates(DBObject op);

   public void close() {
   }
}
