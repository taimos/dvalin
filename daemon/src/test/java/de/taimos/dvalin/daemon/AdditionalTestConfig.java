package de.taimos.dvalin.daemon;

import de.taimos.daemon.spring.SpringDaemonTestRunner.TestConfiguration;

import java.util.Properties;

/**
 * Copyright 2023 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class AdditionalTestConfig implements TestConfiguration {

    static final String TEST_VALUE = "JUnitTest";

    @Override
    public Properties getProps() {
        Properties props = new Properties();
        props.put("jwtauth.issuer", AdditionalTestConfig.TEST_VALUE);
        return props;
    }
}
