package de.taimos.dvalin.monitoring.aspects.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExecutionTime {

    /**
     * @return the metric's namespace
     */
    String namespace();

    /**
     * @return the metric's name
     */
    String metric();

    /**
     * @return true to add the service name as a dimension (defaults to <i>false</i>)
     */
    boolean serviceNameDimension() default false;

}
