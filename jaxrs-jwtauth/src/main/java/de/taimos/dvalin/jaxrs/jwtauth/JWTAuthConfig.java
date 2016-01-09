package de.taimos.dvalin.jaxrs.jwtauth;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Value;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

/**
 * Created by thoeger on 06.01.16.
 */
public class JWTAuthConfig {

    @Value("${jwtauth.secret}")
    private String jwtSharedSecret;


    public String getJwtSharedSecret() {
        return jwtSharedSecret;
    }

    /**
     * Sign the given claims
     *
     * @param claims the claims to sign
     * @return the created and signed JSON Web Token
     */
    public SignedJWT signToken(JWTClaimsSet claims) {
        try {
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            signedJWT.sign(new MACSigner(jwtSharedSecret));
            return signedJWT;
        } catch (JOSEException e) {
            throw new RuntimeException("Error signing JSON Web Token", e);
        }
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
            if (jwt.verify(new MACVerifier(jwtSharedSecret))) {
                return jwt;
            }
            return null;
        } catch (JOSEException e) {
            throw new RuntimeException("Error verifying JSON Web Token", e);
        }
    }

}
