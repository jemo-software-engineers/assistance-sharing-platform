package com.jemo.assistance_sharing_platform.auth;

public record RegisterRequest(
        String username,
        String email,
        String name,
        String phone,
        String address,
        String password,
        String experienceLevel,
        String skill
) {}
