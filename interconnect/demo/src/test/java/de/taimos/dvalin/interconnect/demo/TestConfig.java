package de.taimos.dvalin.interconnect.demo;

import de.taimos.daemon.spring.SpringDaemonTestRunner;

/**
 * Created by thoeger on 17.01.16.
 */
public class TestConfig extends SpringDaemonTestRunner.RunnerConfig {

    @Override
    public String getServicePackage() {
        return TestConfig.class.getPackage().getName();
    }

    @Override
    public String getSpringFile() {
        return "spring/beans.xml";
    }
}
