package de.taimos.daemon.spring;

import java.util.Properties;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author thoeger
 */
public class RunnerConfig {

    private final Properties props = new Properties();


    /**
     * @param key   the prop key
     * @param value the prop value
     */
    protected void addProperty(final String key, final String value) {
        this.props.setProperty(key.trim(), value);
    }

    /**
     * @return the properties
     */
    public Properties getProps() {
        return this.props;
    }

    /**
     * @return the Spring file nme
     */
    public String getSpringFile() {
        return "spring-test/beans.xml";
    }

    public String getServicePackage() {
        return null;
    }

    protected static Integer randomPort() {
        return (int) ((Math.random() * 20000) + 10000);
    }
}
