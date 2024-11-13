package com.jemo.assistance_sharing_platform.offer;

import com.jemo.assistance_sharing_platform.request.Request;
import com.jemo.assistance_sharing_platform.request.RequestService;
import com.jemo.assistance_sharing_platform.user.User;
import com.jemo.assistance_sharing_platform.user.UserRole;
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
        User user = authenticatedUser;
        Request request = requestService.findById(id);
        if (request != null && user != null) {
            if (request.getCreatedBy().equals(authenticatedUser) || user.getRole().equals(UserRole.ADMIN)) {
                return offerRepository.findAllByRequestId(request);
            }
        }
        return null;
    }
}
