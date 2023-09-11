package de.taimos.dvalin.daemon;

import de.taimos.daemon.spring.RunnerConfig;

/**
 * Copyright 2023 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
@SuppressWarnings("javadoc")
public class TestConfig extends RunnerConfig {

    public TestConfig() {
        System.setProperty("demo", "is here");
        this.addProperty("demo", "is here");
    }

    @Override
    public String getSpringFile() {
        return "spring/dvalin.xml";
    }
}
