package com.example.fitnessbackend.components;
import com.example.fitnessbackend.models.AuthToken;
import com.example.fitnessbackend.models.UserModel;
import com.example.fitnessbackend.repositories.AuthTokenRepository;
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

    public JwtTokenProvider(AuthTokenRepository authTokenRepository) {
        this.authTokenRepository = authTokenRepository;
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

    // Fix this exception BS

    public boolean validateToken(String token) throws Exception {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        if (claims.getExpiration().before(new Date())) {
            return false;
        }
        this.renewToken(token);
        return true;
    }


    private void renewToken(String token) throws Exception {
        // grab the user from the token
        AuthToken tokenModel = authTokenRepository.findAuthTokenByEmail(getEmail(token));
        if (tokenModel == null) {
            throw new Exception("Token not found");
        }

        // delete the token out the database
        authTokenRepository.deleteByToken(token);
        // create a new token

    }

}
