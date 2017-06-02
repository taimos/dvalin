package de.taimos.dvalin.daemon;

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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.context.ApplicationContext;

import de.taimos.daemon.DaemonLifecycleAdapter;
import de.taimos.daemon.DaemonProperties;
import de.taimos.daemon.DaemonStarter;
import de.taimos.daemon.log4j.Log4jLoggingConfigurer;
import de.taimos.daemon.properties.FilePropertyProvider;
import de.taimos.daemon.properties.IPropertyProvider;
import de.taimos.daemon.spring.SpringDaemonAdapter;

/**
 * Basic {@link DaemonLifecycleAdapter} preconfigured for dvalin
 */
public abstract class DvalinLifecycleAdapter extends SpringDaemonAdapter {

    public static void start(String serviceName, DvalinLifecycleAdapter lifecycleAdapter) {
        lifecycleAdapter.setupLogging();
        DaemonStarter.startDaemon(serviceName, lifecycleAdapter);
    }

    @Override
    protected void loadBasicProperties(Map<String, String> map) {
        super.loadBasicProperties(map);
        map.put(DaemonProperties.DNS_TTL, "60");
    }

    @Override
    public IPropertyProvider getPropertyProvider() {
        if (EnvPropertyProvider.isConfigured()) {
            return new EnvPropertyProvider();
        }
        return new FilePropertyProvider("dvalin.properties");
    }

    @Override
    protected String getSpringResource() {
        return "spring/dvalin.xml";
    }

    @Override
    protected void doAfterSpringStart() {
        for (ISpringLifecycleListener listener : this.getLifecycleListeners()) {
            listener.afterContextStart();
        }
        super.doAfterSpringStart();
    }

    @Override
    protected void doBeforeSpringStop() {
        for (ISpringLifecycleListener listener : this.getLifecycleListeners()) {
            listener.beforeContextStop();
        }
        super.doBeforeSpringStop();
    }

    @Override
    public void started() {
        for (ISpringLifecycleListener listener : this.getLifecycleListeners()) {
            listener.started();
        }
        super.started();
    }

    @Override
    public void stopping() {
        for (ISpringLifecycleListener listener : this.getLifecycleListeners()) {
            listener.stopping();
        }
        super.stopping();
    }

    @Override
    public void aborting() {
        for (ISpringLifecycleListener listener : this.getLifecycleListeners()) {
            listener.aborting();
        }
        super.aborting();
    }

    @Override
    public void signalUSR2() {
        for (ISpringLifecycleListener listener : this.getLifecycleListeners()) {
            listener.signalUSR2();
        }
        super.signalUSR2();
    }
    
    protected void setupLogging() {
        Log4jLoggingConfigurer.setup();
    }

    private Collection<ISpringLifecycleListener> getLifecycleListeners() {
        ApplicationContext context = this.getContext();
        if (context != null) {
            Map<String, ISpringLifecycleListener> map = context.getBeansOfType(ISpringLifecycleListener.class);
            if (map != null) {
                return map.values();
            }
        }
        return Collections.EMPTY_LIST;
    }
}
