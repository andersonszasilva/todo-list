package br.com.acmecorporation.user.service;

import br.com.acmecorporation.user.domain.AccessToken;
import br.com.acmecorporation.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class TokenService {

    @Value("${security.jwt.secret}")
    private String secretKey;
    @Value("${security.token.expiration-minutes}")
    private String tokenExpirationMinutes;

    public AccessToken generateToken(Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        LocalDateTime creationDate = LocalDateTime.now();
        LocalDateTime expirationDate = LocalDateTime.now().plusMinutes(Long.parseLong(tokenExpirationMinutes));

        String token = Jwts.builder()
                .setIssuer("TODO API LIST")
                .setSubject(principal.getId().toString())
                .setIssuedAt(localDateTimeToDate(creationDate))
                .setExpiration(localDateTimeToDate(expirationDate))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return new AccessToken(token, creationDate, expirationDate, principal);
    }

    private Date localDateTimeToDate(LocalDateTime expirationDate) {
        return Date.from(expirationDate.atZone(ZoneId.systemDefault())
                        .toInstant());
    }

    public boolean isValid(String token) {
        try {
            Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token);
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    public Long getUserId(String token) {
        Claims claims = Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();
        return Long.parseLong(claims.getSubject());
    }
}
