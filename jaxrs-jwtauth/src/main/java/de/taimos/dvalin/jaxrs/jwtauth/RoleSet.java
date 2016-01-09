package de.taimos.dvalin.jaxrs.jwtauth;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

public class RoleSet {

	private static final Logger LOGGER = LoggerFactory.getLogger(RoleSet.class);

	private final Set<String> roles = new HashSet<>();


	public RoleSet(String... perms) {
		this.roles.addAll(Sets.newHashSet(perms));
	}

	public RoleSet with(String perm) {
		this.roles.add(perm);
		return this;
	}

	public RoleSet with(String... perms) {
		this.roles.addAll(Sets.newHashSet(perms));
		return this;
	}

	public boolean oneOf(String... perms) {
		return !Collections.disjoint(this.roles, Sets.newHashSet(perms));
	}

	public boolean allOf(String... perms) {
		return this.roles.containsAll(Sets.newHashSet(perms));
	}

	public boolean has(String perm) {
		return this.roles.contains(perm);
	}

	public Set<String> getRoles() {
		return this.roles;
	}

}
