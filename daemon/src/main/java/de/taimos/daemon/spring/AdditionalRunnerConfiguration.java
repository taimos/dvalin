package de.taimos.daemon.spring;

/**
 * Copyright 2023 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Properties;

/**
 * Copyright 2023 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AdditionalRunnerConfiguration {
    /**
     * @return the class
     */
    Class<? extends TestConfiguration>[] config() default {};

    /**
     * Copyright 2023 Cinovo AG<br>
     * <br>
     *
     * @author fzwirn
     */
    public interface TestConfiguration {
        /**
         * @return the properties for this runner configuration
         */
        Properties getProps();
    }
}
