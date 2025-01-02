package com.example.EPLS.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.EPLS.model.Users;

public interface UserRepository extends JpaRepository<Users,Long>{

	Users findByEmail(String email);

}
