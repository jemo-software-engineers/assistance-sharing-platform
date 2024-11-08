package com.jemo.assistance_sharing_platform.request;

import com.jemo.assistance_sharing_platform.skills.Skill;

public record RequestUserRequest(
        String title,
        String description,
        Skill associatedSkill
) {
}
