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
