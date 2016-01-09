package de.taimos.dvalin.jaxrs.jwtauth;

import java.text.ParseException;
import java.util.Date;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.apache.cxf.message.Message;
import org.apache.cxf.security.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import de.taimos.dvalin.jaxrs.providers.AuthorizationProvider;

/**
 * Created by thoeger on 06.01.16.
 */
public abstract class JWTAuthenticationHandler extends AuthorizationProvider {

    @Autowired
    private JWTAuthConfig authConfig;

    @Override
    protected SecurityContext handleAuthHeader(ContainerRequestContext requestContext, Message msg, String type, String auth) {
        if (type == null || !type.equals("Bearer")) {
            return null;
        }
        try {
            final SignedJWT jwt = authConfig.verifyToken(auth);
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            if (claims.getExpirationTime().before(new Date())) {
                return null;
            }

            AuthenticatedUser user = new AuthenticatedUser(claims);
            msg.put(AuthenticatedUser.class, user);
            return createSC(user.getUsername(), user.getRoles().toArray(new String[0]));
        } catch (ParseException e) {
            return null;
        }
    }


    @Override
    protected SecurityContext handleOther(ContainerRequestContext requestContext, Message msg, HttpHeaders head) {
        return null;
    }
}
