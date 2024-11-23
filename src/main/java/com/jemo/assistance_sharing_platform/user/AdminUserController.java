package com.jemo.assistance_sharing_platform.user;

import com.jemo.assistance_sharing_platform.skills.SkillService;
import com.jemo.assistance_sharing_platform.skills.UserSkill;
import com.jemo.assistance_sharing_platform.skills.UserSkillResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/api")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
    private final UserService userService;
    private final SkillService skillService;


    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> allUsers = userService.getAllUsers();
        List<UserResponse> allUsersResponseDTO = allUsers.stream()
                .map(user -> {
                    return convertUserToUserResponse(user);
                }).toList();

        return new ResponseEntity<>(allUsersResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = userService.findUserById(id);
        if(user != null) {

            UserResponse userResponse = convertUserToUserResponse(user);

            return new ResponseEntity<>(userResponse, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }



    @PutMapping("/users/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest updateUserRequest) {
        boolean updated = userService.updateUser(id, updateUserRequest);
        if(updated) {
            return new ResponseEntity<>("User updated successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("User could not be updated", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUserById(id);
        if(deleted) {
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("User could not be deleted", HttpStatus.BAD_REQUEST);
    }





    private UserResponse convertUserToUserResponse(User user) {
        List<UserSkill> skills = user.getUserSkills();
        List<UserSkillResponse> skillResponses = SkillService.convertListOfSkillsToSkillsResponse(skills);


        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setRole(user.getRole().toString());
        userResponse.setName(user.getName());
        userResponse.setPhone(user.getPhone());
        userResponse.setAddress(user.getAddress());
        userResponse.setSkills(skillResponses);
        userResponse.setIsAvailable(user.getIsAvailable());
        userResponse.setPointScore(user.getPointScore());

        return userResponse;
    }



}

















