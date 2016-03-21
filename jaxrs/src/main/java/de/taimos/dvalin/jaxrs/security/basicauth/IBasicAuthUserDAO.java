package de.taimos.dvalin.jaxrs.security.basicauth;

import de.taimos.dvalin.jaxrs.security.IUser;

public interface IBasicAuthUserDAO {

    IUser getUserByNameAndPassword(String username, String password);

}
