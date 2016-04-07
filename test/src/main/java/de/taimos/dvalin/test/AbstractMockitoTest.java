package de.taimos.dvalin.test;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import de.taimos.daemon.log4j.Log4jLoggingConfigurer;

@RunWith(MockitoJUnitRunner.class)
public class AbstractMockitoTest {

    @BeforeClass
    public static void init() throws Exception {
        new Log4jLoggingConfigurer().simpleLogging();
    }

}
