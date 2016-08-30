package de.taimos.dvalin.daemon.log;

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
