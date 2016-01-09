/**
 *
 */
package de.taimos.dvalin.jaxrs.jwtauth;

import javax.ws.rs.ForbiddenException;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.MessageContextImpl;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.taimos.dvalin.mongo.links.IDLinkDAO;
import de.taimos.resultsservice.server.league.League;
import de.taimos.resultsservice.server.login.UserSession;
import de.taimos.resultsservice.server.matchday.Matchday;

/**
 * Copyright 2016 Taimos GmbH<br>
 * <br>
 *
 * @author thoeger
 *
 */
@Component
public class ExtendedSecurityContextBean {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${jwtauth.anonymousRoles:}")
    private String anonymousRoles;

	public AuthenticatedUser getLoggedIn() {
		return (AuthenticatedUser) this.getContext().get(AuthenticatedUser.class.getName());
	}

	public RoleSet getRoles() {
        if (this.getLoggedIn() != null) {
            return new RoleSet(this.getLoggedIn().getRoles());
        }
        if (anonymousRoles != null && !anonymousRoles.isEmpty()) {
            return new RoleSet(anonymousRoles.split(","));
        }
		return null;
	}

	public boolean hasRole(String role) {
		return this.getRoles().has(role);
	}

	public boolean hasOneOfRoles(String... roles) {
		return this.getRoles().oneOf(roles);
	}

	public void assertRole(String perm) {
		if (!this.hasRole(perm)) {
			throw new ForbiddenException("Missing permission: " + perm);
		}
	}

	private MessageContext getContext() {
		return new MessageContextImpl(PhaseInterceptorChain.getCurrentMessage());
	}
}
