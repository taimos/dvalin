/*
 * Copyright (c) 2016. Taimos GmbH http://www.taimos.de
 */

package de.taimos.dvalin.test.inject;

import org.omg.CORBA.Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

public class MyBean extends MySuperBean {

    @Autowired
	private BeanB bField;

	@Autowired
	@Qualifier("specialBean")
	private BeanC cField;

	private Object oField;

	private String value1;

	@Value("${expression}")
	private String value2;

	@Value("${expression}")
	private Object value3;

    public BeanB getbField() {
		return this.bField;
	}

	public BeanC getcField() {
		return this.cField;
	}

	public Object getoField() {
		return this.oField;
	}

	public String getValue1() {
		return this.value1;
	}

	public String getValue2() {
		return this.value2;
	}

	public Object getValue3() {
		return this.value3;
	}
}
