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

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.taimos.dvalin.jaxrs.monitoring.InvocationInstance;

@RunWith(PowerMockRunner.class)
@PrepareForTest(InvocationInstance.class)
public class InvocationInstanceTest {

    @Test
    public void testStartNano() throws Exception {
        PowerMockito.mockStatic(System.class);
        PowerMockito.when(System.nanoTime()).thenReturn(42L);
        InvocationInstance ii = new InvocationInstance(UUID.randomUUID());
        ii.start();
        Assert.assertEquals(42L, ii.getStartNano());
    }

    @Test
    public void testDuration() throws Exception {
        PowerMockito.mockStatic(System.class);
        PowerMockito.when(System.nanoTime()).thenReturn(1000L);
        UUID uuid = UUID.randomUUID();
        InvocationInstance ii = new InvocationInstance(uuid);
        ii.start();
        PowerMockito.when(System.nanoTime()).thenReturn(2001000L);
        ii.stop();
        Assert.assertEquals(1000L, ii.getStartNano());
        Assert.assertEquals(2001000L, ii.getEndNano());
        Assert.assertEquals(2L, ii.getDuration());
        String msg = "Message " + uuid.toString() + " was 2 ms inflight. Access was to class 'null' and method 'null'";
        Assert.assertEquals(msg, ii.toString());
    }

    @Test
    public void testCalledMethod() throws Exception {
        InvocationInstance ii = new InvocationInstance(UUID.randomUUID());
        ii.setCalledMethod(InvocationInstanceTest.class.getMethod("testCalledMethod"));

        Assert.assertEquals("testCalledMethod", ii.getCalledMethodName());
        Assert.assertEquals("de.taimos.dvalin.jaxrs.InvocationInstanceTest", ii.getCalledClass());
    }

    @Test
    public void testCalledMethodNull() throws Exception {
        InvocationInstance ii = new InvocationInstance(UUID.randomUUID());
        ii.setCalledMethod(null);
        Assert.assertEquals(null, ii.getCalledMethodName());
        Assert.assertEquals(null, ii.getCalledClass());
        Assert.assertEquals(null, ii.getCalledMethod());
    }
}
