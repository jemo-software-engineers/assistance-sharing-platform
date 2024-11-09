package com.jemo.assistance_sharing_platform.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestUserResponse {
    private Long id;
    private String title;
    private String description;
    private String associatedSkill;
    private String status;
    private String user;
    private Long userId;
}
