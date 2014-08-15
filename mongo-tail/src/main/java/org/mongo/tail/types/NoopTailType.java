package org.mongo.tail.types;

import com.mongodb.DBObject;

public class NoopTailType extends AbstractGenericType {

	

	@Override
	protected void handleDeletes(DBObject op) {
		System.out.println(op);
	}

	@Override
	protected void handleInserts(DBObject op) {
		System.out.println(op);
	}

	@Override
	protected void handleUpdates(DBObject op) {
		if ("repl.time".equals((String) op.get("ns"))) {}
		else System.out.println(op);
	}
	

}
