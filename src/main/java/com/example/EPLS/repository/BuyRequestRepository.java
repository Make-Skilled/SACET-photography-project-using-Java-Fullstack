package com.example.EPLS.repository;

import com.example.EPLS.model.BuyRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BuyRequestRepository extends JpaRepository<BuyRequest, Long> {
    List<BuyRequest> findBySellerEmail(String sellerEmail);
    List<BuyRequest> findByArtworkId(Long artworkId);
    List<BuyRequest> findByArtworkIdAndStatus(Long artworkId, String status);
}
