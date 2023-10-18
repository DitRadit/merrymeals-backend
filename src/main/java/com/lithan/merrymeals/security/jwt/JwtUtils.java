package com.lithan.merrymeals.security.jwt;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.lithan.merrymeals.security.service.UserDetailsImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtUtils {


    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.refreshexpirationMs}")
    private int refreshJwtExpirationMs;
    @Value("${jwt.expirationMs}")
    private int jwtExpirationMs;
    @Value("${jwt.secret}")
    private String jwtSecret;

    public boolean validateJwtToken(String authToken){
      try {
          Jwts.parser().setSigningKey(jwtSecret)
            .parseClaimsJws(authToken);
        return true;
      } catch (SignatureException e) {
        logger.error("Invalid JWT Signature : {}", e.getMessage());
      } catch (MalformedJwtException e){
        logger.error("Invalid JWT Token : {}", e.getMessage());
      } catch (ExpiredJwtException e){
        logger.error("JWT Token is Expired : {}", e.getMessage());
      } catch (UnsupportedJwtException e){
        logger.error("JWT Token is Unsupported : {}", e.getMessage());
      } catch (IllegalArgumentException e){
        logger.error("JWT Claims string is empty : {}", e.getMessage());
      }
        return false;
    }
    

   public String generateJwtToken(Authentication authentication){
    UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();

    Claims claims = Jwts.claims().setSubject(principal.getUsername());
    claims.put("roles", principal.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList()));

    return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date())
            .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
}

public String generateRefreshJwtToken(Authentication authentication){
    UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();

    Claims claims = Jwts.claims().setSubject(principal.getUsername());
    claims.put("roles", principal.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList()));

    return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date())
            .setExpiration(new Date((new Date()).getTime() + refreshJwtExpirationMs))
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
}

    public String getUserNameFromJwtToken(String token){
        return Jwts.parser().setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }
    public List<String> getRolesFromJwtToken(String token) {
      Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
      return (List<String>) claims.get("roles");
  }
  
}
