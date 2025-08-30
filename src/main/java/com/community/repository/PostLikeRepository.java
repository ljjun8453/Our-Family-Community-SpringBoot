package com.community.repository;

import com.community.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByPost_IdAndMember_UserId(Long postId, String userId);
    long countByPost_Id(Long postId);
    boolean existsByPost_IdAndMember_Id(Long postId, Long memberId);
}
