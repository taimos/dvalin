package de.taimos.dvalin.test.inject;

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
