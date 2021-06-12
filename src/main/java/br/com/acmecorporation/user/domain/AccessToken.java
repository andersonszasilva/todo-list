package br.com.acmecorporation.user.domain;

import java.time.LocalDateTime;

public class AccessToken {

    private String token;
    private LocalDateTime creationDate;
    private LocalDateTime expirationDate;
    private User user;

    public AccessToken(String token, LocalDateTime creationDate, LocalDateTime expirationDate, User user) {
        this.token = token;
        this.creationDate = creationDate;
        this.expirationDate = expirationDate;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public User getUser() {
        return user;
    }

    public String getUsername() {
        return this.user.getUsername();
    }
}
