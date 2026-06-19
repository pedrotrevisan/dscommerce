package com.devsuperior.dscommerce.config;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

@Bean
@Order(1)
public SecurityFilterChain asSecurityFilterChain(HttpSecurity http) throws Exception {
OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();
authorizationServerConfigurer.tokenEndpoint(tokenEndpoint -> tokenEndpoint
.accessTokenRequestConverter(new CustomPasswordAuthenticationConverter())
.authenticationProvider(new CustomPasswordAuthenticationProvider(authorizationService(),
tokenGenerator(), userDetailsServiceImpl(), passwordEncoder())));

http.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher());
http.with(authorizationServerConfigurer, Customizer.withDefaults());
http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

return http.build();
}

@Bean
@Order(2)
public SecurityFilterChain rsSecurityFilterChain(HttpSecurity http) throws Exception {
http.csrf(csrf -> csrf.disable());
http.authorizeHttpRequests(auth -> auth
.requestMatchers("/oauth2/**").permitAll()
.requestMatchers("/h2-console/**").permitAll()
.requestMatchers(HttpMethod.GET, "/products").permitAll()
.requestMatchers(HttpMethod.GET, "/products/{id}").permitAll()
.requestMatchers(HttpMethod.GET, "/categories").permitAll()
.anyRequest().authenticated()
);
http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
http.headers(headers -> headers.frameOptions(frame -> frame.disable()));
return http.build();
}

@Bean
public OAuth2AuthorizationService authorizationService() {
return new InMemoryOAuth2AuthorizationService();
}

@Bean
public PasswordEncoder passwordEncoder() {
return new BCryptPasswordEncoder();
}

@Bean
public TokenSettings tokenSettings() {
return TokenSettings.builder()
.accessTokenTimeToLive(java.time.Duration.ofDays(1))
.build();
}

@Bean
public ClientSettings clientSettings() {
return ClientSettings.builder().requireAuthorizationConsent(false).build();
}

@Bean
public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
return context -> {
context.getClaims().claim("username", context.getPrincipal().getName());
context.getClaims().claim("authorities", context.getPrincipal().getAuthorities()
.stream().map(a -> a.getAuthority()).toList());
};
}

@Bean
public JWKSource<SecurityContext> jwkSource() {
RSAKey rsaKey = generateRsa();
JWKSet jwkSet = new JWKSet(rsaKey);
return new ImmutableJWKSet<>(jwkSet);
}

private static RSAKey generateRsa() {
KeyPair keyPair = generateRsaKey();
RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
return new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build();
}

private static KeyPair generateRsaKey() {
KeyPair keyPair;
try {
KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
keyPairGenerator.initialize(2048);
keyPair = keyPairGenerator.generateKeyPair();
} catch (Exception ex) {
throw new IllegalStateException(ex);
}
return keyPair;
}

@Bean
public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
return com.nimbusds.jose.proc.SecurityContext.class.isInstance(jwkSource)
? null
: org.springframework.security.oauth2.jwt.NimbusJwtDecoder.withJwkSetUri("").build();
}

@Bean
public AuthorizationServerSettings authorizationServerSettings() {
return AuthorizationServerSettings.builder().build();
}

@Bean
public com.devsuperior.dscommerce.services.UserService userDetailsServiceImpl() {
return new com.devsuperior.dscommerce.services.UserService();
}

@Bean
public org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator<?> tokenGenerator() {
com.nimbusds.jose.jwk.source.JWKSource<SecurityContext> jwkSource = jwkSource();
var jwtGenerator = new org.springframework.security.oauth2.server.authorization.token.JwtGenerator(
org.springframework.security.oauth2.jwt.NimbusJwtEncoder.withJwkSource(jwkSource).build());
jwtGenerator.setJwtCustomizer(tokenCustomizer());
var accessTokenGenerator = new org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator();
var refreshTokenGenerator = new org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator();
return new org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator(
jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
}
}
