package de.taimos.dvalin.jaxrs.security.jwt.cognito;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.http.HttpResponse;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Value;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import de.taimos.daemon.spring.conditional.OnSystemProperty;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.dvalin.jaxrs.security.jwt.IJWTAuth;
import de.taimos.httputils.WS;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

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
    
    private final Map<String, RSAKey> webKeys = new HashMap<>();
    
    @PostConstruct
    public void init() {
        try {
            this.issuer = "https://cognito-idp." + this.cognitoPoolRegion + ".amazonaws.com/" + this.cognitoPoolId;
            HttpResponse httpResponse = WS.url(this.issuer + "/.well-known/jwks.json").accept("application/json").get();
            JSONObject jwksBody = (JSONObject) new JSONParser(JSONParser.MODE_PERMISSIVE).parse(WS.getResponseAsBytes(httpResponse));
            
            JSONArray keys = (JSONArray) jwksBody.get("keys");
            
            for (Object key : keys) {
                RSAKey jwk = RSAKey.parse((JSONObject) key);
                this.webKeys.put(jwk.getKeyID(), jwk);
            }
        } catch (net.minidev.json.parser.ParseException | ParseException e) {
            throw new BeanInitializationException("Cannot load secrets from WS Cognito User Pool", e);
        }
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
        if (!this.webKeys.containsKey(kid)) {
            throw new IllegalArgumentException("No key for kid: " + kid);
        }
        
        try {
            if (jwt.verify(new RSASSAVerifier(this.webKeys.get(kid)))) {
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
