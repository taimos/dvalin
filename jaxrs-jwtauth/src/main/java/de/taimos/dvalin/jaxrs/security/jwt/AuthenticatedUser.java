package de.taimos.dvalin.jaxrs.security.jwt;

/*-
 * #%L
 * JWT Authetication support for dvalin JAX-RS
 * %%
 * Copyright (C) 2016 - 2017 Taimos GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.text.ParseException;
import java.util.Date;

import com.nimbusds.jwt.JWTClaimsSet;

import de.taimos.dvalin.jaxrs.security.IUser;

/**
 * Created by thoeger on 06.01.16.
 */
public final class AuthenticatedUser implements IUser {

    private static final String CLAIM_USERNAME = "preferred_username";
    private static final String CLAIM_DISPLAY_NAME = "name";
    private static final String CLAIM_ROLES = "roles";

    private String id;
    private String username;
    private String displayName;
    private String[] roles;

    public AuthenticatedUser() {
        //
    }

    public AuthenticatedUser(JWTClaimsSet claims) {
        try {
            this.setId(claims.getSubject());
            this.setUsername(claims.getStringClaim(CLAIM_USERNAME));
            this.setRoles(claims.getStringArrayClaim(CLAIM_ROLES));
            this.setDisplayName(claims.getStringClaim(CLAIM_DISPLAY_NAME));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String[] getRoles() {
        return this.roles.clone();
    }

    public void setRoles(String[] roles) {
        this.roles = roles.clone();
    }

    public JWTClaimsSet toClaimSet(String issuer, Date expiry) {
        JWTClaimsSet.Builder b = new JWTClaimsSet.Builder();
        b.issuer(issuer);
        b.expirationTime(expiry);
        b.subject(this.id);
        b.claim(CLAIM_USERNAME, this.username);
        b.claim(CLAIM_DISPLAY_NAME, this.displayName);
        b.claim(CLAIM_ROLES, this.roles);
        return b.build();
    }
}
