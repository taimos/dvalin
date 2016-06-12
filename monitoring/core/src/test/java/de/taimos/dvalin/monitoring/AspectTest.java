package de.taimos.dvalin.monitoring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import de.taimos.daemon.log4j.Log4jLoggingConfigurer;
import de.taimos.daemon.spring.SpringDaemonTestRunner;
import de.taimos.dvalin.daemon.DvalinTestRunnerConfig;

@RunWith(SpringDaemonTestRunner.class)
@SpringDaemonTestRunner.RunnerConfiguration(config = DvalinTestRunnerConfig.class, loggingConfigurer = Log4jLoggingConfigurer.class, svc = "AspectTest")
public class AspectTest {

    @Autowired
    private TestBean bean;

    @Test
    public void timeTest() throws Exception {
        this.bean.doSomething();
    }

    @Test
    public void timeTestWithDimension() throws Exception {
        this.bean.doSomethingWithDimensions();
    }
}
