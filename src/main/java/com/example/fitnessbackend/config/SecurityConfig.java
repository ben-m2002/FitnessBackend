package com.example.fitnessbackend.config;

import com.example.fitnessbackend.components.JwtTokenProvider;
import com.example.fitnessbackend.dtos.responses.ResponseDto;
import com.example.fitnessbackend.security.JwtAuthenticationFilter;
import com.example.fitnessbackend.service.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.Jar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)   // if you’re using @PreAuthorize, etc.
@Configuration
public class SecurityConfig{

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;


    public SecurityConfig(JwtTokenProvider tokenProvider, CustomUserDetailsService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       http
                .csrf(csrf -> csrf.disable())
               .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
               .exceptionHandling(ex -> ex
                       .authenticationEntryPoint((request, response, authException) -> {
                           // Always 401 for invalid/missing JWT
                           response.setStatus(HttpStatus.UNAUTHORIZED.value());
                           response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                           String body = new ObjectMapper()
                                   .writeValueAsString(new ResponseDto(authException.getMessage(), null));
                           response.getWriter().write(body);
                       })
               )
               .authorizeHttpRequests(auth -> auth
                       .requestMatchers("/api/auth/**").permitAll()
                       .anyRequest().authenticated()
               )
               .authenticationProvider(daoAuthProvider())
               .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);


       return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(
                passwordEncoder()
        );
        return authProvider;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(tokenProvider, userDetailsService);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
       return authConfig.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
