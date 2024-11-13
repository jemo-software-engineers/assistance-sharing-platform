package com.jemo.assistance_sharing_platform.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByStatus(RequestStatus requestStatus);
}
