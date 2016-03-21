package de.taimos.dvalin.daemon.conditional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Conditional;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Conditional(SystemPropertyCondition.class)
public @interface OnSystemProperty {

    String propertyName();

    String propertyValue() default "";

}
