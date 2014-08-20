package org.mongo.tail;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.mongo.tail.types.NoopTailType;

public class TailTypeInjectorTest {

   @Test
   public void testNoopType() {
      TailTypeInjector inj = new TailTypeInjector();

      List<TailType> tailTypeFromArgs = inj.getTailTypeFromArgs("NoopTailType");
      assertTrue(tailTypeFromArgs.get(0) instanceof NoopTailType);
   }

}
