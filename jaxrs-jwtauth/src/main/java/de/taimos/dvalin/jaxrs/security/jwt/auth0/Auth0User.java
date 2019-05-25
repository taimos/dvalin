/*
 * Copyright (c) 2019. Taimos GmbH http://www.taimos.de
 */

package de.taimos.dvalin.jaxrs.security.jwt.auth0;

import java.util.HashMap;
import java.util.Map;

import com.nimbusds.jwt.JWTClaimsSet;

import de.taimos.dvalin.jaxrs.security.IUser;

public class Auth0User implements IUser {

	private String subject;
	private String token;
    private Map<String, Object> customFields = new HashMap<>();

	@Override
	public String getUsername() {
		return this.subject;
	}

	public String getSubject() {
		return this.subject;
	}

	@Override
	public String[] getRoles() {
		return new String[0];
	}

	public String getToken() {
		return this.token;
	}

    public Map<String, Object> getCustomFields() {
        return this.customFields;
    }

	public static Auth0User parseClaims(JWTClaimsSet claims, String token) {
		Auth0User user = new Auth0User();
		user.subject = claims.getSubject();
		user.token = token;
		user.customFields = claims.getClaims();
		return user;
	}
}
