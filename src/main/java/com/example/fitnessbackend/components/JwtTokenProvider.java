package com.example.fitnessbackend.components;
import com.example.fitnessbackend.exceptions.InvalidJwtException;
import com.example.fitnessbackend.exceptions.ResourceNotFoundException;
import com.example.fitnessbackend.models.AuthToken;
import com.example.fitnessbackend.models.UserModel;
import com.example.fitnessbackend.repositories.AuthTokenRepository;
import com.example.fitnessbackend.repositories.UserModelRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@PropertySource(value = "classpath:application.properties")
@Component
@Data
public class JwtTokenProvider {
    private String secretKey;
    private long accessValidityInMilliseconds;
    private long refreshValidityInMilliseconds;
    private final AuthTokenRepository authTokenRepository;
    private final UserModelRepository userModelRepository;
    private  Key key;

    public JwtTokenProvider(AuthTokenRepository authTokenRepository,
                            UserModelRepository userModelRepository,
                            @Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.accessExpirationMs}") long accessValidityInMilliseconds,
                            @Value("${jwt.refreshExpirationMs}") long refreshValidityInMilliseconds) {
        this.authTokenRepository = authTokenRepository;
        this.userModelRepository = userModelRepository;
        this.secretKey = secretKey;
        this.accessValidityInMilliseconds = accessValidityInMilliseconds;
        this.refreshValidityInMilliseconds = refreshValidityInMilliseconds;
        key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createRefreshToken(UserModel userModel){
        String jwtToken = this.createJwtToken(userModel, "refresh", refreshValidityInMilliseconds);
        AuthToken token = AuthToken.builder().token(jwtToken)
                .email(userModel.getEmail())
                .expirationMS(new Date(System.currentTimeMillis() + accessValidityInMilliseconds))
                .build();
        authTokenRepository.save(token);
        return jwtToken;
    }

    public String createAccessToken(UserModel userModel){
        return this.createJwtToken(userModel, "access", accessValidityInMilliseconds);
    }

    private String createJwtToken(UserModel userModel, String type, long validityInMilliseconds) {
        Claims claims = Jwts.claims().setSubject(userModel.getEmail());
        claims.put("username", userModel.getUsername());
        claims.put("firstName", userModel.getFirstName());
        claims.put("role", userModel.getRole().name());
        claims.put("type", type);
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        String jwt =  Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .setId(UUID.randomUUID().toString())
                .setIssuer("BYLT/Fitness")
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        return jwt;
    }

    public String getEmail(String token) {
        Claims claims = this.getJwtClaims(token);
        return claims.getSubject();
    }

    // First of all we need to stop sending tokens with every request I dont need to do that anymore, gotta change some stuff
    public String getTokenFromUser(UserModel userModel) {
        AuthToken token = authTokenRepository.findAuthTokenByEmail(userModel.getEmail());
        if (token == null) {
            throw new ResourceNotFoundException("Token not found");
        }
        return token.getToken();
    }

    //Long-lived, slower validation
    public boolean validateRefreshToken(String token){
        if (!this.checkTokenValidation(token, "refresh")) {
            return false;
        }
        // also check in the database
        AuthToken tokenModel = authTokenRepository.findAuthTokenByEmail(getEmail(token));
        if (tokenModel == null) {
            throw new ResourceNotFoundException("Token not found");
        }
        return true;
    }


    // Short Lived token, fast validation
    public boolean validateAccessToken(String token)  {
        // whenever we validate a token it should return to us a new one
        return this.checkTokenValidation(token, "access");
    }

    private boolean checkTokenValidation(String token, String type){
        System.out.println(token);
        try{
            Claims claims = this.getJwtClaims(token);
            if (claims.getExpiration().before(new Date()) || !Objects.equals(claims.get("type", String.class), type)) {
                return false;
            }
        }catch (Exception e){
            throw new InvalidJwtException("Invalid JWT token");
        }
        return true;
    }

    private Claims getJwtClaims(String token){
        JwtParser parser = Jwts.parserBuilder().
                setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8)).build();
        Claims claims = parser.parseClaimsJws(token).getBody();
        return claims;
    }

    @org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteToken(String token) {
        // grab the user from the token
        AuthToken tokenModel = authTokenRepository.findAuthTokenByToken(token);
        if (tokenModel == null) {
            throw new ResourceNotFoundException("Token not found");
        }
        // delete the token out the database
        authTokenRepository.delete(tokenModel);
    }

    @Transactional
    public String renewAccessToken(String token) {
        // check if the token is valid
        if (!this.validateAccessToken(token)) {
            throw new InvalidJwtException("Invalid JWT token");
        }
        String email = this.getEmail(token);
        // grab the user from the token
        UserModel userModel = userModelRepository.findByEmail(email);
        return this.createAccessToken(userModel);
    }


    public String extractAccessToken(HttpServletRequest request) {
        // 1. Read the Authorization header
        String header = request.getHeader("Authorization");
        // 2. Check that it’s non-null and starts with "Bearer "
        if (header != null && header.startsWith("Bearer ")) {
            // 3. Strip off the "Bearer " prefix to get the actual token
            return header.substring(7);
        }
        // No token found
        return null;
    }

}
