package com.jemo.assistance_sharing_platform.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
public class LoginResponse {
    private String jwt;
    private Collection<? extends GrantedAuthority> role;

    public LoginResponse(String jwt, Collection<? extends GrantedAuthority> role) {
        this.jwt = jwt;
        this.role = role;
    }

}
