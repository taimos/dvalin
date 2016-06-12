package de.taimos.dvalin.monitoring;

import de.taimos.daemon.spring.annotations.TestComponent;
import de.taimos.dvalin.monitoring.aspects.annotations.ExecutionTime;

@TestComponent
public class TestBean {

    @ExecutionTime(namespace = "Method/Execution", metric = "doSomething")
    public void doSomething() {
        System.out.println("Did something");
    }

    @ExecutionTime(namespace = "Method/Execution", metric = "doSomething", serviceNameDimension = true)
    public void doSomethingWithDimensions() {
        System.out.println("Did something other");
    }

}
