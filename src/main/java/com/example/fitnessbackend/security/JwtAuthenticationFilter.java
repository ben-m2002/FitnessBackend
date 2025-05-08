package com.example.fitnessbackend.security;

import com.example.fitnessbackend.components.JwtTokenProvider;
import com.example.fitnessbackend.dtos.responses.ResponseDto;
import com.example.fitnessbackend.exceptions.InvalidJwtException;
import com.example.fitnessbackend.service.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtTokenProvider tokenProvider;
  private final CustomUserDetailsService userDetailsService;

  public JwtAuthenticationFilter(
      JwtTokenProvider tokenProvider, CustomUserDetailsService userDetailsService) {
    this.tokenProvider = tokenProvider;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String path = request.getRequestURI();
    return path.startsWith("/api/auth/");
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer")) {
      String token = header.substring(7);
      try {
        if (tokenProvider.validateAccessToken(token)) {
          String email = tokenProvider.getEmail(token);
          UserPrincipal userPrincipal =
              (UserPrincipal) userDetailsService.loadUserByUsername(email);
          UsernamePasswordAuthenticationToken auth =
              new UsernamePasswordAuthenticationToken(
                  userPrincipal, null, userPrincipal.getAuthorities());
          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      } catch (InvalidJwtException e) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
                new ObjectMapper()
                        .writeValueAsString(new ResponseDto(e.getMessage()))
        );
      }
    }
    filterChain.doFilter(request, response);
  }
}
