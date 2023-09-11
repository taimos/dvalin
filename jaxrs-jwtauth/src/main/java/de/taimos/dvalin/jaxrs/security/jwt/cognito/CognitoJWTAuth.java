package de.taimos.dvalin.jaxrs.security.jwt.cognito;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import de.taimos.daemon.spring.conditional.OnSystemProperty;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.dvalin.jaxrs.security.jwt.JWKSKeyLoader;
import de.taimos.dvalin.jaxrs.security.jwt.IJWTAuth;

@JaxRsComponent
@OnSystemProperty(propertyName = "jwtauth.cognito.poolid")
public class CognitoJWTAuth implements IJWTAuth {

    @Value("${jwtauth.cognito.poolid}")
    private String cognitoPoolId;

    @Value("${jwtauth.cognito.region}")
    private String cognitoPoolRegion;

    @Value("${jwtauth.cognito.roles:cognito:groups}")
    private String cognitoRoles;

    private String issuer;

    private LoadingCache<String, RSAKey> jwtKeyCache;

    @PostConstruct
    public void init() {
        this.issuer = "https://cognito-idp." + this.cognitoPoolRegion + ".amazonaws.com/" + this.cognitoPoolId;
        this.jwtKeyCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build(new JWKSKeyLoader(this.issuer));
    }

    @Override
    public CognitoUser validateToken(String jwtString) throws ParseException {
        SignedJWT jwt = SignedJWT.parse(jwtString);

        if (!jwt.getJWTClaimsSet().getIssuer().equals(this.issuer)) {
            throw new IllegalArgumentException("Invalid issuer for JWT: " + jwt.getJWTClaimsSet().getIssuer());
        }

        String tokenUse = jwt.getJWTClaimsSet().getStringClaim("token_use");
        if (!tokenUse.equals("access") && !tokenUse.equals("id")) {
            throw new IllegalArgumentException("Invalid token usage type: " + tokenUse);
        }

        String kid = jwt.getHeader().getKeyID();
        RSAKey key = this.jwtKeyCache.getUnchecked(kid);
        if (key == null) {
            throw new IllegalArgumentException("No key for kid: " + kid);
        }

        try {
            if (jwt.verify(new RSASSAVerifier(key))) {
                JWTClaimsSet claims = jwt.getJWTClaimsSet();
                if (!claims.getExpirationTime().before(new Date())) {
                    return CognitoUser.parseClaims(claims, this.cognitoRoles);
                }
            }
            return null;
        } catch (JOSEException e) {
            throw new IllegalArgumentException("Cannot verify JWT", e);
        }
    }

}
