package org.integration.connector;

import org.junit.Test;
import org.mongo.tail.types.RabbitProducerType;

import com.mongodb.BasicDBObject;

public class RabbitProducerTypeTest {

   @Test
   public void test() {
      RabbitProducerType type = new RabbitProducerType();
      type.tailOp(new BasicDBObject("name", "Bob"));
   }

}
