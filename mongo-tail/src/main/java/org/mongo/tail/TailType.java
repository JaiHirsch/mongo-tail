package org.mongo.tail;

import com.mongodb.DBObject;

public interface TailType {

	public void tailOp(DBObject op);
	
	public void close();
}
