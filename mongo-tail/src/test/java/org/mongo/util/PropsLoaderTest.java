package org.mongo.util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

public class PropsLoaderTest {

   @Test
   public void testLoadProps() throws IOException {
      PropsLoader pl = new PropsLoader();
      Properties mongoProps = pl.loadMongoProperties();
      assertEquals("localhost:27017", mongoProps.get("mongosHostInfo"));
   }

}
