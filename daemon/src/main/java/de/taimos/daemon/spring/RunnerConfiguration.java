package de.taimos.daemon.spring;

import de.taimos.daemon.ILoggingConfigurer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Copyright 2023 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RunnerConfiguration {

    /**
     * @return the class
     */
    Class<? extends RunnerConfig> config() default RunnerConfig.class;

    /**
     * @return the service name
     */
    String svc();

    /**
     * @return the logging configurer
     */
    Class<? extends ILoggingConfigurer> loggingConfigurer();
}
