package com.jemo.assistance_sharing_platform.skills;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    Skill findByName(String name);
}
