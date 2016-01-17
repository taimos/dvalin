package de.taimos.dvalin.interconnect.demo;

import java.util.Map;

import de.taimos.daemon.DaemonStarter;
import de.taimos.daemon.log4j.Log4jLoggingConfigurer;
import de.taimos.daemon.spring.SpringDaemonAdapter;
import de.taimos.dvalin.interconnect.core.MessageConnector;

/**
 * Created by thoeger on 17.01.16.
 */
public class Starter extends SpringDaemonAdapter {

    public static void main(String[] args) {
        Log4jLoggingConfigurer.setup();
        DaemonStarter.startDaemon("user-service", new Starter());
    }

    @Override
    protected void loadBasicProperties(Map<String, String> map) {
        map.put(MessageConnector.SYSPROP_IBROKERURL, "failover:tcp://localhost:61616");
    }
}
