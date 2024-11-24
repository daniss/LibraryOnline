package com.book.bookservice.Security;

import com.book.bookservice.Security.CustomSecurityFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/v1/book").permitAll()
                        .requestMatchers("/api/v1/book/{id}/image").permitAll()
                        .requestMatchers("/api/v1/book/{id}").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterAfter(new CustomSecurityFilter(), BasicAuthenticationFilter.class)
                .build();
    }
}
