package com.jemo.assistance_sharing_platform.request;

public record RequestUserRequest(
        String title,
        String description,
        String associatedSkill
) {}
