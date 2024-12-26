package com.jemo.assistance_sharing_platform.request;

import com.jemo.assistance_sharing_platform.user.User;
import com.jemo.assistance_sharing_platform.user.UserRole;
import com.jemo.assistance_sharing_platform.user.UserService;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


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

        return ConvertListOfRequestToListOfRequestResponse(retrievedRequests);
    }

    // Get all requests for logged-in user
    @GetMapping("/api/my-requests")
    public ResponseEntity<List<RequestUserResponse>> getAllRequestsForLoggedInUser(@AuthenticationPrincipal UserDetails userDetails) {
        User authenticatedUser = userService.findByUsername(userDetails.getUsername());
        if (authenticatedUser != null) {
            List<Request> retrievedRequests = requestService.findAllByUserId(authenticatedUser.getId());
            return ConvertListOfRequestToListOfRequestResponse(retrievedRequests);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }


    // Update request
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

    // Delete Request
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



    // Get a list of all pending requests due for approval
    @GetMapping("/admin/api/requests")
    public ResponseEntity<List<RequestUserResponse>> getAllPendingRequests() {
        List<Request> retrievedRequests = requestService.findAllByPendingStatus();

        return ConvertListOfRequestToListOfRequestResponse(retrievedRequests);
    }


    // Approve Request Decision For ADMIN
    @Transactional
    @PutMapping("/admin/api/requests/{requestId}/approve")
    public ResponseEntity<String> approveRequest(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long requestId) {
        User authenticatedUser = userService.findByUsername(userDetails.getUsername());
        if (authenticatedUser.getRole().equals(UserRole.ADMIN)) {
            Request approvedRequest = requestService.approveRequest(requestId);
            if (approvedRequest.getStatus().equals(RequestStatus.OPEN)) {
                return new ResponseEntity<>("Request Approved", HttpStatus.OK);
            }
        }

        return new ResponseEntity<>("Review Approval Failed", HttpStatus.BAD_REQUEST);
    }

//    // Reject Request Decision For ADMIN
    @Transactional
    @PutMapping("/admin/api/requests/{requestId}/reject")
    public ResponseEntity<String> rejectRequest(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long requestId) {
        User authenticatedUser = userService.findByUsername(userDetails.getUsername());
        if (authenticatedUser.getRole().equals(UserRole.ADMIN)) {
            Request rejectedRequest = requestService.rejectRequest(requestId);
            if (rejectedRequest.getStatus().equals(RequestStatus.REJECTED)) {
                return new ResponseEntity<>("Request Rejected", HttpStatus.OK);
            }
        }

        return new ResponseEntity<>("Review Rejection Failed", HttpStatus.BAD_REQUEST);
    }


    // Complete a request and rate the helper
    @Transactional
    @PutMapping("/api/requests/{requestId}/complete")
    public ResponseEntity<String> completeRequest(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long requestId) {
        User authenticatedUser = userService.findByUsername(userDetails.getUsername());
        Request retrievedRequest = requestService.findById(requestId);
        if ((retrievedRequest.getCreatedBy().equals(authenticatedUser)) || authenticatedUser.getRole().equals(UserRole.ADMIN)) {
            Boolean completed = requestService.completeById(requestId);
            if(completed) {
                // retrieve accepted offer user, and update point score
                User approvedUser = requestService.findRequestApprovedUser(requestId);
                if (approvedUser.getId() != null) {
                    Boolean pointUpdated = userService.updateUserPointScore(approvedUser);
                    if (pointUpdated) {
                        return new ResponseEntity<>("Request Successfully Marked as Complete", HttpStatus.OK);
                    }
                }
            }
        }
        return new ResponseEntity<>("Count not complete request", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/api/requests/get_status")
    public List<String> getRequestStatus() {
        return Arrays.stream(RequestStatus.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }










    private RequestUserResponse ConvertRequestToRequestResponse(Request request) {
        return RequestService.convertRequestToRequestUserResponse(request);
    }

    private ResponseEntity<List<RequestUserResponse>> ConvertListOfRequestToListOfRequestResponse(List<Request> retrievedRequests) {
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



}
