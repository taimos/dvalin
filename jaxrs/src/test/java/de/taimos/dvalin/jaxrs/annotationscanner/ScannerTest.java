package de.taimos.dvalin.jaxrs.annotationscanner;

import java.util.List;

import javax.annotation.security.RolesAllowed;

import org.junit.Assert;
import org.junit.Test;

import de.taimos.dvalin.jaxrs.JaxRsAnnotationScanner;

public class ScannerTest {

    @Test
    public void testFoobarAnno1() throws Exception {
        List<Anno1> list = JaxRsAnnotationScanner.searchForAnnotation(ApiImpl.class.getMethod("foobar"), Anno1.class);
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void testBarbazAnno1() throws Exception {
        List<Anno1> list = JaxRsAnnotationScanner.searchForAnnotation(ApiImpl.class.getMethod("barbaz"), Anno1.class);
        Assert.assertEquals(2, list.size());
    }

    @Test
    public void testBlubbAnno1() throws Exception {
        List<Anno1> list = JaxRsAnnotationScanner.searchForAnnotation(ApiImpl.class.getMethod("blubb"), Anno1.class);
        Assert.assertEquals(2, list.size());
    }

    @Test
    public void testFoobarAnno2() throws Exception {
        List<Anno2> list = JaxRsAnnotationScanner.searchForAnnotation(ApiImpl.class.getMethod("foobar"), Anno2.class);
        Assert.assertEquals(2, list.size());
    }

    @Test
    public void testBarbazAnno2() throws Exception {
        List<Anno2> list = JaxRsAnnotationScanner.searchForAnnotation(ApiImpl.class.getMethod("barbaz"), Anno2.class);
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void testBlubbAnno2() throws Exception {
        List<Anno2> list = JaxRsAnnotationScanner.searchForAnnotation(ApiImpl.class.getMethod("blubb"), Anno2.class);
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void testNullMethod() throws Exception {
        List<Anno1> list = JaxRsAnnotationScanner.searchForAnnotation(null, Anno1.class);
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void testPresentAnno1() throws Exception {
        boolean present = JaxRsAnnotationScanner.hasAnnotation(ApiImpl.class.getMethod("foobar"), Anno1.class);
        Assert.assertTrue(present);
    }

    @Test
    public void testNotPresentRolesAllowed() throws Exception {
        boolean present = JaxRsAnnotationScanner.hasAnnotation(ApiImpl.class.getMethod("foobar"), RolesAllowed.class);
        Assert.assertFalse(present);
    }

}
