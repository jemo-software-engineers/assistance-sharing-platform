package com.jemo.assistance_sharing_platform.offer;

import com.jemo.assistance_sharing_platform.request.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findAllByRequestId(Request request);
}
