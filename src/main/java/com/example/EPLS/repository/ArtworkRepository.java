package com.example.EPLS.repository;

import com.example.EPLS.model.Artwork;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ArtworkRepository extends JpaRepository<Artwork, Long> {
    List<Artwork> findBySellerEmail(String sellerEmail);
    List<Artwork> findByIsSoldFalse();
    List<Artwork> findTop3ByIsSoldFalseOrderByIdDesc();
}
