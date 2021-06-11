package br.com.acmecorporation.user.service;

import br.com.acmecorporation.user.domain.AccessToken;
import br.com.acmecorporation.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class TokenService {

    private String secretKey = "A+X;fTJP&Pd,TD9dwVq(hsHX,ya^<wsD_UK7L+@=S;{'CydP]{v@}G'b>et;yz$*\\yL5S8EJN:%P:X%H9>#nYLrX}@\\s?CQcpspH,2emzBc!Q[V'AYa~uzF8WR~AUrMzxp/V$9([S9X#zj/CH('#]B_Hc+%fGhe27YB;^j4\\Xk=Ju\"Ap~_&<L;=!Z;!,2UP;!hF3P]j85#*`&T]/kB/W^6$v~u6qpejL>kY^f)sy4:qTq_Ec!-z!@aAp~sLKGU>$";

    public AccessToken generateToken(Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        LocalDateTime creationDate = LocalDateTime.now();
        LocalDateTime expirationDate = LocalDateTime.now().plusMinutes(5);

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
