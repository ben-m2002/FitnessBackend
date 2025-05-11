package com.example.fitnessbackend.components;

import com.example.fitnessbackend.exceptions.InvalidJwtException;
import com.example.fitnessbackend.models.UserModel;
import com.example.fitnessbackend.nonPersistData.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class JwtTokenProviderTest {

  @Autowired private JwtTokenProvider jwtTokenProvider;

  private String createToken(String email, String type) {
    UserModel userModel = UserModel.builder().email(email).role(UserRole.USER).build();
    if (type.equals("refresh")) {
      return jwtTokenProvider.createRefreshToken(userModel);
    } else if (type.equals("access")) {
      return jwtTokenProvider.createAccessToken(userModel);
    }
    throw new InvalidJwtException("Invalid token");
  }

  @Test
  public void createRefreshToken_validUserModel() {
    String refreshToken = this.createToken("ben@admin.com", "refresh");
    Assertions.assertNotNull(refreshToken);
  }

  @Test
  public void createAccessToken_validUserModel() {
    String accessToken = this.createToken("ben@admin.com", "access");
    Assertions.assertNotNull(accessToken);
  }

  @Test
  public void getEmail_validToken() {
    String email = "ben@admin.com";
    String accessToken = this.createToken("ben@admin.com", "access");
    String emailFromToken = jwtTokenProvider.getEmail(accessToken);
    Assertions.assertEquals(email, emailFromToken);
  }

  @Test
  public void validaAccessToken_valid() {
    String accessToken = this.createToken("ben@admin.com", "access");
    boolean isValid = jwtTokenProvider.validateAccessToken(accessToken);
    Assertions.assertTrue(isValid);
  }

  @Test
  public void validaAccessToken_Invalid() {
    Assertions.assertThrows(
        InvalidJwtException.class,
        () -> jwtTokenProvider.validateAccessToken("sdswadwqe2weqreqwewqewqewqewqeqwewq"));
  }
}
