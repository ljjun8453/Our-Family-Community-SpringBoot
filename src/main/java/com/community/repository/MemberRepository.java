package com.community.repository;

import com.community.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

//    Member findByUserId(String userId); // 로그인ID로 조회
    Optional<Member> findByUserId(String userId);

    boolean existsByUserId(String userId);
    boolean existsByEmail(String email);
}
