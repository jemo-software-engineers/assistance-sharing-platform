package com.jemo.assistance_sharing_platform.auth;

public class LoginResponse {
    private String jwt;

    public LoginResponse(String jwt) {
        this.jwt = jwt;
    }

    // Getter

    public String getJwt() {
        return jwt;
    }
}
