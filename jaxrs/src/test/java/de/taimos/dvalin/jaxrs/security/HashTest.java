/*
 * Copyright (c) 2016. Taimos GmbH http://www.taimos.de
 */

package de.taimos.dvalin.jaxrs.security;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Stopwatch;

public class HashTest {

	@Test
	public void testHash() {
		String password = "test1234";
		HashedPassword hash = new HashedPassword(password);
		System.out.println(hash.getHash());
		System.out.println(hash.getRoundOffset());
		System.out.println(hash.getSalt());

		Assert.assertTrue(hash.validate(password));

		Assert.assertTrue(new HashedPassword(hash.getRoundOffset(), hash.getHash(), hash.getSalt()).validate(password));
	}

	@Test
	public void stopwatch() {
		String password = "test1234test1234";
		HashedPassword hns = new HashedPassword(password);
		System.out.println(hns.getSalt());
		System.out.println(hns.getRoundOffset());
		System.out.println(hns.getHash());
		Stopwatch w = Stopwatch.createUnstarted();
		w.start();
		boolean validate = hns.validate(password);
		w.stop();
		System.out.println(w.toString());
		Assert.assertTrue(validate);
	}

}
