package de.taimos.dvalin.jaxrs.security.jwt;

import java.text.ParseException;

import de.taimos.dvalin.jaxrs.security.IUser;

public interface IJWTAuth {
    
    /**
     * Check the given JWT and parse it into a user object
     *
     * @param jwtString the JSON Web Token
     * @return the user or null if token is invalid or expired
     * @throws ParseException if the token cannot be parsed
     */
    IUser validateToken(String jwtString) throws ParseException;
    
}
