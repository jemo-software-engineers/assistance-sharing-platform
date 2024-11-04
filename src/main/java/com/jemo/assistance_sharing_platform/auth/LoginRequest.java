package com.jemo.assistance_sharing_platform.auth;

public record LoginRequest (
        String username,
        String password
) {}
