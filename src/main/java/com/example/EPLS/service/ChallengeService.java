package com.example.EPLS.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.EPLS.model.Challenges;
import com.example.EPLS.repository.ChallengesRepository;

public class ChallengeService {
	
	@Autowired
    private ChallengesRepository challengeRepository;

    // Get challenge by ID
    public Challenges getChallengeById(Long id) {
        return challengeRepository.findById(id).orElseThrow(() -> new RuntimeException("Challenge not found"));
    }

    // Update challenge
    public void updateChallenge(Long id, Challenges updatedChallenge) {
        Challenges challenge = challengeRepository.findById(id).orElseThrow(() -> new RuntimeException("Challenge not found"));
        
        // Update the fields
        challenge.setChallengeTitle(updatedChallenge.getChallengeTitle());
        challenge.setDescription(updatedChallenge.getDescription());
        challenge.setImagePath(updatedChallenge.getImagePath());

        // Save the updated challenge
        challengeRepository.save(challenge);
    }

}
