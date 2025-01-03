package com.example.EPLS.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.EPLS.model.Challenges;
import com.example.EPLS.repository.ChallengesRepository;

@Service
public class ChallengeService {
	
	@Autowired
    private ChallengesRepository challengeRepository;

    // Get challenge by ID
    public Challenges getChallengeById(Long id) {
        return challengeRepository.findById(id).orElseThrow(() -> new RuntimeException("Challenge not found"));
    }

    
    public List<Challenges> getAllChallenges() {
        return challengeRepository.findAll();  // Retrieves all challenges from the database
    }

}
