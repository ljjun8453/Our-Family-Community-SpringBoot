package com.community.service;

import com.community.config.MemberDetails;
import com.community.entity.Member;
import com.community.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Member m = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("No such user: " + userId));
        // 꼭 우리 커스텀 MemberDetails를 그대로 반환!
        return new MemberDetails(m);
    }
}
