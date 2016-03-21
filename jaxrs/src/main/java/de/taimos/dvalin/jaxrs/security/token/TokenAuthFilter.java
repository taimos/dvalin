package de.taimos.dvalin.jaxrs.security.token;

import javax.annotation.PostConstruct;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.apache.cxf.message.Message;
import org.apache.cxf.security.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;

import de.taimos.dvalin.daemon.conditional.BeanAvailable;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.dvalin.jaxrs.providers.AuthorizationProvider;
import de.taimos.dvalin.jaxrs.security.IUser;

@JaxRsComponent
@BeanAvailable(ITokenAuthUserDAO.class)
public class TokenAuthFilter extends AuthorizationProvider {

    @Autowired
    private ITokenAuthUserDAO tokenAuthUserDAO;

    @Override
    protected boolean isAuthorizationMandatory() {
        return false;
    }

    @Override
    protected SecurityContext handleAuthHeader(ContainerRequestContext requestContext, Message msg, String type, String auth) {
        if (auth != null && type.toLowerCase().equals("token")) {
            IUser userWithToken = this.tokenAuthUserDAO.getUserByToken(auth);
            if (userWithToken == null) {
                return null;
            }
            msg.put(IUser.class, userWithToken);
            return createSC(userWithToken.getUsername(), userWithToken.getRoles());
        }
        return null;
    }

    @Override
    protected SecurityContext handleOther(ContainerRequestContext requestContext, Message msg, HttpHeaders head) {
        return null;
    }
}
