package com.member.memberservice.Security;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.JsonNode;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.keycloak.TokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class CustomSecurityFilter extends OncePerRequestFilter {

    JwkProvider jwkProvider;

    public CustomSecurityFilter() throws MalformedURLException {
        jwkProvider = new JwkProviderBuilder(new URL("http://localhost:8080/realms/SpringSecurityKeycloak/protocol/openid-connect/certs")).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");




        if (authorizationHeader != null) {
            String token = authorizationHeader.substring(7);
            DecodedJWT decodedJWT = JWT.decode(token);
            try {
                Jwk jwk = jwkProvider.get(decodedJWT.getKeyId());
                Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);

                JWTVerifier verifier = JWT.require(algorithm)
                        .withIssuer("http://localhost:8080/realms/SpringSecurityKeycloak")
                        .withAudience("backend-api")
                        //                 .withClaim("role", "ADMIN")
                        //                 .withSubject("admin")
                        .build();
                verifier.verify(decodedJWT);

                List<GrantedAuthority> authorities = new ArrayList<>();
                Map<String, Object>resourceAccess = decodedJWT.getClaim("resource_access").asMap();
                if ( resourceAccess != null) {
                    Object myAppClientRoles = resourceAccess.get("my-app-client");
                    if (myAppClientRoles instanceof Map) {
                        Map<String, Object> myAppClient = (Map<String, Object>) myAppClientRoles;
                        Object roles = myAppClient.get("roles");

                        if (roles instanceof List) {
                            List<String> appRoles = (List<String>) roles;
                            for (String role : appRoles) {
                                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                            }
                        }
                    }
                }

                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(decodedJWT.getSubject(), "***", authorities)
                );

            } catch (JwkException e) {
                throw new RuntimeException(e);
            } catch (TokenExpiredException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            } catch (Exception e) {
                throw new RuntimeException("Authentication failed: " + e.getMessage());
            }

        }
        response.setStatus(HttpServletResponse.SC_OK);
        filterChain.doFilter(request, response);
    }

}