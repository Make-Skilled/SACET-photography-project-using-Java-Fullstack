package com.example.EPLS.repository;

import com.example.EPLS.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPhotoId(Long photoId);
    List<Comment> findByPhotoIdOrderByCreatedAtDesc(Long photoId);
}
