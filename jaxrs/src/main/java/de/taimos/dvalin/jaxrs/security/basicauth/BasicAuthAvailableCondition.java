package de.taimos.dvalin.jaxrs.security.basicauth;

import de.taimos.dvalin.daemon.AbstractBeanAvailableCondition;

public class BasicAuthAvailableCondition extends AbstractBeanAvailableCondition<IBasicAuthUserDAO> {

    @Override
    protected Class<IBasicAuthUserDAO> getBeanClass() {
        return IBasicAuthUserDAO.class;
    }

}
