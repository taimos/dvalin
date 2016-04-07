/*
 * Copyright (c) 2016. Taimos GmbH http://www.taimos.de
 */

package de.taimos.dvalin.test.inject;

import org.junit.Assert;
import org.junit.Test;

import de.taimos.dvalin.test.AbstractMockitoTest;

public class InjectionUtilsTest extends AbstractMockitoTest {

	@Test
	public void injectSingle() throws Exception {
		BeanA a = new BeanA();
		BeanB b = new BeanB();

		MyBean bean = new MyBean();
		Assert.assertNull(bean.getaField());
		Assert.assertNull(bean.getbField());
		Assert.assertNull(bean.getcField());

		InjectionUtils.inject(bean, a);
		Assert.assertTrue(bean.getaField() == a);
		Assert.assertNull(bean.getbField());
		Assert.assertNull(bean.getcField());

		InjectionUtils.inject(bean, b);
		Assert.assertTrue(bean.getaField() == a);
		Assert.assertTrue(bean.getbField() == b);
		Assert.assertNull(bean.getcField());
	}

	@Test
	public void injectMulti() throws Exception {
		BeanA a = new BeanA();
		BeanB b = new BeanB();

		MyBean bean = new MyBean();
		Assert.assertNull(bean.getaField());
		Assert.assertNull(bean.getbField());
		Assert.assertNull(bean.getcField());

		InjectionUtils.inject(bean, a, b);

		Assert.assertTrue(bean.getaField() == a);
		Assert.assertTrue(bean.getbField() == b);
		Assert.assertNull(bean.getcField());
	}

    @Test
    public void injectAll() throws Exception {
        MyBean bean = new MyBean();
        Assert.assertNull(bean.getaField());
        Assert.assertNull(bean.getbField());
        Assert.assertNull(bean.getcField());

        InjectionMock mocks = InjectionUtils.injectMocks(bean);
        Assert.assertNotNull(bean.getaField());
        Assert.assertNotNull(bean.getbField());
        Assert.assertNotNull(bean.getcField());
        Assert.assertNull(bean.getoField());
        Assert.assertNull(bean.getValue1());
        Assert.assertNull(bean.getValue2());
        Assert.assertNull(bean.getValue3());

        Assert.assertSame(bean.getaField(), mocks.getMock("aField"));
        Assert.assertSame(bean.getaField(), mocks.getMock(BeanA.class));
        Assert.assertSame(bean.getbField(), mocks.getMock("bField"));
        Assert.assertSame(bean.getbField(), mocks.getMock(BeanB.class));
        Assert.assertSame(bean.getcField(), mocks.getMock("cField"));
        Assert.assertSame(bean.getcField(), mocks.getMock(BeanC.class));
        Assert.assertSame(bean.getcField(), mocks.getMock(BeanC.class, "specialBean"));
        Assert.assertNull(mocks.getMock(BeanC.class, "invalidQualifier"));

        Assert.assertNull(mocks.getMock(BeanA.class, "invalidQualifier"));
        Assert.assertNull(mocks.getMock(Object.class));
        Assert.assertNull(mocks.getMock("oField"));
        Assert.assertNull(mocks.getMock("nonExisting"));
    }

	@Test
	public void injectWithQualifier() throws Exception {
		BeanC c = new BeanC();

		MyBean bean = new MyBean();
		Assert.assertNull(bean.getaField());
		Assert.assertNull(bean.getbField());
		Assert.assertNull(bean.getcField());

		InjectionUtils.inject(bean, "specialBean", c);
		Assert.assertNull(bean.getaField());
		Assert.assertNull(bean.getbField());
		Assert.assertTrue(bean.getcField() == c);
	}

	@Test
	public void injectUnqualifiedFieldWithQualifier() throws Exception {
		BeanB b = new BeanB();

		MyBean bean = new MyBean();
		Assert.assertNull(bean.getaField());
		Assert.assertNull(bean.getbField());
		Assert.assertNull(bean.getcField());

		InjectionUtils.inject(bean, "myQualifier", b);
		Assert.assertNull(bean.getaField());
		Assert.assertTrue(bean.getbField() == b);
		Assert.assertNull(bean.getcField());
	}

	@Test(expected = RuntimeException.class)
	public void injectWithWrongQualifier() throws Exception {
		BeanC c = new BeanC();

		MyBean bean = new MyBean();
		Assert.assertNull(bean.getaField());
		Assert.assertNull(bean.getbField());
		Assert.assertNull(bean.getcField());

		InjectionUtils.inject(bean, "wrongQualifier", c);
	}

	@Test(expected = RuntimeException.class)
	public void injectWithoutQualifier() throws Exception {
		BeanC c = new BeanC();

		MyBean bean = new MyBean();
		Assert.assertNull(bean.getaField());
		Assert.assertNull(bean.getbField());
		Assert.assertNull(bean.getcField());

		InjectionUtils.inject(bean, c);
	}

	@Test(expected = RuntimeException.class)
	public void injectNonwiredDependency() throws Exception {
		Object o = new Object();

		MyBean bean = new MyBean();
		Assert.assertNull(bean.getaField());
		Assert.assertNull(bean.getbField());
		Assert.assertNull(bean.getcField());

		InjectionUtils.inject(bean, o);
	}

	@Test
	public void injectValue() throws Exception {
		MyBean bean = new MyBean();
		Assert.assertNull(bean.getValue1());
		Assert.assertNull(bean.getValue2());

		InjectionUtils.injectValue(bean, "value2", "foobar");
		Assert.assertNull(bean.getValue1());
		Assert.assertEquals("foobar", bean.getValue2());
	}

	@Test(expected = RuntimeException.class)
	public void injectValueIntoNonannotatedField() throws Exception {
		MyBean bean = new MyBean();
		Assert.assertNull(bean.getValue1());
		Assert.assertNull(bean.getValue2());

		InjectionUtils.injectValue(bean, "value1", "foobar");
	}

	@Test(expected = RuntimeException.class)
	public void injectValueIntoNonStringField() throws Exception {
		MyBean bean = new MyBean();
		Assert.assertNull(bean.getValue1());
		Assert.assertNull(bean.getValue2());

		InjectionUtils.injectValue(bean, "oField", "foobar");
	}

	@Test(expected = RuntimeException.class)
	public void injectValueIntoNonStringField2() throws Exception {
		MyBean bean = new MyBean();
		Assert.assertNull(bean.getValue1());
		Assert.assertNull(bean.getValue2());

		InjectionUtils.injectValue(bean, "value3", "foobar");
	}

	@Test(expected = RuntimeException.class)
	public void injectValueIntoNonExistingField() throws Exception {
		MyBean bean = new MyBean();
		Assert.assertNull(bean.getValue1());
		Assert.assertNull(bean.getValue2());

		InjectionUtils.injectValue(bean, "nonExisting", "foobar");
	}
}
