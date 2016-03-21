package de.taimos.dvalin.jaxrs.security.token;

import de.taimos.dvalin.daemon.AbstractBeanAvailableCondition;

public class TokenAuthAvailableCondition extends AbstractBeanAvailableCondition<ITokenAuthUserDAO> {

    @Override
    protected Class<ITokenAuthUserDAO> getBeanClass() {
        return ITokenAuthUserDAO.class;
    }

}
