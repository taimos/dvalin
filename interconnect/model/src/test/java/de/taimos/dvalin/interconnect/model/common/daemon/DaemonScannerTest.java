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

import de.taimos.dvalin.interconnect.model.InterconnectList;
import de.taimos.dvalin.interconnect.model.InterconnectObject;
import de.taimos.dvalin.interconnect.model.ivo.IVO;
import de.taimos.dvalin.interconnect.model.ivo.daemon.VoidIVO;
import de.taimos.dvalin.interconnect.model.service.ADaemonHandler;
import de.taimos.dvalin.interconnect.model.service.DaemonError;
import de.taimos.dvalin.interconnect.model.service.DaemonReceiverMethod;
import de.taimos.dvalin.interconnect.model.service.DaemonRequestMethod;
import de.taimos.dvalin.interconnect.model.service.DaemonScanner;
import de.taimos.dvalin.interconnect.model.service.IDaemon;
import de.taimos.dvalin.interconnect.model.service.IDaemonHandler;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings({"javadoc", "unused"})
public class DaemonScannerTest {

	@BeforeClass
	public static void setUp() {
		Logger.getGlobal().setLevel(Level.INFO);
		Logger.getGlobal().addHandler(new ConsoleHandler());
	}


	public interface ITestMissingException extends IDaemon, IDaemonHandler {

		@DaemonRequestMethod(idempotent = false)
        void testVoid(final VoidIVO ivo);
	}

    public interface ITestWrongMultiExceptionTypes extends IDaemon, IDaemonHandler {

        @DaemonRequestMethod(idempotent = false)
        void testVoid(final VoidIVO ivo) throws DaemonError;
    }

	@Test(expected = IllegalStateException.class)
	public void testMissingExceptionInInterface() {
		DaemonScanner.scan(ITestMissingException.class);
	}

    @Test(expected = IllegalStateException.class)
    public void testWrongExceptionInInterface() {
        DaemonScanner.scan(ITestWrongMultiExceptionTypes.class);
    }

	@Test
	public void testMisingExceptionInImplementingClass() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public void testVoid(final VoidIVO ivo) {
				// nothing to do here
			}
		};
		DaemonScanner.scan(rh.getClass());
	}

	@Test(expected = IllegalStateException.class)
	public void testMisingRequestIVO() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public void testVoid() throws DaemonError {
				// nothing to do here
			}
		};
		DaemonScanner.scan(rh.getClass());
	}

	@Test(expected = IllegalStateException.class)
	public void testMisingRequestIVOAndExceprion() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public void testVoid() {
				// nothing to do here
			}
		};
		DaemonScanner.scan(rh.getClass());
	}

	@Test
	public void testVoidResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public void testVoid(final VoidIVO ivo) throws DaemonError {
				// nothing to do here
			}
		};
        Assert.assertEquals(2, DaemonScanner.scan(rh.getClass()).size());
	}

	@Test
	public void testIVOResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public VoidIVO testVoid(final VoidIVO ivo) throws DaemonError {
				return null;
			}
		};
		DaemonScanner.scan(rh.getClass());
	}

	@Test
	public void testDeprecatedInterconnectObjectResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public InterconnectObject testVoid(final VoidIVO ivo) throws DaemonError {
				return null;
			}
		};
		DaemonScanner.scan(rh.getClass());
	}

	@Test(expected = IllegalStateException.class)
	public void testInterfaceIVOResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public VoidIVO testVoid(final InterfaceIVO ivo) throws DaemonError {
				return null;
			}
		};
		DaemonScanner.scan(rh.getClass());
	}

	@Test(expected = IllegalStateException.class)
	public void testDuplicate() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public VoidIVO testVoid(final VoidIVO ivo) throws DaemonError {
				return null;
			}

			@DaemonRequestMethod(idempotent = false)
			public VoidIVO testVoid2(final VoidIVO ivo) throws DaemonError {
				return null;
			}
		};
		DaemonScanner.scan(rh.getClass());
	}

	@Test
	public void testIVOResultAndNoise() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public VoidIVO testVoid(final VoidIVO ivo) throws DaemonError {
				return null;
			}

			public VoidIVO testAnotherPublicMethod() {
				return null;
			}
		};
		DaemonScanner.scan(rh.getClass());
	}

	@Test
	public void testIVOListResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public List<VoidIVO> testVoid(final VoidIVO ivo) throws DaemonError {
				return null;
			}
		};
		Assert.assertEquals(2, DaemonScanner.scan(rh.getClass()).size());
	}

	@Test
	public void testDeprecatedInterconnectObjectListResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public List<InterconnectObject> testVoid(final VoidIVO ivo) throws DaemonError {
				return null;
			}
		};
		Assert.assertEquals(2, DaemonScanner.scan(rh.getClass()).size());
	}

	@Test(expected = IllegalStateException.class)
	public void testIVOSetResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public Set<VoidIVO> testVoid(final VoidIVO ivo) throws DaemonError {
				return null;
			}
		};
		Assert.assertEquals(2, DaemonScanner.scan(rh.getClass()).size());
	}

	@Test(expected = IllegalStateException.class)
	public void testIVOCollectionResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public Collection<VoidIVO> testVoid(final VoidIVO ivo) throws DaemonError {
				return null;
			}
		};
		DaemonScanner.scan(rh.getClass());
	}

	@Test
	public void testIVOArrayResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public VoidIVO[] testVoid(final VoidIVO ivo) throws DaemonError {
				return null;
			}
		};
		DaemonScanner.scan(rh.getClass());
	}

	@Test
	public void testDeprecatedInterconnectObjectArrayResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public InterconnectObject[] testVoid(final VoidIVO ivo) throws DaemonError {
				return null;
			}
		};
		DaemonScanner.scan(rh.getClass());
	}

	@Test(expected = IllegalStateException.class)
	public void testListResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public List<Long> testVoid(final VoidIVO ivo) throws DaemonError {
				return null;
			}
		};
		DaemonScanner.scan(rh.getClass());
	}

	@Test(expected = IllegalStateException.class)
	public void testSetResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public Set<Long> testVoid(final VoidIVO ivo) throws DaemonError {
				return null;
			}
		};
		DaemonScanner.scan(rh.getClass());
	}

	@Test(expected = IllegalStateException.class)
	public void testCollectionResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public Collection<Long> testVoid(final VoidIVO ivo) throws DaemonError {
				return null;
			}
		};
		DaemonScanner.scan(rh.getClass());
	}

	@Test(expected = IllegalStateException.class)
	public void testArrayResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public Long[] testVoid(final VoidIVO ivo) throws DaemonError {
				return null;
			}
		};
		DaemonScanner.scan(rh.getClass());
	}

	@Test(expected = IllegalStateException.class)
	public void testPrimitiveArrayResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public long[] testVoid(final VoidIVO ivo) throws DaemonError {
				return null;
			}
		};
		DaemonScanner.scan(rh.getClass());
	}

	@Test
	public void testReceiver() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonReceiverMethod(idempotent = false)
			public void testVoid(final VoidIVO ivo) {
				// nothing to do here
			}
		};
		Assert.assertEquals(2, DaemonScanner.scan(rh.getClass()).size());
	}

	@Test
	public void testDeprecatedReceiver() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonReceiverMethod(idempotent = false)
			public void testVoid(final InterconnectList<?> ivo) {
				// nothing to do here
			}
		};
		Assert.assertEquals(2, DaemonScanner.scan(rh.getClass()).size());
	}

	@Test(expected = IllegalStateException.class)
	public void testReceiverWithException() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonReceiverMethod(idempotent = false)
			public void testVoid(final VoidIVO ivo) throws DaemonError {
				// nothing to do here
			}
		};
		DaemonScanner.scan(rh.getClass());
	}

	@Test(expected = IllegalStateException.class)
	public void testReceiverWithReturn() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonReceiverMethod(idempotent = false)
			public VoidIVO testVoid(final VoidIVO ivo) {
				return null;
			}
		};
		DaemonScanner.scan(rh.getClass());
	}

	@Test
	public void testInheritance() {
		final TestTestRequestHandler rh = new TestTestRequestHandler() {
			// nothing
		};
		Assert.assertEquals(2, DaemonScanner.scan(rh.getClass()).size());
	}

	@Test
	public void testInheritance2() {
		final TestTestRequestHandler rh = new TestTestRequestHandler();
		Assert.assertEquals(2, DaemonScanner.scan(rh.getClass()).size());
	}


	private class TestTestRequestHandler extends TestRequestHandler implements ITest {

		@Override
		public void testVoid(final VoidIVO ivo) {
			// nothing to do here
		}

	}

	private interface ITest extends IDaemon {

		@DaemonReceiverMethod(idempotent = false)
		void testVoid(final VoidIVO ivo);
	}

	private abstract class TestRequestHandler extends ADaemonHandler {
        //
	}

	private interface InterfaceIVO extends IVO {
		// marker
	}

}
