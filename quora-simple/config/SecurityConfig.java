package com.upgrad.quora.api.config;

import com.upgrad.quora.service.common.JwtTokenProvider;
import com.upgrad.quora.service.dao.UserDao;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDao userDao;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, UserDao userDao) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDao = userDao;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/user/signup", "/user/signin").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public OncePerRequestFilter jwtAuthFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain) throws ServletException, IOException {
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    if (jwtTokenProvider.validateToken(token)) {
                        String username = jwtTokenProvider.getUsernameFromToken(token);
                        userDao.findByUsername(username).ifPresent(u -> {
                            var auth = new org.springframework.security.authentication
                                    .UsernamePasswordAuthenticationToken(
                                    User.withUsername(u.getUsername())
                                        .password(u.getPassword())
                                        .authorities(Collections.emptyList())
                                        .build(),
                                    null, Collections.emptyList());
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        });
                    }
                }
                chain.doFilter(request, response);
            }
        };
    }
}
