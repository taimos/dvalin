package de.taimos.testservice;

import org.junit.runner.RunWith;

import de.taimos.daemon.log4j.Log4jLoggingConfigurer;
import de.taimos.daemon.spring.SpringDaemonTestRunner;

@RunWith(SpringDaemonTestRunner.class)
@SpringDaemonTestRunner.RunnerConfiguration(svc = "testservice", config = TestConfig.class, loggingConfigurer = Log4jLoggingConfigurer.class)
public abstract class SpringTest {
	//
}
