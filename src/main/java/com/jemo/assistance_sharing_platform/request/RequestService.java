package com.jemo.assistance_sharing_platform.request;

import com.jemo.assistance_sharing_platform.offer.Offer;
import com.jemo.assistance_sharing_platform.offer.OfferStatus;
import com.jemo.assistance_sharing_platform.skills.Skill;
import com.jemo.assistance_sharing_platform.skills.SkillService;
import com.jemo.assistance_sharing_platform.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final SkillService skillService;

    public Boolean addRequest(User authenticatedUser, RequestUserRequest userRequest) {
        Skill skillToAdd = skillService.findByName(userRequest.associatedSkill());
        if (skillToAdd != null) {
            Request request = Request.builder()
                    .title(userRequest.title())
                    .description(userRequest.description())
                    .status(RequestStatus.PENDING)
                    .associatedSkill(skillToAdd)
                    .createdBy(authenticatedUser)
                    .build();
            Request savedRequest = requestRepository.save(request);
            return savedRequest.getId() != null;
        }
        return false;
    }

    public Request findById(Long id) {
        return requestRepository.findById(id).orElse(null);
    }

    public List<Request> findAll() {
        return requestRepository.findAll();
    }

    public Boolean updateById(Long id, RequestUserRequest userRequest) {
        Request request = findById(id);
        Skill skillToUpdate = skillService.findByName(userRequest.associatedSkill());
        if (request != null) {
            Request updatedRequest = Request.builder()
                    .id(request.getId())
                    .title(userRequest.title() != null ? userRequest.title() : request.getTitle())
                    .description(userRequest.description() != null ? userRequest.description() : request.getDescription())
                    .status(RequestStatus.PENDING)
                    .associatedSkill(skillToUpdate != null ? skillToUpdate : request.getAssociatedSkill())
                    .createdBy(request.getCreatedBy())
                    .createdAt(request.getCreatedAt())
                    .build();
            requestRepository.save(updatedRequest);
            return true;
        }
        return false;
    }

    public Boolean deleteById(Long id) {
        Request request = findById(id);
        if (request != null) {
            requestRepository.delete(request);
            return true;
        }
        return false;
    }

    public List<Request> findAllByPendingStatus() {
        return requestRepository.findAllByStatus(RequestStatus.PENDING);
    }

    public List<Request> findAllByUserId(Long id) {
        return requestRepository.findAllByCreatedById(id);
    }

    public Request approveRequest(Long requestId) {
        Request request = findById(requestId);
        if (request != null) {
            request.setStatus(RequestStatus.OPEN);
            return requestRepository.save(request);
        }
        return null;
    }

    public Request rejectRequest(Long requestId) {
        Request request = findById(requestId);
        if (request != null) {
            request.setStatus(RequestStatus.REJECTED);
            return requestRepository.save(request);
        }
        return null;
    }

    public static RequestUserResponse convertRequestToRequestUserResponse(Request request) {
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

    public void setStatus(Request request, RequestStatus requestStatus) {
        request.setStatus(requestStatus);
        requestRepository.save(request);
    }

    public Boolean completeById(Long requestId) {
        Request request = findById(requestId);
        if (request != null) {
            request.setStatus(RequestStatus.COMPLETED);
            requestRepository.save(request);
            return true;
        }
        return null;
    }

    public User findRequestApprovedUser(Long requestId) {
        Request request = findById(requestId);
        if (request != null && request.getOffers() != null) {
            List<Offer> offers = request.getOffers();
            Optional<Offer> completedOffer = offers.stream()
                    .filter(offer -> offer.getStatus().equals(OfferStatus.ACCEPTED))
                    .findFirst();
            System.out.println(completedOffer.toString());
           return completedOffer.get().getUserId();
        }
        return null;
    }


}
