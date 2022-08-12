package com.kodeit.backend.config;

import com.kodeit.backend.security.filter.JWTTokenAuthenticationFilter;
import com.kodeit.backend.security.oauth2.OAuth2AuthorizationFailureHandler;
import com.kodeit.backend.security.oauth2.OAuth2AuthorizationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(jwtTokenAuthenticationFilter(), BasicAuthenticationFilter.class);
        http.authorizeRequests()
                .antMatchers("/api/admin/**").authenticated()
                .antMatchers("/api/private/**").authenticated()
                .antMatchers("/api/public/**").permitAll();
        http.authorizeRequests().anyRequest().permitAll();
        http.oauth2Login()
                .userInfoEndpoint()
                .and()
                .successHandler(oAuth2AuthorizationSuccessHandler())
                .failureHandler(oAuth2AuthorizationFailureHandler());
        http.cors().and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "https://www.kodeit.me"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type",
                "x-xsrf-token"));
        configuration.setExposedHeaders(List.of("authorization", "x-xsrf-token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JWTTokenAuthenticationFilter jwtTokenAuthenticationFilter() {
        return new JWTTokenAuthenticationFilter();
    }

    @Bean
    public OAuth2AuthorizationFailureHandler oAuth2AuthorizationFailureHandler() {
        return new OAuth2AuthorizationFailureHandler();
    }

    @Bean
    public OAuth2AuthorizationSuccessHandler oAuth2AuthorizationSuccessHandler() {
        return new OAuth2AuthorizationSuccessHandler();
    }

}