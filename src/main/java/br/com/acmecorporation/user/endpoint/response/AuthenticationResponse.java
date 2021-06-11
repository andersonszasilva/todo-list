package br.com.acmecorporation.user.endpoint.response;

import java.time.LocalDateTime;

public class AuthenticationResponse {

    private String token;
    private String type;
    private LocalDateTime expirationDate;

    public AuthenticationResponse(String token, String type, LocalDateTime expirationDate) {
        this.token = token;
        this.type = type;
        this.expirationDate = expirationDate;
    }

    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }
}
