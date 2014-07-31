package org.mongo.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Iterables;
import com.mongodb.*;

public class ShardSetFinderTest {

   @Test
   public void findReplicaSetsForShards() {
      MongoClient mc = mock(MongoClient.class);
      DB adminDB = mock(DB.class);
      DB configDB = mock(DB.class);
      DBCollection shardCol = mock(DBCollection.class);
      DBCursor replCur = mock(DBCursor.class);

      when(mc.getDB("admin")).thenReturn(adminDB);
      when(adminDB.getSisterDB("config")).thenReturn(configDB);
      when(configDB.getCollection("shards")).thenReturn(shardCol);
      when(shardCol.find()).thenReturn(replCur);
      when(replCur.next()).thenReturn(new BasicDBObject("_id", "s0").append("host",
                                                                            "s0/localhost:37017,localhost:37018,localhost:37019"),
                                      new BasicDBObject("_id", "s1").append("host",
                                                                            "s1/localhost:47017,localhost:47018,localhost:47019"),
                                      new BasicDBObject("_id", "s2").append("host",
                                                                            "s2/localhost:57017,localhost:57018,localhost:57019"));
      when(replCur.hasNext()).thenReturn(true, true, true, false);

      SardSetFinder finder = new SardSetFinder();
      Map<String, List<String>> expected = new HashMap<String, List<String>>();
      expected.put("s0", Arrays.asList(new String[] { "localhost:37017", "localhost:37018", "localhost:37019" }));
      expected.put("s1", Arrays.asList(new String[] { "localhost:47017", "localhost:47018", "localhost:47019" }));
      expected.put("s2", Arrays.asList(new String[] { "localhost:57017", "localhost:57018", "localhost:57019" }));

      Map<String, List<ServerAddress>> actual = finder.findShardSets(mc);
      System.out.println(expected);
      assertTrue(Iterables.elementsEqual(expected.entrySet(), actual.entrySet()));
      assertTrue(Iterables.elementsEqual(expected.keySet(), actual.keySet()));
   }
}
