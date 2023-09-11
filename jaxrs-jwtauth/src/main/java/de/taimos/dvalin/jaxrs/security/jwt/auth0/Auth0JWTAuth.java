/*
 * Copyright (c) 2019. Taimos GmbH http://www.taimos.de
 */

package de.taimos.dvalin.jaxrs.security.jwt.auth0;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PostConstruct;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import de.taimos.daemon.spring.conditional.OnSystemProperty;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.dvalin.jaxrs.security.jwt.IJWTAuth;
import de.taimos.dvalin.jaxrs.security.jwt.JWKSKeyLoader;
import de.taimos.httputils.HTTPResponse;
import de.taimos.httputils.WS;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;

@JaxRsComponent
@OnSystemProperty(propertyName = "jwtauth.auth0.issuer")
public class Auth0JWTAuth implements IJWTAuth {

	@Value("${jwtauth.auth0.issuer}")
	private String issuer;
	@Value("${jwtauth.auth0.audience}")
	private String audience;

	private LoadingCache<String, RSAKey> jwtKeyCache;

	@PostConstruct
	public void init() {
		this.jwtKeyCache = CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.HOURS).build(new JWKSKeyLoader(this.issuer));
	}

	@Override
	public Auth0User validateToken(String jwtString) throws ParseException {
        SignedJWT jwt = SignedJWT.parse(jwtString);

        if (!jwt.getJWTClaimsSet().getIssuer().equals(this.issuer)) {
            throw new IllegalArgumentException("Invalid issuer for JWT: " + jwt.getJWTClaimsSet().getIssuer());
        }
        if (!jwt.getJWTClaimsSet().getAudience().contains(this.audience)) {
            throw new IllegalArgumentException("Invalid audience for JWT: " + jwt.getJWTClaimsSet().getAudience());
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
                    return Auth0User.parseClaims(claims, jwtString);
                }
            }
            return null;
        } catch (JOSEException e) {
            throw new IllegalArgumentException("Cannot verify JWT", e);
        }
	}

	public Map<String, Object> retrieveUserProfile(Auth0User user) {
        try (HTTPResponse httpResponse = WS.url(this.issuer + "userinfo").authBearer(user.getToken()).retry().get()) {
            return (JSONObject) new JSONParser(JSONParser.MODE_PERMISSIVE).parse(httpResponse.getResponseAsBytes());
        } catch (net.minidev.json.parser.ParseException e) {
			throw new RuntimeException("Could not parse user info", e);
		}
	}

}
