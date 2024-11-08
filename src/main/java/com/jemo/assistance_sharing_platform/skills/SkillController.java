package com.jemo.assistance_sharing_platform.skills;

import com.jemo.assistance_sharing_platform.user.User;
import com.jemo.assistance_sharing_platform.user.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Getter
@Setter
public class SkillController {

    private final SkillService skillsService;
    private final UserService userService;
    private final UserSkillService userSkillService;

    // Get all skills

    @GetMapping("/api/skills")
    public ResponseEntity<List<SkillResponse>> getSkills() {
        List<Skill> skills = skillsService.findAll();

        List<SkillResponse> skillResponses = skills.stream()
                .map(skill -> {
                    SkillResponse skillResponse = new SkillResponse();
                    skillResponse.setId(skill.getId());
                    skillResponse.setName(skill.getName());
                    return skillResponse;
                }).toList();

        return new ResponseEntity<>(skillResponses, HttpStatus.OK);
    }

    // Get a single skill
    @GetMapping("/api/skills/{id}")
    public ResponseEntity<SkillResponse> getSkillById(@PathVariable Long id) {

        Skill skill = skillsService.findById(id);

        SkillResponse skillResponse = new SkillResponse();
        skillResponse.setId(skill.getId());
        skillResponse.setName(skill.getName());
        return new ResponseEntity<>(skillResponse, HttpStatus.OK);
    }
    // Add new skill
    @PostMapping("/admin/api/skills")
    public ResponseEntity<String> createSkill(@RequestBody AdminSkillRequest adminSkillRequest) {
        Boolean created = skillsService.create(adminSkillRequest);

        if (created) {
            return new ResponseEntity<>("Skill created successfully", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Could not create skill", HttpStatus.BAD_REQUEST);

    }

    // Update a skill
    @PutMapping("/admin/api/skills/{id}")
    public ResponseEntity<String> updateSkill(@PathVariable Long id, @RequestBody AdminSkillRequest adminSkillRequest) {
        Boolean updated = skillsService.updateById(id, adminSkillRequest);
        if (updated) {
            return new ResponseEntity<>("Skill updated successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Could not update skill", HttpStatus.BAD_REQUEST);
    }

    // Delete a skill
    @DeleteMapping("/admin/api/skills/{id}")
    public ResponseEntity<String> deleteSkill(@PathVariable Long id) {
        Boolean deleted = skillsService.deleteById(id);
        if (deleted) {
            return new ResponseEntity<>("Skill deleted successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Could not delete skill", HttpStatus.BAD_REQUEST);
    }


    // User add new skill to their profile
    @PostMapping("/api/skills")
    public ResponseEntity<String> addNewSkillToUser(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UserSkillRequest userSkillRequest) {

        User authenticatedUser = userService.findByUsername(userDetails.getUsername());
        Boolean skillAdded = skillsService.addSkillToUser(authenticatedUser.getId(), userSkillRequest.skill(), userSkillRequest.experienceLevel());


        if (skillAdded) {
            return new ResponseEntity<>("Skill added successfully", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Could not add skill to user", HttpStatus.BAD_REQUEST);
    }

    // user get list of skills
    @GetMapping("/api/userskills")
    public ResponseEntity<List<UserSkillResponse>> getAllUserSkills(@AuthenticationPrincipal UserDetails userDetails) {
        User authenticatedUser = userService.findByUsername(userDetails.getUsername());
        List<UserSkill> userSkills = userSkillService.findAllByUserId(authenticatedUser.getId());

        List<UserSkillResponse> userSkillResponses = userSkills.stream()
                .map(skill -> {
                    UserSkillResponse userSkillResponse = new UserSkillResponse();
                    userSkillResponse.setId(skill.getId());
                    userSkillResponse.setSkillName(skill.getSkill().getName());
                    userSkillResponse.setExperienceLevel(String.valueOf(skill.getExperienceLevel()));
                    return userSkillResponse;
                }).toList();

        return new ResponseEntity<>(userSkillResponses, HttpStatus.OK);
    }


    // User update skill experience level
    @PutMapping("/api/userskills/{id}")
    public ResponseEntity<String> updateUserSkill(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id, @RequestBody UserSkillRequest userSkillRequest) {
        User authenticatedUser = userService.findByUsername(userDetails.getUsername());
        UserSkill skill = userSkillService.findUserSkillById(id);
        if(skill != null && authenticatedUser.getId().equals(skill.getUser().getId())) {
            Boolean updated = skillsService.updateSkill(skill, authenticatedUser, userSkillRequest);
            if (updated) {
                return new ResponseEntity<>("Skill updated successfully", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Could not update skill", HttpStatus.BAD_REQUEST);
    }


    // User remove skill from their profile
    @DeleteMapping("/api/userskills/{id}")
    public ResponseEntity<String> deleteUserSkill(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        User authenticatedUser = userService.findByUsername(userDetails.getUsername());
        UserSkill skill = userSkillService.findUserSkillById(id);
        if(skill != null && authenticatedUser.getId().equals(skill.getUser().getId())) {
            Boolean deleted = userSkillService.deleteById(id);
            if (deleted) {
                return new ResponseEntity<>("Skill deleted successfully", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Could not delete skill", HttpStatus.BAD_REQUEST);
    }
}
