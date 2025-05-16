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
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;

@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // if you’re using @PreAuthorize, etc.
@Configuration
public class SecurityConfig {

  private final JwtTokenProvider tokenProvider;
  private final CustomUserDetailsService userDetailsService;

  public SecurityConfig(
      JwtTokenProvider tokenProvider, CustomUserDetailsService userDetailsService) {
    this.tokenProvider = tokenProvider;
    this.userDetailsService = userDetailsService;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    // 1) Actuator endpoints first:
                    .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()                   //  [oai_citation:0‡GitHub](https://github.com/spring-projects/spring-security/issues/13602?utm_source=chatgpt.com)
                    // 2) Your public API:
                    .requestMatchers("/api/auth/**").permitAll()
                    // 3) Everything else must be authenticated:
                    .anyRequest().authenticated()                                                   //  [oai_citation:1‡GitHub](https://github.com/spring-projects/spring-security/issues/9835?utm_source=chatgpt.com)
            )
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((req, res, authEx) -> {
                      res.setStatus(HttpStatus.UNAUTHORIZED.value());
                      res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                      String body = new ObjectMapper()
                              .writeValueAsString(new ResponseDto(authEx.getMessage()));
                      res.getWriter().write(body);
                    })
            )
            .authenticationProvider(daoAuthProvider())
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) ->
        web
            // completely bypass security filters for these paths:
            .ignoring()
            .requestMatchers("/actuator/**", "/error");
  }

  @Bean
  public DaoAuthenticationProvider daoAuthProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter(tokenProvider, userDetailsService);
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
      throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
