package de.taimos.dvalin.daemon;

import de.taimos.daemon.spring.SpringDaemonTestRunner;


/**
 *
 */
public class DvalinTestRunnerConfig extends SpringDaemonTestRunner.RunnerConfig {

    @Override
    public String getSpringFile() {
        return "spring/dvalin.xml";
    }
}
