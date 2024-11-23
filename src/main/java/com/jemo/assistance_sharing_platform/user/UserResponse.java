package com.jemo.assistance_sharing_platform.user;

import com.jemo.assistance_sharing_platform.skills.UserSkillResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserResponse{
    private Long id;
    private String username;
    private String email;
    private String role;
    private String address;
    private String phone;
    private String name;
    private Boolean isAvailable;
    private Long pointScore;
    private List<UserSkillResponse> skills;

}
