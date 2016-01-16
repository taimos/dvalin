package de.taimos.dvalin.interconnect.model.common.daemon;

import org.junit.Assert;
import org.junit.Test;

import de.taimos.dvalin.interconnect.model.service.ADaemonErrorNumber;
import de.taimos.dvalin.interconnect.model.service.DaemonErrorNumber;

@SuppressWarnings("javadoc")
public final class ADaemonErrorNumberTest {

	private static final class TestError extends ADaemonErrorNumber {

		private static final long serialVersionUID = 1L;


		public TestError(final int aNumber, final String aDaemon) {
			super(aNumber, aDaemon);
		}

	}


	@Test
	public void testEquals1() {
		Assert.assertEquals(new TestError(1, "test"), new TestError(1, "test"));
	}

	@Test
	public void testEquals2() {
		final DaemonErrorNumber den = new TestError(1, "test");
		Assert.assertEquals(den, den);
	}

	@Test
	public void testNotEquals1() {
		Assert.assertNotEquals(new TestError(1, "test"), new TestError(2, "test"));
	}

	@Test
	public void testNotEquals2() {
		Assert.assertNotEquals(new TestError(1, "test1"), new TestError(1, "test2"));
	}

}
