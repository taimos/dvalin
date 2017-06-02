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
