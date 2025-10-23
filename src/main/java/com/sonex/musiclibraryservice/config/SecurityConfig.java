package com.sonex.musiclibraryservice.config;

import com.sonex.musiclibraryservice.security.JwtSecurityFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final JwtSecurityFilter jwtSecurityFilter;
    public SecurityConfig(UserDetailsService userDetailsService, JwtSecurityFilter jwtSecurityFilter){
        this.userDetailsService = userDetailsService;
        this.jwtSecurityFilter = jwtSecurityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // FileController endpoints
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/files/upload").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/files/music/{id}").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/files/list").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/files/download/{id}").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/files/{id}/like").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/files/{id}/score").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/files/{id}/listen_count").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/files/most-played").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/files/trending").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/files/{id}").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/files/favorite").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/files/recent").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/files/recommendations").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/files/count").permitAll()
                        // FolderController endpoints
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/folders").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/folders").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/folders/{id}").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/folders/{id}").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/folders/{id}").permitAll()
                        // MoodController endpoints
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/moods").permitAll()
                        // PublicMusicController endpoints
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/public/music/publish").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/public/music/{id}").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/public/music/artist/{artistId}").permitAll()
                        //
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtSecurityFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration=new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization","Content-Type","Accept"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(){
        DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authenticationProvider);
    }


}
