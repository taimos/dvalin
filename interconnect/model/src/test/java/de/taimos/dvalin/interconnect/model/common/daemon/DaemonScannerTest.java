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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.SystemException;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings({"javadoc", "unused"})
public class DaemonScannerTest {

	@BeforeAll
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
        void testVoid(final VoidIVO ivo) throws DaemonError, SystemException;
    }

	@Test
    void testMissingExceptionInInterface() {
        Assertions.assertThrows(IllegalStateException.class, () -> DaemonScanner.scan(ITestMissingException.class));
	}

    @Test
    void testWrongExceptionInInterface() {
        Assertions.assertThrows(IllegalStateException.class, () -> DaemonScanner.scan(ITestWrongMultiExceptionTypes.class));
    }

	@Test
    void testMisingExceptionInImplementingClass() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public void testVoid(final VoidIVO ivo) {
				// nothing to do here
			}
		};
		DaemonScanner.scan(rh.getClass());
	}

	@Test
    void testMisingRequestIVO() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            final TestRequestHandler rh = new TestRequestHandler() {

                @DaemonRequestMethod(idempotent = false)
                public void testVoid() throws DaemonError {
                    // nothing to do here
                }
            };
            DaemonScanner.scan(rh.getClass());
        });
	}

	@Test
    void testMisingRequestIVOAndExceprion() {
        final TestRequestHandler rh = new TestRequestHandler() {

            @DaemonRequestMethod(idempotent = false)
            public void testVoid() {
                // nothing to do here
            }
        };
        Assertions.assertThrows(IllegalStateException.class, () -> DaemonScanner.scan(rh.getClass()));
	}

	@Test
    void testVoidResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public void testVoid(final VoidIVO ivo) {
				// nothing to do here
			}
		};
        Assertions.assertEquals(2, DaemonScanner.scan(rh.getClass()).size());
	}

	@Test
    void testIVOResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public VoidIVO testVoid(final VoidIVO ivo) {
				return null;
			}
		};
		DaemonScanner.scan(rh.getClass());
	}

	@Test
    void testDeprecatedInterconnectObjectResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public InterconnectObject testVoid(final VoidIVO ivo) throws DaemonError {
				return null;
			}
		};
		DaemonScanner.scan(rh.getClass());
	}

	@Test
    void testInterfaceIVOResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public VoidIVO testVoid(final InterfaceIVO ivo) throws DaemonError {
				return null;
			}
		};
        Assertions.assertThrows(IllegalStateException.class, () -> {
            DaemonScanner.scan(rh.getClass());
        });
	}

	@Test
    void testDuplicate() {
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
        Assertions.assertThrows(IllegalStateException.class, () -> {
            DaemonScanner.scan(rh.getClass());
        });
	}

	@Test
    void testIVOResultAndNoise() {
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
    void testIVOListResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public List<VoidIVO> testVoid(final VoidIVO ivo) throws DaemonError {
				return null;
			}
		};
		Assertions.assertEquals(2, DaemonScanner.scan(rh.getClass()).size());
	}

	@Test
    void testDeprecatedInterconnectObjectListResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public List<InterconnectObject> testVoid(final VoidIVO ivo) throws DaemonError {
				return null;
			}
		};
		Assertions.assertEquals(2, DaemonScanner.scan(rh.getClass()).size());
	}

	@Test
    void testIVOSetResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public Set<VoidIVO> testVoid(final VoidIVO ivo) throws DaemonError {
				return null;
			}
		};
        Assertions.assertThrows(IllegalStateException.class, () -> {
            Assertions.assertEquals(2, DaemonScanner.scan(rh.getClass()).size());
        });
	}

	@Test
    void testIVOCollectionResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public Collection<VoidIVO> testVoid(final VoidIVO ivo) throws DaemonError {
				return null;
			}
		};
        Assertions.assertThrows(IllegalStateException.class, () -> {
            DaemonScanner.scan(rh.getClass());
        });
	}

	@Test
    void testIVOArrayResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public VoidIVO[] testVoid(final VoidIVO ivo) throws DaemonError {
				return null;
			}
		};
		DaemonScanner.scan(rh.getClass());
	}

	@Test
    void testDeprecatedInterconnectObjectArrayResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public InterconnectObject[] testVoid(final VoidIVO ivo) throws DaemonError {
				return null;
			}
		};
		DaemonScanner.scan(rh.getClass());
	}

	@Test
    void testListResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public List<Long> testVoid(final VoidIVO ivo) throws DaemonError {
				return null;
			}
		};
        Assertions.assertThrows(IllegalStateException.class, () -> {
            DaemonScanner.scan(rh.getClass());
        });
	}

	@Test
    void testSetResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public Set<Long> testVoid(final VoidIVO ivo) throws DaemonError {
				return null;
			}
		};
        Assertions.assertThrows(IllegalStateException.class, () -> {
            DaemonScanner.scan(rh.getClass());
        });
	}

	@Test
    void testCollectionResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public Collection<Long> testVoid(final VoidIVO ivo) throws DaemonError {
				return null;
			}
		};
        Assertions.assertThrows(IllegalStateException.class, () -> {
            DaemonScanner.scan(rh.getClass());
        });
	}

	@Test
    void testArrayResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public Long[] testVoid(final VoidIVO ivo) throws DaemonError {
				return null;
			}
		};
        Assertions.assertThrows(IllegalStateException.class, () -> {
            DaemonScanner.scan(rh.getClass());
        });
	}

	@Test
    void testPrimitiveArrayResult() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonRequestMethod(idempotent = false)
			public long[] testVoid(final VoidIVO ivo) throws DaemonError {
				return null;
			}
		};
        Assertions.assertThrows(IllegalStateException.class, () -> {
            DaemonScanner.scan(rh.getClass());
        });
	}

	@Test
    void testReceiver() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonReceiverMethod(idempotent = false)
			public void testVoid(final VoidIVO ivo) {
				// nothing to do here
			}
		};
		Assertions.assertEquals(2, DaemonScanner.scan(rh.getClass()).size());
	}

	@Test
    void testDeprecatedReceiver() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonReceiverMethod(idempotent = false)
			public void testVoid(final InterconnectList<?> ivo) {
				// nothing to do here
			}
		};
		Assertions.assertEquals(2, DaemonScanner.scan(rh.getClass()).size());
	}

	@Test
    void testReceiverWithException() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonReceiverMethod(idempotent = false)
			public void testVoid(final VoidIVO ivo) throws DaemonError {
				// nothing to do here
			}
		};
        Assertions.assertThrows(IllegalStateException.class, () -> {
            DaemonScanner.scan(rh.getClass());
        });
	}

	@Test
    void testReceiverWithReturn() {
		final TestRequestHandler rh = new TestRequestHandler() {

			@DaemonReceiverMethod(idempotent = false)
			public VoidIVO testVoid(final VoidIVO ivo) {
				return null;
			}
		};
        Assertions.assertThrows(IllegalStateException.class, () -> {
            DaemonScanner.scan(rh.getClass());
        });
	}

	@Test
    void testInheritance() {
		final TestTestRequestHandler rh = new TestTestRequestHandler() {
			// nothing
		};
		Assertions.assertEquals(2, DaemonScanner.scan(rh.getClass()).size());
	}

	@Test
    void testInheritance2() {
		final TestTestRequestHandler rh = new TestTestRequestHandler();
		Assertions.assertEquals(2, DaemonScanner.scan(rh.getClass()).size());
	}


	private static class TestTestRequestHandler extends TestRequestHandler implements ITest {

		@Override
		public void testVoid(final VoidIVO ivo) {
			// nothing to do here
		}

	}

	private interface ITest extends IDaemon {

		@DaemonReceiverMethod(idempotent = false)
		void testVoid(final VoidIVO ivo);
	}

	private abstract static class TestRequestHandler extends ADaemonHandler {
        //
	}

	private interface InterfaceIVO extends IVO {
		// marker
	}

}
