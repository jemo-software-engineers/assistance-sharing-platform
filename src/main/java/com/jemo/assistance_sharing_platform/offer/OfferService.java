package com.jemo.assistance_sharing_platform.offer;

import com.jemo.assistance_sharing_platform.request.Request;
import com.jemo.assistance_sharing_platform.request.RequestService;
import com.jemo.assistance_sharing_platform.request.RequestStatus;
import com.jemo.assistance_sharing_platform.user.User;
import com.jemo.assistance_sharing_platform.user.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OfferService {
    private final OfferRepository offerRepository;
    private final RequestService requestService;

    public Boolean submitOffer(User authenticatedUser, OfferUserRequest offerUserRequest) {
        Request request = requestService.findById(offerUserRequest.requestId());
        if (request != null) {
            if (request.getCreatedBy().equals(authenticatedUser)) {
                return false;
            }
            Offer offerExists = offerRepository.findByRequestIdAndUserId(request, authenticatedUser);
            if (offerExists != null) {
                return false;
            }
            Offer offer = Offer.builder()
                    .userId(authenticatedUser)
                    .requestId(request)
                    .status(OfferStatus.PENDING)
                    .build();
            Offer savedOffer = offerRepository.save(offer);
            return savedOffer.getId() != null;
        }
        return false;
    }


    public List<Offer> getAllOffersByRequestId(Long id, User authenticatedUser) {
        Request request = requestService.findById(id);
        if (request != null && authenticatedUser != null) {
            if (request.getCreatedBy().equals(authenticatedUser) || authenticatedUser.getRole().equals(UserRole.ADMIN)) {
                return offerRepository.findAllByRequestId(request);
            }
        }
        return null;
    }

    public Offer getOfferById(Long id) {
        return offerRepository.findById(id).orElse(null);
    }

    public Boolean withdrawOffer(Long offerId, User authenticatedUser) {
        Offer offer = getOfferById(offerId);
        if (offer != null) {
            if (offer.getUserId().equals(authenticatedUser)) {
                offer.setStatus(OfferStatus.CANCELLED);
                offerRepository.save(offer);
                return true;
            }
        }
        return false;
    }

    public List<Offer> getAllOffersByUserId(User user) {
        return offerRepository.findAllByUserId(user);
    }

    @Transactional
    public Boolean approveOffer(Long offerId, User authenticatedUser, Long requestId) {
        // confirm that the authenticated user is the owner of the request
        Request request = requestService.findById(requestId);
        if (request != null && authenticatedUser != null && request.getCreatedBy().equals(authenticatedUser)) {
            List<Offer> offers = request.getOffers();

            // loop through all the offers for the request, and change all to declined, except the one to be approved.
            offers.stream()
                    .map(offer -> {
                        if (offer.getId().equals(offerId)) {
                            offer.setStatus(OfferStatus.ACCEPTED);
                        } else {
                            offer.setStatus(OfferStatus.DECLINED);
                        }
                        offerRepository.save(offer);
                        return offer;
                    }).toList();

            // update request status
            requestService.setStatus(request, RequestStatus.INPROGRESS);
            return true;

        }

        return false;
    }
}
