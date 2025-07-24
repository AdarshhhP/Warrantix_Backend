package com.example.signup.jwt;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	    // Generate a secret key using HS256 algorithm
	    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // or load from config
        // Method to generate a JWT token for a given email
	    public String generateToken(String email) {
	        return Jwts.builder()
	                .setSubject(email)
	                .setIssuedAt(new Date())
	                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
	                .signWith(key) // Sign the token using the secret key
	                .compact(); // Finalize the token
	    }
        // // Method to extract the email (subject) from a given JWT token
	    public String extractEmail(String token) {
	        return Jwts.parserBuilder().setSigningKey(key).build()
	                .parseClaimsJws(token).getBody().getSubject();
	    }
        // Method to validate if a token is valid and not expired
	    public boolean validateToken(String token) {
	        try {
	            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
	            return true; // Token is valid
	        } catch (JwtException e) {
	            return false; // Token is invalid or expired
	        }
	    }
	}

