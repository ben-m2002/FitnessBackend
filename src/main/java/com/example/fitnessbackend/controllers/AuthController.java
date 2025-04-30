package com.example.fitnessbackend.controllers;


import com.example.fitnessbackend.components.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }



}


//@RestController @RequestMapping("/auth")
//public class AuthController {
//    private final AuthenticationManager authManager;
//    private final JwtTokenProvider tokenProvider;
//
//    @PostMapping("/login")
//    public AuthResponse login(@RequestBody AuthRequest req) {
//        // 1. Delegate to AuthenticationManager
//        Authentication auth = authManager.authenticate(
//                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
//        // 2. On success, extract roles & build token
//        List<String> roles = auth.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .toList();
//        String token = tokenProvider.createToken(req.getUsername(), roles);
//        return new AuthResponse(token);
//    }
//}
