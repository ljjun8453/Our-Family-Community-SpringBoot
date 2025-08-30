package com.community.service;

import com.community.entity.Member;
import com.community.entity.Post;
import com.community.entity.PostLike;
import com.community.repository.MemberRepository;
import com.community.repository.PostLikeRepository;
import com.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final PostLikeRepository likeRepo;
    private final PostRepository postRepo;
    private final MemberRepository memberRepo;

    // true: 새로 좋아요 됨, false: 이미 눌렀음(변화 없음)
    @Transactional
    public boolean likeOnce(Long postId, String principal) {
        if (principal == null) return false;
        Member m = memberRepo.findByUserId(principal).orElse(null);
        if (m == null) return false;

        Post p = postRepo.getReferenceById(postId);
        try {
            likeRepo.saveAndFlush(new PostLike(p, m));   // 유니크 충돌 시 예외
            postRepo.incrementLikes(postId);
            return true;
        } catch (RuntimeException e) {
            // 유니크/중복 예외면 false
            Throwable t = e;
            while (t != null) {
                if (t instanceof org.springframework.dao.DataIntegrityViolationException
                        || t instanceof org.hibernate.exception.ConstraintViolationException
                        || t instanceof java.sql.SQLIntegrityConstraintViolationException) return false;
                t = t.getCause();
            }
            throw e;
        }
    }

}
