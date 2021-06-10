package br.com.acmecorporation.user.endpoint.response;

public class UserResponse {

    private String token;
    private String type;

    public UserResponse(String token, String type) {
        this.token = token;
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }
}
