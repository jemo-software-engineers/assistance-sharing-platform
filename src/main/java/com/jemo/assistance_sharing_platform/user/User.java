package com.jemo.assistance_sharing_platform.user;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.jemo.assistance_sharing_platform.skills.UserSkill;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "user_table")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id") // help prevent infinite loop
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    @NonNull
    @NotEmpty(message = "username cannot be empty")
    private String username;

    @Email
    @NonNull
    @NotEmpty(message = "email cannot be empty")
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @NonNull
    @NotEmpty(message = "phone cannot be empty")
    private String phone;

    @Column(nullable = false)
    @NonNull
    @NotEmpty(message = "name cannot be empty")
    private String name;

    @Column(nullable = false)
    @NonNull
    @NotEmpty(message = "address cannot be empty")
    private String address;

    @Column(nullable = false)
    @NonNull
    @NotEmpty(message = "password cannot be empty")
    private String password;

    @Column(nullable = true)
    private Long pointScore;

    @Column(nullable = false)
    private Boolean isAvailable;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(nullable = true)
    List<UserSkill> userSkills;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
