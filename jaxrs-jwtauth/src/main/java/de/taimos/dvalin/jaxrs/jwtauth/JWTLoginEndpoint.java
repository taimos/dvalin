package de.taimos.dvalin.jaxrs.jwtauth;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.MessageContextImpl;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import de.taimos.dvalin.jaxrs.AbstractAPI;
import de.taimos.restutils.RESTAssert;

/**
 * Created by thoeger on 07.01.16.
 */
public abstract class JWTLoginEndpoint extends AbstractAPI {

    public static class LoginData {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    @Autowired
    private ExtendedSecurityContextBean extSecurity;

    @Autowired
    private JWTAuthConfig authConfig;

    @Value("${jwtauth.timeout}")
    private Long jwtTimeout;

    @Value("${jwtauth.issuer}")
    private String jwtIssuer;

    @GET
    @Path("/currentUser")
    @Produces(MediaType.APPLICATION_JSON)
    public AuthenticatedUser getCurrentUser() {
        AuthenticatedUser user = extSecurity.getLoggedIn();
        if (user == null) {
            throw new NotAuthorizedException();
        }
        return user;
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String login(LoginData data) {
        RESTAssert.assertNotNull(data);
        RESTAssert.assertNotEmpty(data.getUsername());
        RESTAssert.assertNotEmpty(data.getPassword());

        AuthenticatedUser user = authenticate(data.getUsername(), data.getPassword());

        Date expiry = new Date(System.currentTimeMillis() + jwtTimeout);
        JWTClaimsSet claimsSet = user.toClaimSet(jwtIssuer, expiry);
        SignedJWT jwt = authConfig.signToken(claimsSet);
        return jwt.serialize();
    }

    protected abstract AuthenticatedUser authenticate(String username, String password);

}
