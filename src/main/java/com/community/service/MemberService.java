package com.community.service;

import com.community.entity.Member;
import com.community.entity.Post;
import com.community.repository.MemberRepository;
import com.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;

    // 아이디 중복 검사
    public boolean isUserIdTaken(String userId) {
        return memberRepository.existsByUserId(userId);
    }
    // 이메일 중복 검사
    public boolean isEmailTaken(String email) {
        return memberRepository.existsByEmail(email);
    }

    // 회원가입 DB저장
    public Member saveMember(Member member) {
        // 가입 첫 저장은 본인 userId를 명시적으로 기록
        if (member.getCreatedBy() == null || member.getCreatedBy().isBlank()) {
            member.setCreatedBy(member.getUserId());
        }
        if (member.getModifiedBy() == null || member.getModifiedBy().isBlank()) {
            member.setModifiedBy(member.getUserId());
        }
        return memberRepository.save(member);
    }

    // 아이디 찾기
    @Transactional
    public Member findByUserId(String userId) {
        return memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다: " + userId));
    }

    // 내가 쓴 게시글 50개씩 출력
    @Transactional
    public List<Post> findMyPosts(Long userId) {
        // authorId가 Long(PK)일 때
        String uid = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음: " + userId))
                .getUserId();
        return postRepository.findTop50ByCreatedByOrderByCreateTimeDesc(uid);
    }

    // 비밀번호 변경
    @Transactional
    public void changePassword(String userId, String currentPassword, String newPassword) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다: " + userId));

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            // 컨트롤러에서 잡고 메시지 띄우고 싶으면 예외 타입만 보고 처리하면 됨
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 길이 제한
        if (newPassword.length() < 8 || newPassword.length() > 20) {
            throw new IllegalArgumentException("비밀번호는 8자 이상 20자 이하로 입력해야 합니다.");
        }

        // 새 비밀번호 저장
        member.setPassword(passwordEncoder.encode(newPassword));
    }

    // 회원 탈퇴
    @Transactional
    public void deleteAccount(String userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다: " + userId));

        // 기본: 하드 삭제
        memberRepository.delete(member);

        // 소프트 삭제를 쓰고 있다면 위 한 줄 대신 예:
        // member.setDeleted(true);
        // member.setEnabled(false);
        // (그리고 save 호출 또는 dirty checking)
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));

        if(member == null){
            throw new UsernameNotFoundException(userId);
        }

        return User.builder()
                .username(member.getUserId())
                .password(member.getPassword())
                .build();
    }
}
