package de.taimos.dvalin.daemon.log;

/*-
 * #%L
 * Daemon support for dvalin
 * %%
 * Copyright (C) 2015 - 2017 Taimos GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.MDC;

public class DvalinLogger {
    
    private Logger logger;
    private Map<String, String> context = new HashMap<>();
    
    private DvalinLogger(Logger logger) {
        this.logger = logger;
    }
    
    public DvalinLogger with(String key, String value) {
        this.context.put(key, value);
        return this;
    }
    
    public DvalinLogger with(Map<String, String> context) {
        this.context.putAll(context);
        return this;
    }
    
    public void info(String message, Object... args) {
        this.doLog(() -> this.logger.info(message, args));
    }
    
    public void warn(String message, Object... args) {
        this.doLog(() -> this.logger.warn(message, args));
    }
    
    public void error(String message, Object... args) {
        this.doLog(() -> this.logger.error(message, args));
    }
    
    public void info(String message, Throwable t) {
        this.doLog(() -> this.logger.info(message, t));
    }
    
    public void warn(String message, Throwable t) {
        this.doLog(() -> this.logger.warn(message, t));
    }
    
    public void error(String message, Throwable t) {
        this.doLog(() -> this.logger.error(message, t));
    }
    
    public void doLog(LogInvoker invoker) {
        try {
            for (Map.Entry<String, String> entry : this.context.entrySet()) {
                MDC.put(entry.getKey(), entry.getValue());
            }
            invoker.invoke();
        } finally {
            this.context.keySet().forEach(MDC::remove);
        }
    }
    
    public static DvalinLogger using(Logger logger) {
        return new DvalinLogger(logger);
    }
    
    @FunctionalInterface
    private interface LogInvoker {
        void invoke();
    }
    
}
