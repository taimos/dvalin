/*
 * Copyright (c) 2016. Taimos GmbH http://www.taimos.de
 */

package de.taimos.dvalin.test.jaxrs;

/*-
 * #%L
 * Test support for dvalin
 * %%
 * Copyright (C) 2016 - 2017 Taimos GmbH
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.junit.Assert;

import de.taimos.dvalin.jaxrs.JaxRsAnnotationScanner;
import de.taimos.dvalin.jaxrs.security.annotation.LoggedIn;

public class AnnotationAssert {
	
	public static void needsLogIn(Class<?> clazz, String method, Class<?>... parameterTypes) {
		AnnotationAssert.assertAnnotation(LoggedIn.class, clazz, method, parameterTypes);
	}
	
	public static void assertPublic(Class<?> clazz, String method, Class<?>... parameterTypes) {
		AnnotationAssert.assertNotAnnotation(LoggedIn.class, clazz, method, parameterTypes);
	}
	
	public static void assertAnnotation(Class<? extends Annotation> annotation, Class<?> clazz, String method, Class<?>... parameterTypes) {
		Assert.assertTrue(hasAnnotation(annotation, clazz, method, parameterTypes));
	}
	
	public static void assertNotAnnotation(Class<? extends Annotation> annotation, Class<?> clazz, String method, Class<?>... parameterTypes) {
		Assert.assertFalse(hasAnnotation(annotation, clazz, method, parameterTypes));
	}
	
	private static boolean hasAnnotation(Class<? extends Annotation> annotation, Class<?> clazz, String method, Class<?>... parameterTypes) {
		Method apiMethod;
		try {
			apiMethod = clazz.getMethod(method, parameterTypes);
		} catch (NoSuchMethodException e) {
			throw new AssertionError(e);
		}
		return JaxRsAnnotationScanner.hasAnnotation(apiMethod, annotation);
	}
}
