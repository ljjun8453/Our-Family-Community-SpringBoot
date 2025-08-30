package com.community.repository;

import com.community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    @Query(value = "SELECT * FROM comment WHERE post_id = :postId", nativeQuery = true)
    List<Comment> findByPostId(Long postId);

    @Query(value = "SELECT * FROM comment WHERE name = :name", nativeQuery = true)
    List<Comment> findByName(String name);

    long countByPostId(Long postId);

}
