package com.matchday.config.config;

import com.matchday.security.filter.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String TEST_ENDPOINT = "/**";

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configure(http))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(this::configureAuthorization);
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    private void configureAuthorization(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize)
    {
        // 인증 없이 접근 가능한 엔드포인트
        authorize
                .requestMatchers(TEST_ENDPOINT).permitAll();
//                .requestMatchers("/api/auth/**").permitAll()
//                .requestMatchers("/actuator/health").permitAll();

        if ("prod".equals(activeProfile)) {
            authorize
                    .requestMatchers(
                            "/v3/api-docs/**",
                            "/swagger-ui/**",
                            "/swagger-ui.html"
                    ).denyAll();
        } else {
            authorize
                    .requestMatchers(
                            "/v3/api-docs/**",
                            "/swagger-ui/**",
                            "/swagger-ui.html"
                    ).permitAll();
        }

        // 그 외 모든 엔드포인트는 인증 필요
        authorize.anyRequest().authenticated();
    }

}