package org.mongo.tail;

import java.util.LinkedList;
import java.util.List;

import org.mongo.tail.types.NoopTailType;

public class TailTypeInjector {

	public List<TailType> getTailTypeFromArgs(String... types) {
		List<TailType> tailers = new LinkedList<TailType>();
		TailType tailType = null;
		for (String type : types) {
			if ("".equals(type)) {
				System.out.println("------------- WARNING -----------------");
				System.out
						.println("---- No Tail Type passed as argument, using default NoOpTailType ----");
				tailType = new NoopTailType();
			} else {
				Class<?> clazz;
				try {
					clazz = Class.forName("org.mongo.tail.types." + type);
					tailType = (TailType) clazz.newInstance();
					tailers.add(tailType);
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException e) {
					e.printStackTrace();
					throw new RuntimeException(
							"Unknown tail type passed to TailTypeInjector.  You must pass the name of the TailType class.");
				}
			}
		}
		return tailers;
	}

}
