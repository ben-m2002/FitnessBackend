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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@PropertySource(value = "classpath:application.properties")
@Component
public class JwtTokenProvider {
    private String secretKey;
    private long validityInMilliseconds;
    private final AuthTokenRepository authTokenRepository;
    private final UserModelRepository userModelRepository;
    private  Key key;

    public JwtTokenProvider(AuthTokenRepository authTokenRepository,
                            UserModelRepository userModelRepository,
                            @Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.expirationMs}") long validityInMilliseconds) {
        this.authTokenRepository = authTokenRepository;
        this.userModelRepository = userModelRepository;
        this.secretKey = secretKey;
        this.validityInMilliseconds = validityInMilliseconds;
        key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(UserModel userModel){
        Claims claims = Jwts.claims().setSubject(userModel.getEmail());
        claims.put("username", userModel.getUsername());
        claims.put("firstName", userModel.getFirstName());
        claims.put("role", userModel.getRole().name());
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

        AuthToken token = AuthToken.builder().token(jwt)
                .email(userModel.getEmail())
                .expirationMS(validity)
                .build();

        authTokenRepository.save(token);

        return jwt;
    }

    public String getEmail(String token) {
        Claims claims = this.getJwtClaims(token);
        return claims.getSubject();
    }


    public boolean validateToken(String token)  {
        // whenever we validate a token it should return to us a new one
        System.out.println(token);
        try{
            Claims claims = this.getJwtClaims(token);
            if (claims.getExpiration().before(new Date())) {
                return false;
            }
        }catch (Exception e){
            throw new InvalidJwtException("Invalid JWT token");
        }
        // check if the token is in the database
        AuthToken tokenModel = authTokenRepository.findAuthTokenByEmail(getEmail(token));
        if (tokenModel == null) {
            return false;
        }
        return true;
    }

    private Claims getJwtClaims(String token){
        JwtParser parser = Jwts.parserBuilder().
                setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8)).build();
        Claims claims = parser.parseClaimsJws(token).getBody();
        return claims;
    }


    private String renewToken(String token)  {
        // grab the user from the token
        AuthToken tokenModel = authTokenRepository.findAuthTokenByEmail(getEmail(token));
        if (tokenModel == null) {
            throw new ResourceNotFoundException("Token not found");
        }
        UserModel userModel = userModelRepository.findByEmail(tokenModel.getEmail());
        // delete the token out the database
        authTokenRepository.deleteByToken(token);
        // create a new token
        return this.createToken(userModel);
    }

}
