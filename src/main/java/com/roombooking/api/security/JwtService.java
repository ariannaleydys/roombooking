package com.roombooking.api.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.roombooking.api.config.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service 
public class JwtService {

    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(
                jwtProperties.getSecretKey()
        );

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails) {

        Date issuedAt = new Date();
        Date expiration = new Date(
                issuedAt.getTime() + jwtProperties.getExpiration()
        );

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    @SuppressWarnings("null")
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @SuppressWarnings("null")
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver
    ) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ) {
        try {
            String username = extractUsername(token);

            return username.equals(userDetails.getUsername())
                    && !extractExpiration(token).before(new Date());

        } catch (Exception exception) {
            return false;
        }
    }
}
