package de.taimos.dvalin.test.jaxrs;

import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.taimos.daemon.log4j.Log4jLoggingConfigurer;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JAXRSUtils.class})
public class AbstractJaxRsPowermockTest {

    @BeforeClass
    public static void init() throws Exception {
        new Log4jLoggingConfigurer().simpleLogging();
    }
    

}
