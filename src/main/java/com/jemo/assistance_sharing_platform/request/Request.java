package com.jemo.assistance_sharing_platform.request;

import com.jemo.assistance_sharing_platform.skills.Skill;
import com.jemo.assistance_sharing_platform.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Request {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    private Skill associatedSkill;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(cascade = CascadeType.ALL)
    private User createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
