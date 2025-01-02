package com.example.EPLS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.EPLS.model.Images;

public interface ImagesRepository extends JpaRepository<Images,Long> {

	List<Images> findByUploadedBy(String user);

}
