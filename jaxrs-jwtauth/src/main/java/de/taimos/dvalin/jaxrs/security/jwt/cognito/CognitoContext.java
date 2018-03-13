/*
 * Copyright (c) 2018. Taimos GmbH http://www.taimos.de
 */

package de.taimos.dvalin.jaxrs.security.jwt.cognito;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.taimos.daemon.spring.conditional.OnSystemProperty;
import de.taimos.dvalin.jaxrs.context.DvalinRSContext;

@Component
@OnSystemProperty(propertyName = "jwtauth.cognito.poolid")
public class CognitoContext {

	private DvalinRSContext context;

	@Autowired
	public void setContext(DvalinRSContext context) {
		this.context = context;
	}

	public CognitoUser getCurrentUser() {
		CognitoUser currentUser = (CognitoUser) this.context.getCurrentUser();
		if (currentUser == null) {
			Response response = Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials or session").build();
			throw new NotAuthorizedException(response);
		}
		return currentUser;
	}

	public String getCurrentSubject() {
		return this.getCurrentUser().getSubject();
	}

	public void assertLoggedIn() {
		this.context.assertLoggedIn();
	}

	public boolean hasRole(String role) {
		return this.context.hasRole(role);
	}

	public boolean isLoggedIn() {
		return this.context.isLoggedIn();
	}

}
