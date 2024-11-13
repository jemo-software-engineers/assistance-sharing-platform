package com.jemo.assistance_sharing_platform.offer;

import com.jemo.assistance_sharing_platform.user.User;
import com.jemo.assistance_sharing_platform.user.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Getter
@Setter
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;
    private final UserService userService;

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

    // View all offers for a request.. Only owner and admin should see this
//    @GetMapping("/api/requests/{id}/offers")
//    public ResponseEntity<List<OfferResponse>> viewAllOffersForRequest(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
//        User authenticatedUser = userService.findByUsername(userDetails.getUsername());
//        if (authenticatedUser != null) {
//            List<Offer> offers = offerService.getAllOffersByRequestId(id, authenticatedUser);
//            if (!offers.isEmpty()) {
//                List<OfferResponse offerResponses = offers.stream()
//                        .map(offer -> {
//                            OfferResponse offerResponse = new OfferResponse();
//                            // figure out a way to return the offer along with the user who offered and their details
//                        }).toList();
//                return new ResponseEntity<>(offerResponses, HttpStatus.OK);
//            }
//        }
//        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }

    // Withdraw an offer

    // Approve an offer

    // Reject an offer
}
