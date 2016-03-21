package de.taimos.dvalin.jaxrs.security.basicauth;

import javax.annotation.PostConstruct;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.codec.binary.Base64;
import org.apache.cxf.message.Message;
import org.apache.cxf.security.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;

import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.dvalin.jaxrs.providers.AuthorizationProvider;
import de.taimos.dvalin.jaxrs.security.IUser;

@JaxRsComponent
@Conditional(BasicAuthAvailableCondition.class)
public class BasicAuthFilter extends AuthorizationProvider {

    @Autowired
    private IBasicAuthUserDAO basicAuthUserDAO;

    @Override
    protected boolean isAuthorizationMandatory() {
        return false;
    }

    @Override
    protected SecurityContext handleAuthHeader(ContainerRequestContext requestContext, Message msg, String type, String auth) {
        if (auth != null && type.toLowerCase().equals("basic")) {
            String decoded = new String(Base64.decodeBase64(auth));
            if (decoded == null || !decoded.contains(":")) {
                return null;
            }
            String username = decoded.substring(0, decoded.indexOf(":"));
            String pwd = decoded.substring(decoded.indexOf(":") + 1);

            IUser user = basicAuthUserDAO.getUserByNameAndPassword(username, pwd);
            if (user == null) {
                return null;
            }
            msg.put(IUser.class, user);
            return createSC(user.getUsername(), user.getRoles());
        }
        return null;
    }

    @Override
    protected SecurityContext handleOther(ContainerRequestContext requestContext, Message msg, HttpHeaders head) {
        return null;
    }
}
