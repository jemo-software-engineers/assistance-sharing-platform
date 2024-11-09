package com.jemo.assistance_sharing_platform.request;

import com.jemo.assistance_sharing_platform.user.User;
import com.jemo.assistance_sharing_platform.user.UserRole;
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
public class RequestController {

    private final UserService userService;
    private final RequestService requestService;

    // create a new request
    @PostMapping("/api/requests")
    public ResponseEntity<String> createRequest(@AuthenticationPrincipal UserDetails userDetails, @RequestBody RequestUserRequest userRequest) {
        User authenticatedUser = userService.findByUsername(userDetails.getUsername());
        if (authenticatedUser != null) {
            Boolean created = requestService.addRequest(authenticatedUser, userRequest);
            if(created) {
                return ResponseEntity.ok("Request added successfully");
            }
        }

        throw new RuntimeException("Could not create request");
    }


    // Get a single request
    @GetMapping("/api/requests/{id}")
    public ResponseEntity<RequestUserResponse> getRequestById(@PathVariable Long id) {
            Request retrievedRequest = requestService.findById(id);

            if (retrievedRequest != null) {
                RequestUserResponse userResponse = ConvertRequestToRequestResponse(retrievedRequest);
                return new ResponseEntity<>(userResponse, HttpStatus.OK);
            }
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }


    // Get all requests
    @GetMapping("/api/requests")
    public ResponseEntity<List<RequestUserResponse>> getAllRequests() {
        List<Request> retrievedRequests = requestService.findAll();

        if(retrievedRequests != null) {
            List<RequestUserResponse> userResponses = retrievedRequests.stream()
                    .map(request -> {
                        RequestUserResponse userResponse = ConvertRequestToRequestResponse(request);
                        return userResponse;
                    }).toList();

            return new ResponseEntity<>(userResponses, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }


    @PutMapping("/api/requests/{id}")
    public ResponseEntity<String> updateRequest(@AuthenticationPrincipal UserDetails userDetails, @RequestBody RequestUserRequest userRequest, @PathVariable Long id) {
        User authenticatedUser = userService.findByUsername(userDetails.getUsername());
        Request retrievedRequest = requestService.findById(id);
        if ((retrievedRequest.getCreatedBy().equals(authenticatedUser)) || authenticatedUser.getRole().equals(UserRole.ADMIN)) {
            Boolean updated = requestService.updateById(id, userRequest);
            if(updated) {
                return new ResponseEntity<>("Request Updated Successfully", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Count not update request", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/api/requests/{id}")
    public ResponseEntity<String> deleteRequest(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        User authenticatedUser = userService.findByUsername(userDetails.getUsername());
        Request retrievedRequest = requestService.findById(id);
        if ((retrievedRequest.getCreatedBy().equals(authenticatedUser)) || authenticatedUser.getRole().equals(UserRole.ADMIN)) {
            Boolean deleted = requestService.deleteById(id);
            if(deleted) {
                return new ResponseEntity<>("Request Updated Successfully", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Count not delete request", HttpStatus.NOT_FOUND);
    }

    private RequestUserResponse ConvertRequestToRequestResponse(Request request) {
        RequestUserResponse userResponse = new RequestUserResponse();
        userResponse.setId(request.getId());
        userResponse.setTitle(request.getTitle());
        userResponse.setDescription(request.getDescription());
        userResponse.setAssociatedSkill(request.getAssociatedSkill().getName());
        userResponse.setStatus(request.getStatus().name());
        userResponse.setUser(request.getCreatedBy().getName());
        userResponse.setUserId(request.getCreatedBy().getId());
        return userResponse;
    }
}
