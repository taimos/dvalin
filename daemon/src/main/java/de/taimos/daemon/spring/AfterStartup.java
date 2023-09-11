package de.taimos.daemon.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Copyright 2014 Taimos GmbH<br>
 * <br>
 *
 * @author thoeger
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterStartup {
    //
}
