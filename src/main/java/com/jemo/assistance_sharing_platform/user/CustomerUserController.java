package com.jemo.assistance_sharing_platform.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/customer")
public class CustomerUserController {

    private final UserService userService;

    public CustomerUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/myprofile")
    public ResponseEntity<UserResponse> getUserById(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
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
        return new ResponseEntity<>( HttpStatus.NOT_FOUND);
    }

    @PutMapping("/update-my-profile")
    public ResponseEntity<String> updateUser(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UpdateUserRequest updateUserRequest) {
        User userToUpdate = userService.findByUsername(userDetails.getUsername());
        boolean updated = userService.updateUser(userToUpdate.getId(), updateUserRequest);
        if(updated) {
            return new ResponseEntity<>("User updated successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("User could not be updated", HttpStatus.BAD_REQUEST);
    }
}
