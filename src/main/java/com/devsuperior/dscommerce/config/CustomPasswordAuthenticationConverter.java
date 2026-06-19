package com.devsuperior.dscommerce.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.StringUtils;

import java.util.*;

public class CustomPasswordAuthenticationConverter implements AuthenticationConverter {

	@Override
	public Authentication convert(HttpServletRequest request) {
		String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
		if (!"password".equals(grantType)) {
			return null;
		}

		Authentication clientPrincipal = null;
		try {
			org.springframework.security.core.context.SecurityContext ctx =
					org.springframework.security.core.context.SecurityContextHolder.getContext();
			clientPrincipal = ctx.getAuthentication();
		} catch (Exception ignored) {}

		if (!(clientPrincipal instanceof OAuth2ClientAuthenticationToken)) {
			return null;
		}

		Map<String, Object> additionalParameters = new HashMap<>();
		request.getParameterMap().forEach((key, values) -> {
			if (!key.equals(OAuth2ParameterNames.GRANT_TYPE) &&
					!key.equals(OAuth2ParameterNames.CLIENT_ID) &&
					!key.equals(OAuth2ParameterNames.CLIENT_SECRET)) {
				additionalParameters.put(key, values[0]);
			}
		});

		String username = request.getParameter(OAuth2ParameterNames.USERNAME);
		String password = request.getParameter(OAuth2ParameterNames.PASSWORD);

		if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
			return null;
		}

		Set<String> scopes = new HashSet<>();
		String scopeParam = request.getParameter(OAuth2ParameterNames.SCOPE);
		if (StringUtils.hasText(scopeParam)) {
			scopes = new HashSet<>(Arrays.asList(scopeParam.split(" ")));
		}

		return new CustomPasswordAuthenticationToken(username, password,
				(OAuth2ClientAuthenticationToken) clientPrincipal, scopes, additionalParameters);
	}
}
