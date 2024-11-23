package com.jemo.assistance_sharing_platform.skills;

import com.jemo.assistance_sharing_platform.auth.exceptions.SkillAdditionException;
import com.jemo.assistance_sharing_platform.user.User;
import com.jemo.assistance_sharing_platform.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillsRepository;
    private final UserService userService;
    private final UserSkillRepository userSkillRepository;

    public List<Skill> findAll() {
        return skillsRepository.findAll();
    }

    public Skill findById(Long id) {
        return skillsRepository.findById(id).orElse(null);
    }

    public Skill findByName(String name) {
        return skillsRepository.findByName(name);
    }

    public Boolean create(AdminSkillRequest adminSkillRequest) {
        Skill skillExists = skillsRepository.findByName(adminSkillRequest.skill());
        if (skillExists != null) {
            return false;
        }

        Skill newSKill = Skill.builder()
                .name(adminSkillRequest.skill())
                .build();
        Skill savedSkill = skillsRepository.save(newSKill);
        return savedSkill.getId() != null;
    }

    public Boolean updateById(Long id, AdminSkillRequest adminSkillRequest) {
        Skill skillExists = skillsRepository.findById(id).orElse(null);
        if (skillExists != null) {
            skillExists.setId(id);
            skillExists.setName(adminSkillRequest.skill() != null ? adminSkillRequest.skill() : skillExists.getName());
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

    public Boolean addSkillToUser(Long userId, String skill, String experienceLevel) {
        User user = userService.findUserById(userId);
        if(user != null) {
            Skill skillFound = skillsRepository.findByName(skill); // check this
            if(skillFound != null) {
                UserSkill userSkill = UserSkill.builder()
                        .user(user)
                        .skill(skillFound)
                        .experienceLevel(ExperienceLevel.valueOf(experienceLevel))
                        .build();
                userSkillRepository.save(userSkill);
                return true;
            }
        }
        throw new SkillAdditionException("Failed to add skill to user " + userId);
    }

    public Boolean updateSkill(UserSkill skill, User authenticatedUser, UserSkillRequest userSkillRequest) {
        UserSkill updatedUserSkill = UserSkill.builder()
                .id(skill.getId())
                .user(authenticatedUser)
                .skill(skill.getSkill())
                .experienceLevel(ExperienceLevel.valueOf(userSkillRequest.experienceLevel()))
                .build();
        userSkillRepository.save(updatedUserSkill);
        return true;
    }

    public static List<UserSkillResponse> convertListOfSkillsToSkillsResponse(List<UserSkill> skills) {
        return skills.stream()
                .map(skill -> {
                    UserSkillResponse userSkillResponse = new UserSkillResponse();
                    userSkillResponse.setId(skill.getId());
                    userSkillResponse.setSkillName(skill.getSkill().getName());
                    userSkillResponse.setExperienceLevel(skill.getExperienceLevel().name());
                    return userSkillResponse;
                }).toList();
    }
}
