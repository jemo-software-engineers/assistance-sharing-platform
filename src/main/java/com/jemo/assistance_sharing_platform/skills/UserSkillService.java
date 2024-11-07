package com.jemo.assistance_sharing_platform.skills;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSkillService {
    private final UserSkillRepository userSkillRepository;

    public UserSkill findUserSkillById(Long id) {
        return userSkillRepository.findById(id).orElse(null);
    }

    public Boolean deleteById(Long id) {
        UserSkill skill = userSkillRepository.findById(id).orElse(null);
        if (skill != null) {
            userSkillRepository.delete(skill);
            return true;
        }
        return false;
    }
}
