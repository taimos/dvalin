/*
 * Copyright (c) 2016. Taimos GmbH http://www.taimos.de
 */

package de.taimos.dvalin.test.inject;

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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.taimos.dvalin.test.AbstractMockitoTest;

class InjectionUtilsTest extends AbstractMockitoTest {

	@Test
    void injectSingle() {
		BeanA a = new BeanA();
		BeanB b = new BeanB();

		MyBean bean = new MyBean();
		Assertions.assertNull(bean.getaField());
		Assertions.assertNull(bean.getbField());
		Assertions.assertNull(bean.getcField());

		InjectionUtils.inject(bean, a);
		Assertions.assertSame(bean.getaField(), a);
		Assertions.assertNull(bean.getbField());
		Assertions.assertNull(bean.getcField());

		InjectionUtils.inject(bean, b);
		Assertions.assertSame(bean.getaField(), a);
		Assertions.assertSame(bean.getbField(), b);
		Assertions.assertNull(bean.getcField());
	}

	@Test
    void injectMulti() {
		BeanA a = new BeanA();
		BeanB b = new BeanB();

		MyBean bean = new MyBean();
		Assertions.assertNull(bean.getaField());
		Assertions.assertNull(bean.getbField());
		Assertions.assertNull(bean.getcField());

		InjectionUtils.inject(bean, a, b);

		Assertions.assertSame(bean.getaField(), a);
		Assertions.assertSame(bean.getbField(), b);
		Assertions.assertNull(bean.getcField());
	}

    @Test
    void injectAll() {
        MyBean bean = new MyBean();
        Assertions.assertNull(bean.getaField());
        Assertions.assertNull(bean.getbField());
        Assertions.assertNull(bean.getcField());

        InjectionMock mocks = InjectionUtils.injectMocks(bean);
        Assertions.assertNotNull(bean.getaField());
        Assertions.assertNotNull(bean.getbField());
        Assertions.assertNotNull(bean.getcField());
        Assertions.assertNull(bean.getoField());
        Assertions.assertNull(bean.getValue1());
        Assertions.assertNull(bean.getValue2());
        Assertions.assertNull(bean.getValue3());

        Assertions.assertSame(bean.getaField(), mocks.getMock("aField"));
        Assertions.assertSame(bean.getaField(), mocks.getMock(BeanA.class));
        Assertions.assertSame(bean.getbField(), mocks.getMock("bField"));
        Assertions.assertSame(bean.getbField(), mocks.getMock(BeanB.class));
        Assertions.assertSame(bean.getcField(), mocks.getMock("cField"));
        Assertions.assertSame(bean.getcField(), mocks.getMock(BeanC.class));
        Assertions.assertSame(bean.getcField(), mocks.getMock(BeanC.class, "specialBean"));
        Assertions.assertNull(mocks.getMock(BeanC.class, "invalidQualifier"));

        Assertions.assertNull(mocks.getMock(BeanA.class, "invalidQualifier"));
        Assertions.assertNull(mocks.getMock(Object.class));
        Assertions.assertNull(mocks.getMock("oField"));
        Assertions.assertNull(mocks.getMock("nonExisting"));
    }

	@Test
    void injectWithQualifier() {
		BeanC c = new BeanC();

		MyBean bean = new MyBean();
		Assertions.assertNull(bean.getaField());
		Assertions.assertNull(bean.getbField());
		Assertions.assertNull(bean.getcField());

		InjectionUtils.inject(bean, "specialBean", c);
		Assertions.assertNull(bean.getaField());
		Assertions.assertNull(bean.getbField());
		Assertions.assertSame(bean.getcField(), c);
	}

	@Test
    void injectUnqualifiedFieldWithQualifier() {
		BeanB b = new BeanB();

		MyBean bean = new MyBean();
		Assertions.assertNull(bean.getaField());
		Assertions.assertNull(bean.getbField());
		Assertions.assertNull(bean.getcField());

		InjectionUtils.inject(bean, "myQualifier", b);
		Assertions.assertNull(bean.getaField());
		Assertions.assertSame(bean.getbField(), b);
		Assertions.assertNull(bean.getcField());
	}

	@Test
    void injectWithWrongQualifier() {
		BeanC c = new BeanC();

		MyBean bean = new MyBean();
		Assertions.assertNull(bean.getaField());
		Assertions.assertNull(bean.getbField());
		Assertions.assertNull(bean.getcField());
        Assertions.assertThrows(RuntimeException.class, () -> {
            InjectionUtils.inject(bean, "wrongQualifier", c);
        });
	}

	@Test
    void injectWithoutQualifier() {
		BeanC c = new BeanC();

		MyBean bean = new MyBean();
		Assertions.assertNull(bean.getaField());
		Assertions.assertNull(bean.getbField());
		Assertions.assertNull(bean.getcField());

        Assertions.assertThrows(RuntimeException.class, () -> {
            InjectionUtils.inject(bean, c);
        });
	}

	@Test
    void injectNonwiredDependency() {
		Object o = new Object();

		MyBean bean = new MyBean();
		Assertions.assertNull(bean.getaField());
		Assertions.assertNull(bean.getbField());
		Assertions.assertNull(bean.getcField());

        Assertions.assertThrows(RuntimeException.class, () -> {
            InjectionUtils.inject(bean, o);
        });
	}

	@Test
    void injectValue() {
		MyBean bean = new MyBean();
		Assertions.assertNull(bean.getValue1());
		Assertions.assertNull(bean.getValue2());

		InjectionUtils.injectValue(bean, "value2", "foobar");
		Assertions.assertNull(bean.getValue1());
		Assertions.assertEquals("foobar", bean.getValue2());
	}

	@Test
    void injectValueIntoNonannotatedField() {
		MyBean bean = new MyBean();
		Assertions.assertNull(bean.getValue1());
		Assertions.assertNull(bean.getValue2());

        Assertions.assertThrows(RuntimeException.class, () -> {
            InjectionUtils.injectValue(bean, "value1", "foobar");
        });
	}

	@Test
    void injectValueIntoNonStringField() {
		MyBean bean = new MyBean();
		Assertions.assertNull(bean.getValue1());
		Assertions.assertNull(bean.getValue2());

        Assertions.assertThrows(RuntimeException.class, () -> {
            InjectionUtils.injectValue(bean, "oField", "foobar");
        });
	}

	@Test
    void injectValueIntoNonStringField2() {
		MyBean bean = new MyBean();
		Assertions.assertNull(bean.getValue1());
		Assertions.assertNull(bean.getValue2());

        Assertions.assertThrows(RuntimeException.class, () -> {
            InjectionUtils.injectValue(bean, "value3", "foobar");
        });
	}

	@Test
    void injectValueIntoNonExistingField() {
		MyBean bean = new MyBean();
		Assertions.assertNull(bean.getValue1());
		Assertions.assertNull(bean.getValue2());

        Assertions.assertThrows(RuntimeException.class, () -> {
            InjectionUtils.injectValue(bean, "nonExisting", "foobar");
        });
	}
}
