/**
 * 
 */
package de.taimos.dao.mongo;

import java.math.BigDecimal;

import org.junit.Assert;

import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;

/**
 * Copyright 2015 Taimos GmbH<br>
 * <br>
 *
 * @author thoeger
 * 		
 */
public class ABaseTest {
	
	protected static final String dbName = "spring-dao-mongo";
	public static final MongoClient mongo = new Fongo("InMemory").getMongo();
	
	
	protected static void assertEquals(BigDecimal bd1, BigDecimal bd2) {
		Assert.assertEquals(bd1.doubleValue(), bd2.doubleValue(), 0);
	}
	
	/**
	 * 
	 */
	public ABaseTest() {
		super();
	}
	
}