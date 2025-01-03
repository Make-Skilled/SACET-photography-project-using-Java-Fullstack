package com.example.EPLS.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.EPLS.model.ChallengeRequests;

public interface ChallengeRequestsRepository extends JpaRepository<ChallengeRequests,Long> {

	Optional<ChallengeRequests> findByChallengeIdAndUserEmail(Long challengeId, String userEmail);

	List<ChallengeRequests> findByUserEmail(String userEmail);

}
