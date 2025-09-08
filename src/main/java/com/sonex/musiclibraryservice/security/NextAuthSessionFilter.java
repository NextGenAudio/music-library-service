package com.sonex.musiclibraryservice.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.WebUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@Component
public class NextAuthSessionFilter extends OncePerRequestFilter {

    private final Logger log = LoggerFactory.getLogger(NextAuthSessionFilter.class);

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String sessionUrl;
    private final String cookieName;

    public NextAuthSessionFilter(WebClient webClient,
                                 ObjectMapper objectMapper,
                                 @Value("${nextauth.session-url}") String sessionUrl,
                                 @Value("${nextauth.cookie-name:authjs.session-token}") String cookieName) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
        this.sessionUrl = sessionUrl;
        this.cookieName = cookieName;
    }

    // Skip filter for preflight requests (OPTIONS) to avoid extra calls
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return HttpMethod.OPTIONS.matches(request.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            // If already authenticated, skip verification
            if (SecurityContextHolder.getContext().getAuthentication() != null &&
                    SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
                filterChain.doFilter(request, response);
                return;
            }

            Cookie cookie = WebUtils.getCookie(request, cookieName);
            if (cookie == null || !StringUtils.hasText(cookie.getValue())) {
                log.debug("NextAuthSessionFilter: no '{}' cookie present", cookieName);
                filterChain.doFilter(request, response);
                return;
            }

            log.debug("NextAuthSessionFilter: calling NextAuth session endpoint with cookie (len={})", cookie.getValue().length());

            // Call NextAuth server-side session endpoint
            ResponseEntity<String> resp = webClient.get()
                    .uri(sessionUrl)
                    .header(HttpHeaders.COOKIE, cookieName + "=" + cookie.getValue())
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .toEntity(String.class)
                    .block(Duration.ofSeconds(5));

            if (resp == null) {
                log.warn("NextAuthSessionFilter: null response from NextAuth");
                filterChain.doFilter(request, response);
                return;
            }

            if (!resp.getStatusCode().is2xxSuccessful()) {
                log.debug("NextAuthSessionFilter: session invalid, status={}", resp.getStatusCode());
                filterChain.doFilter(request, response);
                return;
            }

            String body = resp.getBody();
            log.debug("NextAuthSessionFilter: session response body={}", body);

            JsonNode root = objectMapper.readTree(body);

            // NextAuth usually returns: { "user": { "name": "...", "email": "...", "id": "..."}, "expires": "..." }
            JsonNode userNode = root.path("user");
            if (userNode.isMissingNode() || userNode.isNull()) {
                log.debug("NextAuthSessionFilter: no user node in session JSON");
                filterChain.doFilter(request, response);
                return;
            }

            // Prefer id (UUID) if present, otherwise sub or email
            String userId = userNode.has("id") ? userNode.path("id").asText(null)
                    : userNode.has("sub") ? userNode.path("sub").asText(null)
                    : userNode.path("email").asText(null);

            if (userId == null) {
                log.debug("NextAuthSessionFilter: cannot find id/sub/email in session user object");
                filterChain.doFilter(request, response);
                return;
            }

            var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);

            auth.setDetails(userNode); // store whole user JSON in details if you want
            SecurityContextHolder.getContext().setAuthentication(auth);

            log.info("NextAuthSessionFilter: authenticated userId={}", userId);

        } catch (Exception ex) {
            log.error("NextAuthSessionFilter: error verifying NextAuth session", ex);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
