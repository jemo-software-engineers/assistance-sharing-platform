package com.jemo.assistance_sharing_platform.user;

public record UpdateUserRequest(
        String username,
        String password,
        String email,
        String address,
        String phone,
        String name,
        Boolean isAvailable
) {
}
