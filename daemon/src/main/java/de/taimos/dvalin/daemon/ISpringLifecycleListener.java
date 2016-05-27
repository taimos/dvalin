package de.taimos.dvalin.daemon;

public interface ISpringLifecycleListener {

    void afterContextStart();

    void started();

    void stopping();

    void beforeContextStop();

    void aborting();

    void signalUSR2();

}
