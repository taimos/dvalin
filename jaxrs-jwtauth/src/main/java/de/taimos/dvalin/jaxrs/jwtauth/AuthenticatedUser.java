package de.taimos.dvalin.jaxrs.jwtauth;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.nimbusds.jwt.JWTClaimsSet;

/**
 * Created by thoeger on 06.01.16.
 */
public class AuthenticatedUser {

    private static final String CLAIM_USERNAME = "preferred_username";
    private static final String CLAIM_DISPLAY_NAME = "name";
    private static final String CLAIM_ROLES = "roles";

    private String id;
    private String username;
    private String displayName;
    private Set<String> roles;
    private String session;

    public AuthenticatedUser() {

    }

    public AuthenticatedUser(JWTClaimsSet claims) {
        try {
            this.setId(claims.getSubject());
            this.setUsername(claims.getStringClaim(CLAIM_USERNAME));
            this.setRoles(new HashSet<String>(claims.getStringListClaim(CLAIM_ROLES)));
            this.setDisplayName(claims.getStringClaim(CLAIM_DISPLAY_NAME));
            this.setSession(claims.getJWTID());
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public JWTClaimsSet toClaimSet(String issuer, Date expiry) {
        JWTClaimsSet.Builder b = new JWTClaimsSet.Builder();
        b.issuer(issuer);
        b.expirationTime(expiry);
        b.jwtID(session);
        b.subject(id);
        b.claim(CLAIM_USERNAME, username);
        b.claim(CLAIM_DISPLAY_NAME, displayName);
        b.claim(CLAIM_ROLES, roles);
        return b.build();
    }
}
