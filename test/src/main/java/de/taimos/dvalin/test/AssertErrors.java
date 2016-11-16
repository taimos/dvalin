package de.taimos.dvalin.test;

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
