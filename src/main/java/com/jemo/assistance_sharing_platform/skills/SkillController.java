package com.jemo.assistance_sharing_platform.skills;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class SkillController {

    private final SkillService skillsService;

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
    @PostMapping("/api/skills")
    public ResponseEntity<String> createSkill(@RequestBody SkillRequest skillRequest) {
        Boolean created = skillsService.create(skillRequest);

        if (created) {
            return new ResponseEntity<>("Skill created successfully", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Could not create skill", HttpStatus.BAD_REQUEST);

    }

    // Update a skill
    @PutMapping("/api/skills/{id}")
    public ResponseEntity<String> updateSkill(@PathVariable Long id, @RequestBody SkillRequest skillRequest) {
        Boolean updated = skillsService.updateById(id, skillRequest);
        if (updated) {
            return new ResponseEntity<>("Skill updated successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Could not update skill", HttpStatus.BAD_REQUEST);
    }

    // Delete a skill
    @DeleteMapping("/api/skills/{id}")
    public ResponseEntity<String> deleteSkill(@PathVariable Long id) {
        Boolean deleted = skillsService.deleteById(id);
        if (deleted) {
            return new ResponseEntity<>("Skill deleted successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Could not delete skill", HttpStatus.BAD_REQUEST);
    }
}
