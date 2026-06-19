package com.devsuperior.dscommerce.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class CustomPasswordAuthenticationToken implements Authentication {

	private final String username;
	private final String password;
	private final OAuth2ClientAuthenticationToken clientPrincipal;
	private final Set<String> scopes;
	private final Map<String, Object> additionalParameters;
	private boolean authenticated = false;

	public CustomPasswordAuthenticationToken(String username, String password,
			OAuth2ClientAuthenticationToken clientPrincipal,
			Set<String> scopes, Map<String, Object> additionalParameters) {
		this.username = username;
		this.password = password;
		this.clientPrincipal = clientPrincipal;
		this.scopes = scopes;
		this.additionalParameters = additionalParameters;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return clientPrincipal.getAuthorities();
	}

	@Override
	public Object getCredentials() {
		return password;
	}

	@Override
	public Object getDetails() {
		return additionalParameters;
	}

	@Override
	public Object getPrincipal() {
		return clientPrincipal;
	}

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		this.authenticated = isAuthenticated;
	}

	@Override
	public String getName() {
		return username;
	}

	public String getUsername() { return username; }
	public String getPassword() { return password; }
	public OAuth2ClientAuthenticationToken getClientPrincipal() { return clientPrincipal; }
	public Set<String> getScopes() { return scopes; }
	public Map<String, Object> getAdditionalParameters() { return additionalParameters; }
}
