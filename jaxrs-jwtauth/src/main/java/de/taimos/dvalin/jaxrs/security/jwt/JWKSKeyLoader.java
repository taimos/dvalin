package de.taimos.dvalin.jaxrs.security.jwt;

import jakarta.annotation.Nonnull;

import com.google.common.cache.CacheLoader;
import com.nimbusds.jose.jwk.RSAKey;

import de.taimos.httputils.HTTPResponse;
import de.taimos.httputils.WS;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

public class JWKSKeyLoader extends CacheLoader<String, RSAKey> {

    private final String issuer;

    public JWKSKeyLoader(String issuer) {
        this.issuer = issuer;
    }

    @Override
    public RSAKey load(@Nonnull String keyId) throws Exception {
        JSONObject jwksBody;
        try (HTTPResponse httpResponse = WS.url(this.issuer + "/.well-known/jwks.json").accept("application/json").retry().get()) {
            jwksBody = (JSONObject) new JSONParser(JSONParser.MODE_PERMISSIVE).parse(httpResponse.getResponseAsBytes());
        }

        JSONArray keys = (JSONArray) jwksBody.get("keys");
        for (Object key : keys) {
            RSAKey jwk = RSAKey.parse((JSONObject) key);
            if (jwk.getKeyID().equals(keyId)) {
                return jwk;
            }
        }
        throw new RuntimeException("Key not found");
    }
}
