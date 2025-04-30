package com.example.fitnessbackend.components;
import com.example.fitnessbackend.exceptions.InvalidJwtException;
import com.example.fitnessbackend.exceptions.ResourceNotFoundException;
import com.example.fitnessbackend.models.AuthToken;
import com.example.fitnessbackend.models.UserModel;
import com.example.fitnessbackend.repositories.AuthTokenRepository;
import com.example.fitnessbackend.repositories.UserModelRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import java.util.Date;
import java.util.UUID;

@PropertySource(value = "classpath:application.properties")
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expirationMs}")
    private long validityInMilliseconds;
    private final AuthTokenRepository authTokenRepository;
    private final UserModelRepository userModelRepository;

    public JwtTokenProvider(AuthTokenRepository authTokenRepository, UserModelRepository userModelRepository) {
        this.authTokenRepository = authTokenRepository;
        this.userModelRepository = userModelRepository;
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
                .compact();

        AuthToken token = AuthToken.builder().token(jwt)
                .email(userModel.getEmail())
                .expirationMS(validity)
                .build();

        authTokenRepository.save(token);

        return jwt;
    }

    public String getEmail(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }


    public String validateToken(String token)  {
        // whenever we validate a token it should return to us a new one
        try{
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
            if (claims.getExpiration().before(new Date())) {
                return null;
            }
            return renewToken(token);
        }catch (Exception e){
            throw new InvalidJwtException("Invalid JWT token");
        }
    }


    private String renewToken(String token) throws Exception {
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
