package com.tertiary.verify.security.jwt;

import com.tertiary.verify.data_model.model.entities.User;
import com.tertiary.verify.security.services.impl.UserDetailsImpl;
import com.tertiary.verify.utils.ConstantUtil;
import io.jsonwebtoken.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  @Value("${sibiya.c.app.jwtSecret}")
  private String jwtSecret;

  @Value("${sibiya.c.app.jwtExpirationMs}")
  private int jwtExpirationMs;

  public String generateJwtToken(Authentication authentication) {

    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

    return buildToken(userPrincipal.getUsername(), userPrincipal.getId());
  }

  public String getUserNameFromJwtToken(String token) {
    return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
  }

  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
      return true;
    } catch (SignatureException e) {
      logger.error("Invalid JWT signature: {}", e);
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e);
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e);
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e);
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e);
    }
    return false;
  }

  public String refreshJwtExpiryTime(User user) throws Exception {
    try {
      return buildToken(user.getUsername(), user.getId());
    } catch (Exception e) {
      logger.error("Error: {}", e);
      throw new Exception(ConstantUtil.SERVICE_UNAVAILABLE_MSSG);
    }
  }

  private String buildToken(String username, Long userID) {
    // 10 hours in milliseconds
    long expirationTimeMillis = System.currentTimeMillis() + 10 * 60 * 60 * 1000;
    return Jwts.builder()
        .setId(UUID.randomUUID().toString())
        .setIssuer("Tertiary Verify")
        .setSubject(username)
        .setAudience("USER_".concat(String.valueOf(userID)))
        .setIssuedAt(new Date())
        .setExpiration(new Date(expirationTimeMillis))
        // .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();
  }
}
