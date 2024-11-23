package com.jemo.assistance_sharing_platform.offer;

import com.jemo.assistance_sharing_platform.request.Request;
import com.jemo.assistance_sharing_platform.request.RequestService;
import com.jemo.assistance_sharing_platform.request.RequestUserResponse;
import com.jemo.assistance_sharing_platform.skills.SkillService;
import com.jemo.assistance_sharing_platform.skills.UserSkill;
import com.jemo.assistance_sharing_platform.skills.UserSkillResponse;
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
@Getter
@Setter
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;
    private final UserService userService;
    private final RequestService requestService;

    // Submit an offer for a request. Owner of the request cannot make an offer.
    @PostMapping("/api/offers")
    public ResponseEntity<String> submitOffer(@RequestBody OfferUserRequest offerUserRequest, @AuthenticationPrincipal UserDetails userDetails) {
        User authenticatedUser = userService.findByUsername(userDetails.getUsername());
        if (authenticatedUser != null) {
            Boolean created = offerService.submitOffer(authenticatedUser, offerUserRequest);
            if(created) {
                return ResponseEntity.ok("Offer submitted successfully");
            }
        }

        throw new RuntimeException("Could not submit offer");
    }

    // View all offers for a request. Only owner and admin should see this
    @GetMapping("/api/requests/{id}/offers")
    public ResponseEntity<List<OfferResponse>> viewAllOffersForRequest(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        User authenticatedUser = userService.findByUsername(userDetails.getUsername());
        if (authenticatedUser != null) {
            List<Offer> offers = offerService.getAllOffersByRequestId(id, authenticatedUser);
            if (!offers.isEmpty()) {
                List<OfferResponse> offerResponses = offers.stream()
                        .map(offer -> {
                            List<UserSkill> skills = offer.getUserId().getUserSkills();
                            List<UserSkillResponse> skillResponses = SkillService.convertListOfSkillsToSkillsResponse(skills);


                            OfferResponse offerResponse = getOfferResponse(offer, skillResponses);
                            return offerResponse;
                        }).toList();
                return new ResponseEntity<>(offerResponses, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // View the request details of all my offers
    @GetMapping("/api/offers")
    public ResponseEntity<List<RequestUserResponse>> viewAllOffersForUser(@AuthenticationPrincipal UserDetails userDetails) {
        User authenticatedUser = userService.findByUsername(userDetails.getUsername());
        if (authenticatedUser != null) {
            List<Offer> offers = offerService.getAllOffersByUserId(authenticatedUser);

            if(!offers.isEmpty()) {
                List<RequestUserResponse> requestUserResponses = offers.stream()
                        .map(offer -> {
                            Request request = offer.getRequestId();
                            return RequestService.convertRequestToRequestUserResponse(request);
                        }).toList();
                return new ResponseEntity<>(requestUserResponses, HttpStatus.OK);
            }
        }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }



    // Withdraw an offer
    @PutMapping("/api/offers/{offerId}/withdraw")
    public ResponseEntity<String> withdrawOffer(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long offerId) {
        User authenticatedUser = userService.findByUsername(userDetails.getUsername());
        if (authenticatedUser != null) {
            Boolean withdrawn = offerService.withdrawOffer(offerId, authenticatedUser);
            if (withdrawn) {
                return ResponseEntity.ok("Offer withdrawn successfully");
            }
        }
        throw new RuntimeException("Could not withdraw offer");
    }
    // Request Owner Approve a specific offer
    @PutMapping("/api/requests/{requestId}/offers/{offerId}/approve")
    public ResponseEntity<String> approveOffer(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long offerId, @PathVariable Long requestId) {
        User authenticatedUser = userService.findByUsername(userDetails.getUsername());
        if (authenticatedUser != null) {
            Boolean approved = offerService.approveOffer(offerId, authenticatedUser, requestId);
            if (approved) {
                return ResponseEntity.ok("Offer approved successfully");
            }
        }
        throw new RuntimeException("Could not approve offer");
    }
    

    // Reject an offer




    private static OfferResponse getOfferResponse(Offer offer, List<UserSkillResponse> skillResponses) {
        OfferResponse offerResponse = new OfferResponse();
        offerResponse.setId(offer.getId());
        offerResponse.setRequestId(offer.getRequestId().getId());
        offerResponse.setUserId(offer.getUserId().getId());
        offerResponse.setUsername(offer.getUserId().getUsername());
        offerResponse.setPointScore(offer.getUserId().getPointScore());
        offerResponse.setSkills(skillResponses);
        return offerResponse;
    }
}
