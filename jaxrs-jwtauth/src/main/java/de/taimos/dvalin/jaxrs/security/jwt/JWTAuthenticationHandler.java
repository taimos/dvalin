package de.taimos.dvalin.jaxrs.security.jwt;

import java.text.ParseException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.apache.cxf.message.Message;
import org.apache.cxf.security.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;

import de.taimos.dvalin.daemon.conditional.BeanAvailable;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.dvalin.jaxrs.providers.AuthorizationProvider;

/**
 * Created by thoeger on 06.01.16.
 */
@JaxRsComponent
@BeanAvailable(JWTAuth.class)
public abstract class JWTAuthenticationHandler extends AuthorizationProvider {

    @Autowired
    private JWTAuth auth;

    @Override
    protected SecurityContext handleAuthHeader(ContainerRequestContext requestContext, Message msg, String type, String auth) {
        if (type == null || !type.equals("Bearer")) {
            return null;
        }
        try {
            return loginUser(msg, this.auth.validateToken(auth));
        } catch (ParseException e) {
            return null;
        }
    }


    @Override
    protected SecurityContext handleOther(ContainerRequestContext requestContext, Message msg, HttpHeaders head) {
        return null;
    }
}
