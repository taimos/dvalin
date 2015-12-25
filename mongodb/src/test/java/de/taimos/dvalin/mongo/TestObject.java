package de.taimos.dvalin.mongo;

/*
 * #%L
 * Spring DAO Mongo
 * %%
 * Copyright (C) 2013 - 2015 Taimos GmbH
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

import java.math.BigDecimal;

import org.joda.time.DateTime;

public class TestObject extends AEntity {

    private static final long serialVersionUID = 1L;

    private String name;

    private BigDecimal value;

    private DateTime dt = DateTime.now();


    public DateTime getDt() {
        return this.dt;
    }

    public void setDt(DateTime dt) {
        this.dt = dt;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getValue() {
        return this.value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "TestObject [name=" + this.name + ", value=" + this.value + ", id=" + this.id + "]";
    }

}
