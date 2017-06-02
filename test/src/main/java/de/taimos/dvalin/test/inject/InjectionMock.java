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

import java.util.HashMap;
import java.util.Map;

public class InjectionMock {

    private static class InjectedMock<T> {
        private String fieldName;
        private Class<T> targetType;
        private T mock;
        private String qualifier;
    }

    private final Map<String, InjectedMock> mocks = new HashMap<>();

    /**
     * add the given mock to the list of available mocks
     *
     * @param fieldName  the name of the field the mock was injected into
     * @param mock       the mock object itself
     * @param targetType the type of the field the mock was injected into
     */
    public void addMock(String fieldName, Object mock, Class<?> targetType) {
        this.addMock(fieldName, mock, targetType, null);
    }

    /**
     * add the given mock to the list of available mocks
     *
     * @param fieldName  the name of the field the mock was injected into
     * @param mock       the mock object itself
     * @param targetType the type of the field the mock was injected into
     * @param qualifier  the qualifier of the field the mock was injected into
     */
    public void addMock(String fieldName, Object mock, Class<?> targetType, String qualifier) {
        InjectedMock im = new InjectedMock();
        im.fieldName = fieldName;
        im.mock = mock;
        im.targetType = targetType;
        im.qualifier = qualifier;
        this.mocks.put(fieldName, im);
    }

    /**
     * retrieve the mock for the given field or <em>null</em> if none present
     *
     * @param field the field name
     * @return the mock object or <em>null</em> if none is found
     */
    public Object getMock(String field) {
        return this.mocks.containsKey(field) ? this.mocks.get(field).mock : null;
    }

    /**
     * retrieve the mock for the given target class
     *
     * @param clazz the class of the field the mock was injected into
     * @param <T>   class parameter to get casted mock object
     * @return the mock for the given target class or <em>null</em> if none is found
     */
    public <T> T getMock(Class<T> clazz) {
        return this.getMock(clazz, null);
    }

    /**
     * retrieve the mock for the given target class
     *
     * @param clazz     the class of the field the mock was injected into
     * @param qualifier the qualifier of the field the mock was injected into
     * @param <T>       class parameter to get casted mock object
     * @return the mock for the given target class and qualifier or <em>null</em> if none is found
     */
    public <T> T getMock(Class<T> clazz, String qualifier) {
        for (InjectedMock mock : this.mocks.values()) {
            if (mock.targetType.isAssignableFrom(clazz) && (qualifier == null || qualifier.equals(mock.qualifier))) {
                return (T) mock.mock;
            }
        }
        return null;
    }

}
