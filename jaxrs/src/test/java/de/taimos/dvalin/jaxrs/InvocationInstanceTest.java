package de.taimos.dvalin.jaxrs;

/*
 * #%L
 * JAX-RS support for dvalin using Apache CXF
 * %%
 * Copyright (C) 2015 - 2016 Taimos GmbH
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

import de.taimos.dvalin.jaxrs.monitoring.INanoTimer;
import de.taimos.dvalin.jaxrs.monitoring.InvocationInstance;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class InvocationInstanceTest {

    static MockedStatic<INanoTimer> timerMock = Mockito.mockStatic(INanoTimer.class);
    @Test
    void testStartNano() {
		InvocationInstanceTest.timerMock.when(INanoTimer::system).thenReturn((INanoTimer) () -> 42L);
        InvocationInstance ii = new InvocationInstance(UUID.randomUUID().toString(), "/");
        ii.start();
        Assertions.assertEquals(42L, ii.getStartNano());
    }

    @Test
    void testDuration() {

		InvocationInstanceTest.timerMock.when(INanoTimer::system).thenReturn((INanoTimer) () -> 1000L);
        UUID uuid = UUID.randomUUID();
        InvocationInstance ii = new InvocationInstance(uuid.toString(), "/");
        ii.start();
		InvocationInstanceTest.timerMock.when(INanoTimer::system).thenReturn((INanoTimer) () -> 2001000L);
        ii.stop();
        Assertions.assertEquals(1000L, ii.getStartNano());
        Assertions.assertEquals(2001000L, ii.getEndNano());
        Assertions.assertEquals(2L, ii.getDuration());
        String msg = "Message " + uuid +
                     " was 2 ms inflight. Access was to class 'null' and method 'null' via URI '/'";
        Assertions.assertEquals(msg, ii.toString());
    }

    @Test
    void testCalledMethod() throws Exception {
        InvocationInstance ii = new InvocationInstance(UUID.randomUUID().toString(), "/");
        ii.setCalledMethod(InvocationInstanceTest.class.getMethod("testCalledMethod"));

        Assertions.assertEquals("testCalledMethod", ii.getCalledMethodName());
        Assertions.assertEquals("de.taimos.dvalin.jaxrs.InvocationInstanceTest", ii.getCalledClass());
    }

    @Test
    void testCalledMethodNull() {
        InvocationInstance ii = new InvocationInstance(UUID.randomUUID().toString(), "/");
        ii.setCalledMethod(null);
        Assertions.assertNull(ii.getCalledMethodName());
        Assertions.assertNull(ii.getCalledClass());
        Assertions.assertNull(ii.getCalledMethod());
    }

    @AfterAll
    public static void close() {
		InvocationInstanceTest.timerMock.close();
    }
}
