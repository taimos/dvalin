package de.taimos.dvalin.jaxrs;

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
