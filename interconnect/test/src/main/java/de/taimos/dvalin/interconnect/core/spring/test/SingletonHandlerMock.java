package de.taimos.dvalin.interconnect.core.spring.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Scope;

import de.taimos.daemon.spring.annotations.TestComponent;

/**
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@TestComponent
@Scope("singleton")
public @interface SingletonHandlerMock {

    /**
     * The value may indicate a suggestion for a logical component name, to be turned into a Spring bean in case of an autodetected
     * component.
     *
     * @return the suggested component name, if any
     */
    String value() default "";
}
