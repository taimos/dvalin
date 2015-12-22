package de.taimos.springcxfdaemon.test;

/*
 * #%L
 * Daemon with Spring and CXF
 * %%
 * Copyright (C) 2013 - 2015 Taimos GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;

/**
 * Assert tooling for asynchronous callbacks
 *
 * @author thoeger
 */
public class AsyncAssert {
	
	private CountDownLatch cdl;
	private AssertionError ae = null;
	
	
	/**
	 * Init with a count of 1
	 */
	public AsyncAssert() {
		this(1);
	}
	
	/**
	 * Init
	 * 
	 * @param count the number of success calls are needed to pass
	 */
	public AsyncAssert(int count) {
		this.cdl = new CountDownLatch(count);
	}
	
	public void assertTrue(String message, boolean condition) {
		if (!condition) {
			this.fail(message);
		}
	}
	
	public void assertTrue(boolean condition) {
		this.assertTrue("Value was expected to be true", condition);
	}
	
	public void assertFalse(String message, boolean condition) {
		this.assertTrue(message, !condition);
	}
	
	public void assertFalse(boolean condition) {
		this.assertFalse("Value was expected to be false", condition);
	}
	
	public void assertNotNull(Object o) {
		this.assertNotNull("Value was expected not to be null", o);
	}
	
	public void assertNotNull(String message, Object o) {
		this.assertTrue(message, o != null);
	}
	
	public void assertEquals(Object expected, Object actual) {
		this.assertEquals("Value was expected to be " + expected + " but was " + actual, expected, actual);
	}
	
	public void assertEquals(String message, Object expected, Object actual) {
		this.assertTrue(message, ((expected == null) && (actual == null)) || ((expected != null) && expected.equals(actual)));
	}
	
	public void assertSame(BigDecimal expected, BigDecimal actual) {
		this.assertEquals("Value was expected to be " + expected + " but was " + actual, expected, actual);
	}
	
	public void assertSame(String message, BigDecimal expected, BigDecimal actual) {
		this.assertTrue(message, ((expected == null) && (actual == null)) || ((expected != null) && (expected.compareTo(actual) == 0)));
	}
	
	public void fail() {
		this.fail(null);
	}
	
	public void fail(String message) {
		if (message == null) {
			this.ae = new AssertionError();
		}
		this.ae = new AssertionError(message);
		this.cdl.countDown();
		throw this.ae;
	}
	
	public void success() {
		this.cdl.countDown();
	}
	
	public void awaitOneSecond() {
		this.await(1, TimeUnit.SECONDS);
	}
	
	public void await(long timeout, TimeUnit unit) {
		try {
			if (this.cdl.await(timeout, unit)) {
				if (this.ae != null) {
					throw this.ae;
				}
				return;
			}
			Assert.fail("Timeout reached");
		} catch (InterruptedException e) {
			Assert.fail(e.getMessage());
		}
	}
	
}
