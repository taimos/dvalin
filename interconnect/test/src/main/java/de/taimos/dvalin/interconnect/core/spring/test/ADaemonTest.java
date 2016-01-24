package de.taimos.dvalin.interconnect.core.spring.test;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.taimos.dvalin.interconnect.model.service.ADaemonHandler;


/**
 * @param <H> Daemon handler
 */
public class ADaemonTest<H extends ADaemonHandler> {

    @Autowired
    private BeanFactory beanFactory;

    /**
     * @return Daemon handler
     */
    public final H handler() {
        return (H) this.beanFactory.getBean("requestHandler");
    }

}
