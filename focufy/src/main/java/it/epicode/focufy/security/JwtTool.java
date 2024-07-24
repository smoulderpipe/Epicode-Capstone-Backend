package it.epicode.focufy.security;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import it.epicode.focufy.entities.User;
import it.epicode.focufy.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTool {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.duration}")
    private long duration;

    public String createToken(User user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + duration))
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }

    public void verifyToken(String token) {
        try {
            JwtParser parser = Jwts.parser()
                    .setSigningKey(secret.getBytes())
                    .build();
            parser.parse(token);
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid or expired token");
        }
    }

    public int getIdFromToken(String token) {
        try {
            JwtParser parser = Jwts.parser()
                    .setSigningKey(secret.getBytes())
                    .build();
            Claims claims = (Claims) parser.parse(token).getBody();
            return Integer.parseInt(claims.getSubject());
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid or expired token");
        }
    }
}
