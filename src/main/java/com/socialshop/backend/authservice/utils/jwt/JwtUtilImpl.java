package com.socialshop.backend.authservice.utils.jwt;

import com.socialshop.backend.authservice.constants.JwtConstant;
import com.socialshop.backend.authservice.constants.enums.JwtEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
@Configuration
public class JwtUtilImpl implements JwtUtil {
    @Value("${jwt.signing-key}")
    private String signingKey;

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public JwtEnum extractTokenType(String token) {
        var value = extractClaim(token, claims -> claims.get(JwtConstant.typeKey, String.class));
        return JwtEnum.valueOf(value);
    }

    @Override
    public String generateToken(Map<String, Object> claims, UserDetails userDetails, Duration duration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + duration.toMillis()))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public boolean isValidToken(String token, UserDetails userDetails) {
        var username = extractUsername(token);
        return !isExpiredToken(token) && username.equals(userDetails.getUsername());
    }

    private boolean isExpiredToken(String token) {
        var expiredDate = extractClaim(token, Claims::getExpiration);
        return expiredDate.before(new Date(System.currentTimeMillis()));
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    private <T> T extractClaim(String token, Function<Claims, T> extractFunction) {
        Claims claims = extractClaims(token);
        return extractFunction.apply(claims);
    }

    private SecretKey getSigningKey() {
        byte[] bytes = Decoders.BASE64.decode(signingKey);
        return Keys.hmacShaKeyFor(bytes);
    }

}
