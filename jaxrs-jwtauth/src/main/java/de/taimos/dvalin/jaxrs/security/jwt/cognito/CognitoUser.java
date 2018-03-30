package de.taimos.dvalin.jaxrs.security.jwt.cognito;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.nimbusds.jwt.JWTClaimsSet;

import de.taimos.dvalin.jaxrs.security.IUser;

public class CognitoUser implements IUser {

    private String subject;

    private String email;
    private boolean emailVerified;

    private String username;
    private String[] roles;

    private Map<String, Object> customFields = new HashMap<>();

    /**
     * @return the subject of the JWT (Field: sub)
     */
    public String getSubject() {
        return this.subject;
    }

    /**
     * @return the email of the JWT (Field: email) - only with id token
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * @return the verification status of the email of the JWT (Field: email_verfied) - only with id token
     */
    public boolean isEmailVerified() {
        return this.emailVerified;
    }

    /**
     * @return all claims as map
     */
    public Map<String, Object> getCustomFields() {
        return this.customFields;
    }

    /**
     * @return the username (Field: cognito:username for id tokens; username for access tokens)
     */
    @Override
    public String getUsername() {
        return this.username;
    }

    /**
     * @return the roles (Field: cognito:groups by default)
     */
    @Override
    public String[] getRoles() {
        return this.roles;
    }

    @Override
    public String toString() {
        return "CognitoUser{" +
            "subject='" + this.subject + '\'' +
            ", email='" + this.email + '\'' +
            ", emailVerified=" + this.emailVerified +
            ", username='" + this.username + '\'' +
            ", roles=" + Arrays.toString(this.roles) +
            ", customFields=" + this.customFields +
            '}';
    }

    public static CognitoUser parseClaims(JWTClaimsSet claims, String rolesField) throws ParseException {
        CognitoUser user = new CognitoUser();
        user.subject = claims.getSubject();
        user.roles = claims.getStringArrayClaim(rolesField);
        user.customFields = claims.getClaims();

        String tokenUse = claims.getStringClaim("tokenUse");
        switch (tokenUse) {
        case "id":
            user.username = claims.getStringClaim("cognito:username");
            user.email = claims.getStringClaim("email");
            user.emailVerified = claims.getBooleanClaim("email_verified");
            break;
        case "access":
            user.username = claims.getStringClaim("username");
            break;
        default:
            throw new IllegalArgumentException("Invalid token use: " + tokenUse);
        }
        return user;
    }
}

