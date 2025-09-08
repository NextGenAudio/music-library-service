package com.sonex.musiclibraryservice.config;

import com.sonex.musiclibraryservice.security.NextAuthSessionFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Autowired
    private NextAuthSessionFilter nextAuthSessionFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())      // for API-only, or provide CSRF tokens if needed
                .cors(withDefaults())              // picks up CorsConfig bean
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // allow preflight
                        .requestMatchers("/public/**", "/health").permitAll()
                        .requestMatchers("/files/**").authenticated()
                        .anyRequest().permitAll()
                )
                // insert our NextAuthSessionFilter BEFORE username/password filter
                .addFilterBefore(nextAuthSessionFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
