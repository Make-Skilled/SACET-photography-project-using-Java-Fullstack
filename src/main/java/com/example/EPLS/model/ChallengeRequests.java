package com.example.EPLS.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ChallengeRequests {
	
	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column
	private Long challengeId;
	
	@Column
	private String userRequested;
	
	@Column
	private String challengeName;
	
	@Column
	private String userEmail;
	
	@Column(nullable = false)
	private int count = 0;
	
	@Column
	private String status="pending";
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getChallengeId() {
		return challengeId;
	}

	public void setChallengeId(Long challengeId) {
		this.challengeId = challengeId;
	}

	public String getUserRequested() {
		return userRequested;
	}

	public void setUserRequested(String userRequested) {
		this.userRequested = userRequested;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getChallengeName() {
		return challengeName;
	}

	public void setChallengeName(String challengeName) {
		this.challengeName = challengeName;
	}

	@Override
	public String toString() {
		return "ChallengeRequests [id=" + id + ", challengeId=" + challengeId + ", userRequested=" + userRequested
				+ ", challengeName=" + challengeName + ", userEmail=" + userEmail + ", count=" + count + ", status="
				+ status + "]";
	}	
	
	
	
}
