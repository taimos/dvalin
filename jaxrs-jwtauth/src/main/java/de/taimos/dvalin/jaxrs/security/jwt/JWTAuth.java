package de.taimos.dvalin.jaxrs.security.jwt;

import java.text.ParseException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import de.taimos.daemon.spring.conditional.OnSystemProperty;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.dvalin.jaxrs.security.SecurityContextUtil;

/**
 * Created by thoeger on 06.01.16.
 */
@JaxRsComponent
@OnSystemProperty(propertyName = "jwtauth.secret")
public class JWTAuth {

    @Value("${jwtauth.secret}")
    private String jwtSharedSecret;

    @Value("${jwtauth.timeout:3600000}")
    private Long jwtTimeout;

    @Value("${jwtauth.issuer}")
    private String jwtIssuer;


    /**
     * Sign the given claims
     *
     * @param claims the claims to sign
     * @return the created and signed JSON Web Token
     */
    public SignedJWT signToken(JWTClaimsSet claims) {
        try {
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            signedJWT.sign(new MACSigner(this.jwtSharedSecret));
            return signedJWT;
        } catch (JOSEException e) {
            throw new RuntimeException("Error signing JSON Web Token", e);
        }
    }

    /**
     * Sign the given claims
     *
     * @param user the user to create the claims from
     * @return the created and signed JSON Web Token as string
     */
    public String signToken(AuthenticatedUser user) {
        Date expiry = new Date(System.currentTimeMillis() + this.jwtTimeout);
        JWTClaimsSet claimsSet = user.toClaimSet(this.jwtIssuer, expiry);
        SignedJWT jwt = this.signToken(claimsSet);
        return jwt.serialize();
    }

    /**
     * Check the given JWT
     *
     * @param jwtString the JSON Web Token
     * @return the parsed and verified token or null if token is invalid
     * @throws ParseException if the token cannot be parsed
     */
    public SignedJWT verifyToken(String jwtString) throws ParseException {
        try {
            SignedJWT jwt = SignedJWT.parse(jwtString);
            if (jwt.verify(new MACVerifier(this.jwtSharedSecret))) {
                return jwt;
            }
            return null;
        } catch (JOSEException e) {
            throw new RuntimeException("Error verifying JSON Web Token", e);
        }
    }

    /**
     * Check the given JWT and parse it into a user object
     *
     * @param jwtString the JSON Web Token
     * @return the parsed and verified token or null if token is invalid or expired
     * @throws ParseException if the token cannot be parsed
     */
    public AuthenticatedUser validateToken(String jwtString) throws ParseException {
        final SignedJWT jwt = this.verifyToken(jwtString);
        JWTClaimsSet claims = jwt.getJWTClaimsSet();
        if (claims.getExpirationTime().before(new Date())) {
            return null;
        }
        return new AuthenticatedUser(claims);
    }

	/**
     * Get the currently logged in user
     *
     * @return the user or null if not available
     */
    public AuthenticatedUser getCurrentUser() {
        return (AuthenticatedUser) SecurityContextUtil.getUserObject();
    }


}
