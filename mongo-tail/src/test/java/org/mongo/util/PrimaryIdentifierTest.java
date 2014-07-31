package org.mongo.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.mongodb.CommandResult;
import com.mongodb.DB;

public class PrimaryIdentifierTest {

   private PrimaryIdentifier pider;

   @Before
   public void setUp() {
      pider = new PrimaryIdentifier();
   }

   @Test
   public void youAreNotThePrimaryTest() {
      DB mockDB = setupMockDB(false);
      assertFalse("expected false but was "+pider.areYouThePrimary(mockDB),pider.areYouThePrimary(mockDB));
   }
   @Test
   public void youAreThePrimaryTest() {
      DB mockDB = setupMockDB(true);
      assertTrue("expected true but was "+pider.areYouThePrimary(mockDB),pider.areYouThePrimary(mockDB));
   }

   private DB setupMockDB(boolean isMaster) {
      DB mockDB = mock(DB.class);
      CommandResult result = mock(CommandResult.class);
      when(result.get("ismaster")).thenReturn(isMaster);
      when(mockDB.command("isMaster")).thenReturn(result);
      return mockDB;
   }

}
