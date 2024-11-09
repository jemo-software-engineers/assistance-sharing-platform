package com.jemo.assistance_sharing_platform.request;

import com.jemo.assistance_sharing_platform.skills.Skill;
import com.jemo.assistance_sharing_platform.skills.SkillService;
import com.jemo.assistance_sharing_platform.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
