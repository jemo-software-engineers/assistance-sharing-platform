package com.jemo.assistance_sharing_platform.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/api")
//@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> allUsers = userService.getAllUsers();
        List<UserResponse> allUsersResponseDTO = allUsers.stream()
                .map(user -> {
                    UserResponse userResponse = new UserResponse();
                    userResponse.setId(user.getId());
                    userResponse.setUsername(user.getUsername());
                    userResponse.setEmail(user.getEmail());
                    userResponse.setRole(user.getRole().toString());
                    userResponse.setName(user.getName());
                    userResponse.setPhone(user.getPhone());
                    userResponse.setAddress(user.getAddress());
                    userResponse.setIsAvailable(user.getIsAvailable());
                    userResponse.setPointScore(user.getPointScore());
                    return userResponse;
                }).toList();

        return new ResponseEntity<>(allUsersResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = userService.findUserById(id);
        if(user != null) {
            UserResponse userResponse = new UserResponse();
            userResponse.setId(user.getId());
            userResponse.setUsername(user.getUsername());
            userResponse.setEmail(user.getEmail());
            userResponse.setRole(user.getRole().toString());
            userResponse.setName(user.getName());
            userResponse.setPhone(user.getPhone());
            userResponse.setAddress(user.getAddress());
            userResponse.setIsAvailable(user.getIsAvailable());
            userResponse.setPointScore(user.getPointScore());


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

}

















