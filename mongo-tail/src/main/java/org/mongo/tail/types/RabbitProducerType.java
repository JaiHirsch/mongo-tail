package org.mongo.tail.types;

import java.io.IOException;

import com.mongodb.DBObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitProducerType extends AbstractGenericType {

   private static final String EXCHANGE_NAME = "mongo-tail";
   private Connection connection;
   private Channel channel;

   public RabbitProducerType() {
      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("localhost");
      try {
         connection = factory.newConnection();
         channel = connection.createChannel();
         channel.exchangeDeclare(EXCHANGE_NAME, "direct");
      } catch (IOException e) {
         e.printStackTrace();
      }

   }

   public void publishMessage(DBObject op, String routingKey) {
      try {
         String message = op.toString();
         channel.basicPublish(EXCHANGE_NAME, routingKey, null,
               message.getBytes());
         System.out.println(" [x] Sent '" + message + "'");

      } catch (IOException e) {
         e.printStackTrace();
      }

   }

   @Override
   protected void handleDeletes(DBObject op) {
      publishMessage(op, "d");
   }

   @Override
   protected void handleInserts(DBObject op) {
      publishMessage(op, "i");
   }

   @Override
   protected void handleUpdates(DBObject op) {
      publishMessage(op, "u");

   }

   @Override
   public void close() {
      try {
         channel.close();
         connection.close();
      } catch (IOException e) {
         e.printStackTrace();
      }

   }

}
