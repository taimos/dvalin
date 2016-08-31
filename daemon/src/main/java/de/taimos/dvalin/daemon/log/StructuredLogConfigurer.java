package de.taimos.dvalin.daemon.log;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.taimos.daemon.ILoggingConfigurer;
import de.taimos.daemon.log4j.JSONLayout;

public class StructuredLogConfigurer implements ILoggingConfigurer {
    
    private final Logger rlog = Logger.getRootLogger();
    private ConsoleAppender console;
    
    @Override
    public void initializeLogging() throws Exception {
        this.rlog.removeAllAppenders();
        this.rlog.setLevel(Level.INFO);
        
        this.console = new ConsoleAppender();
        this.console.setName("CONSOLE");
        this.console.setLayout(new JSONLayout());
        this.console.setTarget("System.out");
        this.console.activateOptions();
        this.rlog.addAppender(this.console);
    }
    
    @Override
    public void reconfigureLogging() throws Exception {
        this.rlog.setLevel(Level.toLevel(System.getProperty("logger.level"), Level.INFO));
    }
    
    @Override
    public void simpleLogging() throws Exception {
        this.initializeLogging();
        this.reconfigureLogging();
    }
    
    public static void setup() {
        System.setProperty("loggerConfigurer", StructuredLogConfigurer.class.getCanonicalName());
    }
}
