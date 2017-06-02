package de.taimos.dvalin.test;

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

import java.util.Objects;
import java.util.function.Function;

import org.junit.Assert;

public class AssertErrors {
    
    public static void assertThrows(Class<? extends Exception> clazz, Execute func) {
        assertThrows(func, e -> Objects.equals(clazz, e.getClass()));
    }
    
    public static void assertThrows(Execute func, Function<Exception, Boolean> condition) {
        try {
            func.apply();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(condition.apply(e));
        }
    }
    
    @FunctionalInterface
    public interface Execute {
        void apply();
    }
}
