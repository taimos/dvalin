package de.taimos.dvalin.jaxrs.security.token;

import de.taimos.dvalin.jaxrs.security.IUser;

public interface ITokenAuthUserDAO {

    IUser getUserByToken(String token);

}
