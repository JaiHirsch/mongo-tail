package org.mongo.runner;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.mongo.tail.TailType;
import org.mongo.tail.TailTypeInjector;
import org.mongo.util.PropsLoader;
import org.mongo.util.ShardSetFinder;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class ShardedReplicaTailer {

	private static MongoClient hostMongoS = null;
	private static MongoClient timeClient;
	private static Map<String, MongoClient> shardSetClients;
	private static DB timeDB;

	public static void main(String[] args) throws UnknownHostException {
		String tailType = "";
		if (args.length > 0) {
			tailType = args[0];
		}
		try {
			addShutdownHookToMainThread();
			establishMongoDBConnections();
			runTailingThreads(tailType);
			while (true)
				;
		} finally {
			closeMongoConnections();
		}

	}

	private static void establishMongoDBConnections()
			throws UnknownHostException {
		Properties mongoConnectionProperties = loadProperties();
		hostMongoS = new MongoClient(
				mongoConnectionProperties.getProperty("mongosHostInfo"));
		timeClient = new MongoClient(
				mongoConnectionProperties
						.getProperty(("mongoReplTimeHostInfo")));
		timeDB = timeClient.getDB("time_d");
		shardSetClients = new ShardSetFinder().findShardSets(hostMongoS);
	}

	private static void runTailingThreads(String tailType) {
		ExecutorService executor = Executors.newFixedThreadPool(shardSetClients
				.size());
		for (Entry<String, MongoClient> client : shardSetClients.entrySet()) {
			Runnable worker = new OplogTail(client, timeDB, getOpType(tailType));
			executor.execute(worker);
		}
		executor.shutdown();
	}

	private static TailType getOpType(String tailType) {
		return new TailTypeInjector().getTailTypeFromArgs(tailType);
	}

	private static void closeMongoConnections() {
		if (hostMongoS != null)
			hostMongoS.close();
		if (timeClient != null)
			timeClient.close();
		for (MongoClient repClient : shardSetClients.values()) {
			repClient.close();
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
				System.out
						.println("Closing MongoDB connections through shutdown hook");
				if (hostMongoS != null) {
					hostMongoS.close();
				}
				if (timeClient != null) {
					timeClient.close();
				}
				if (shardSetClients != null) {
					for (MongoClient repClient : shardSetClients.values()) {
						repClient.close();
					}
				}
				try {
					mainThread.join();
				} catch (InterruptedException e) {
					System.out
							.println("---------------- Unable to join main thread, attempting to shutdown MongoDB connections gracefully. --------------");
					if (hostMongoS != null)
						hostMongoS.close();
					if (timeClient != null)
						timeClient.close();
					if (shardSetClients != null) {
						for (MongoClient repClient : shardSetClients.values()) {
							repClient.close();
						}
					}
					throw new RuntimeException(e);
				}
			}
		});
	}

}
