package de.taimos.dvalin.interconnect.core.event;


import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a class,
 * that extends {@link de.taimos.dvalin.interconnect.model.service.IEventHandler} as an event handler,
 * to be picked up by {@link EventMessageListener}.
 *
 * @author psigloch
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
@Scope("prototype")
public @interface EventHandler {
    //
}
