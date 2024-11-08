package com.jemo.assistance_sharing_platform.request;

import com.jemo.assistance_sharing_platform.user.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
@RequiredArgsConstructor
@Getter
@Setter
public class RequestController {

    private final UserService userService;
    private final RequestService requestService;

    // send a request
//    @PostMapping("/api/request")
//    public ResponseEntity<String> createRequest(@AuthenticationPrincipal UserDetails userDetails, RequestUserRequest userRequest) {
//        User authenticatedUser = userService.findByUsername(userDetails.getUsername());
//        if (authenticatedUser == null) {
//            Boolean created = requestService.addRequest(authenticatedUser, userRequest);
//            if(created) {
//                return ResponseEntity.ok("Request added successfully");
//            }
//        }
//
//        return new R
//    }
}
