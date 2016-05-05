package de.taimos.dvalin.daemon;

import de.taimos.daemon.DaemonLifecycleAdapter;
import de.taimos.daemon.DaemonProperties;
import de.taimos.daemon.DaemonStarter;
import de.taimos.daemon.log4j.Log4jLoggingConfigurer;
import de.taimos.daemon.properties.FilePropertyProvider;
import de.taimos.daemon.properties.IPropertyProvider;
import de.taimos.daemon.spring.SpringDaemonAdapter;

import java.util.Map;


/**
 * Basic {@link DaemonLifecycleAdapter} preconfigured for dvalin
 */
public abstract class DvalinLifecycleAdapter extends SpringDaemonAdapter{

    public static void start(String serviceName, DaemonLifecycleAdapter lifecycleAdapter) {
        Log4jLoggingConfigurer.setup();
        DaemonStarter.startDaemon(serviceName, lifecycleAdapter);
    }

    @Override
    protected void loadBasicProperties(Map<String, String> map) {
        super.loadBasicProperties(map);
        map.put(DaemonProperties.DNS_TTL, "60");
    }

    @Override
    public IPropertyProvider getPropertyProvider() {
        return new FilePropertyProvider("dvalin.properties");
    }


    @Override
    protected String getSpringResource() {
        return "spring/dvalin.xml";
    }
}
