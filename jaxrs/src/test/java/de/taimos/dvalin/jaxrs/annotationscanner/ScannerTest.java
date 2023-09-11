package de.taimos.dvalin.jaxrs.annotationscanner;

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

import java.util.List;

import jakarta.annotation.security.RolesAllowed;

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
