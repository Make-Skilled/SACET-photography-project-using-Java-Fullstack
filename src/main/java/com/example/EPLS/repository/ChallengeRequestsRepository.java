package com.example.EPLS.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.EPLS.model.ChallengeRequests;

public interface ChallengeRequestsRepository extends JpaRepository<ChallengeRequests,Long> {

	Optional<ChallengeRequests> findByChallengeIdAndUserEmail(Long challengeId, String userEmail);

}
