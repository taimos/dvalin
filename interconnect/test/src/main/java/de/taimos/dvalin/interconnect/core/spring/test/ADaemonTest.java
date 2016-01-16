package de.taimos.dvalin.interconnect.core.spring.test;

import java.util.UUID;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Preconditions;

import de.taimos.dvalin.interconnect.model.ivo.IVO;
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
        return this.handler(UUID.randomUUID());
    }

    /**
     * @param uuid UUID
     * @return Daemon handler
     */
    public final H handler(final UUID uuid) {
        Preconditions.checkNotNull(uuid);
        ADaemonHandler.Context context = new ADaemonHandler.Context(IVO.class, uuid, 1, false);
        return (H) this.beanFactory.getBean("requestHandler", context);
    }

}
