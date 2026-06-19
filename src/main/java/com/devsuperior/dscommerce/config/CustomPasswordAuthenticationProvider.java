package com.devsuperior.dscommerce.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

import java.security.Principal;
import java.util.*;

public class CustomPasswordAuthenticationProvider implements AuthenticationProvider {

	private final OAuth2AuthorizationService authorizationService;
	private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;
	private final UserDetailsService userDetailsService;
	private final PasswordEncoder passwordEncoder;

	public CustomPasswordAuthenticationProvider(
			OAuth2AuthorizationService authorizationService,
			OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator,
			UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		this.authorizationService = authorizationService;
		this.tokenGenerator = tokenGenerator;
		this.userDetailsService = userDetailsService;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		CustomPasswordAuthenticationToken customPasswordAuthentication =
				(CustomPasswordAuthenticationToken) authentication;

		OAuth2ClientAuthenticationToken clientPrincipal =
				getAuthenticatedClientElseThrowInvalidClient(customPasswordAuthentication);
		RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

		UserDetails userDetails;
		try {
			userDetails = userDetailsService.loadUserByUsername(customPasswordAuthentication.getUsername());
		} catch (Exception e) {
			throw new OAuth2AuthenticationException(OAuth2ErrorCodes.ACCESS_DENIED);
		}

		if (!passwordEncoder.matches(customPasswordAuthentication.getPassword(), userDetails.getPassword())) {
			throw new OAuth2AuthenticationException(OAuth2ErrorCodes.ACCESS_DENIED);
		}

		UsernamePasswordAuthenticationToken usernamePasswordAuthentication =
				new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

		Set<String> authorizedScopes = registeredClient.getScopes();

		DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
				.registeredClient(registeredClient)
				.principal(usernamePasswordAuthentication)
				.authorizationServerContext(AuthorizationServerContextHolder.getContext())
				.authorizedScopes(authorizedScopes)
				.authorizationGrantType(new AuthorizationGrantType("password"))
				.authorizationGrant(customPasswordAuthentication);

		OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization
				.withRegisteredClient(registeredClient)
				.principalName(userDetails.getUsername())
				.authorizationGrantType(new AuthorizationGrantType("password"))
				.authorizedScopes(authorizedScopes)
				.attribute(Principal.class.getName(), usernamePasswordAuthentication);

		// Access Token
		OAuth2TokenContext tokenContext = tokenContextBuilder
				.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
		OAuth2Token generatedAccessToken = tokenGenerator.generate(tokenContext);
		if (generatedAccessToken == null) {
			throw new OAuth2AuthenticationException(OAuth2ErrorCodes.SERVER_ERROR);
		}
		OAuth2AccessToken accessToken = new OAuth2AccessToken(
				OAuth2AccessToken.TokenType.BEARER,
				generatedAccessToken.getTokenValue(),
				generatedAccessToken.getIssuedAt(),
				generatedAccessToken.getExpiresAt(),
				authorizedScopes);
		if (generatedAccessToken instanceof ClaimAccessor) {
			authorizationBuilder.token(accessToken,
					meta -> meta.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME,
							((ClaimAccessor) generatedAccessToken).getClaims()));
		} else {
			authorizationBuilder.accessToken(accessToken);
		}

		OAuth2Authorization authorization = authorizationBuilder.build();
		authorizationService.save(authorization);

		return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return CustomPasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

	private static OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(
			Authentication authentication) {
		OAuth2ClientAuthenticationToken clientPrincipal = null;
		if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
			clientPrincipal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
		}
		if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
			return clientPrincipal;
		}
		throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
	}
}
