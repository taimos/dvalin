package de.taimos.dvalin.interconnect.model.common.daemon;

/*
 * #%L
 * Dvalin interconnect transfer data model
 * %%
 * Copyright (C) 2016 Taimos GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
		Assertions.assertEquals(new TestError(1, "test"), new TestError(1, "test"));
	}

	@Test
	public void testEquals2() {
		final DaemonErrorNumber den = new TestError(1, "test");
		Assertions.assertEquals(den, den);
	}

	@Test
	public void testNotEquals1() {
		Assertions.assertNotEquals(new TestError(1, "test"), new TestError(2, "test"));
	}

	@Test
	public void testNotEquals2() {
		Assertions.assertNotEquals(new TestError(1, "test1"), new TestError(1, "test2"));
	}

}
