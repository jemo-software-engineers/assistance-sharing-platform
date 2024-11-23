package com.jemo.assistance_sharing_platform.offer;

import com.jemo.assistance_sharing_platform.skills.UserSkillResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OfferResponse {
    private Long id;
    private Long requestId;
    private Long userId;
    private String username;
    private Long pointScore;
    private List<UserSkillResponse> skills;
}
