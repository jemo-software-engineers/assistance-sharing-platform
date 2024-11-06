package com.jemo.assistance_sharing_platform.skills;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillsRepository;

    public List<Skill> findAll() {
        return skillsRepository.findAll();
    }

    public Skill findById(Long id) {
        return skillsRepository.findById(id).orElse(null);
    }

    public Boolean create(SkillRequest skillRequest) {
        Skill skillExists = skillsRepository.findByName(skillRequest.skill());
        if (skillExists != null) {
            return false;
        }

        Skill newSKill = Skill.builder()
                .name(skillRequest.skill())
                .build();
        Skill savedSkill = skillsRepository.save(newSKill);
        return savedSkill.getId() != null;
    }

    public Boolean updateById(Long id, SkillRequest skillRequest) {
        Skill skillExists = skillsRepository.findById(id).orElse(null);
        if (skillExists != null) {
            skillExists.setId(id);
            skillExists.setName(skillRequest.skill() != null ? skillRequest.skill() : skillExists.getName());
            skillsRepository.save(skillExists);
            return true;
        }

        return false;
    }

    public Boolean deleteById(Long id) {
        Skill skill = skillsRepository.findById(id).orElse(null);
        if (skill != null) {
            skillsRepository.delete(skill);
            return true;
        }
        return false;

    }
}
