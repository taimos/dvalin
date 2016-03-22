package de.taimos.dvalin.jaxrs;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.provider.StringTextProvider;
import org.junit.Assert;
import org.junit.Test;

public class StringTextProviderTest {

    @Test
    public void testWritable() throws Exception {
        StringTextProvider prov = new StringTextProvider();

        Assert.assertTrue(prov.isWriteable(String.class, null, null, new MediaType("application", "octet-stream")));
        Assert.assertTrue(prov.isWriteable(String.class, null, null, new MediaType("application", "pdf")));
        Assert.assertTrue(prov.isWriteable(String.class, null, null, new MediaType("application", "foobar")));

        Assert.assertFalse(prov.isWriteable(String.class, null, null, new MediaType("application", "json")));
        Assert.assertFalse(prov.isWriteable(String.class, null, null, new MediaType("application", "foobar+json")));

        Assert.assertFalse(prov.isWriteable(Object.class, null, null, new MediaType("application", "foobar")));
        Assert.assertFalse(prov.isWriteable(Object.class, null, null, new MediaType("application", "pdf")));
    }
}
