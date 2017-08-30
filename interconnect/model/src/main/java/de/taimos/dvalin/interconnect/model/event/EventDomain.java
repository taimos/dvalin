package de.taimos.dvalin.interconnect.model.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The @EventDomain annotation is used with interfaces that extend {@link IEvent} to provide information that clients can interact with that
 * domain.<br>
 * This information are:<br>
 * - The name of the JMS topic ${name}<br>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EventDomain {
    /**
     * @return domain name
     */
    String name();
}
