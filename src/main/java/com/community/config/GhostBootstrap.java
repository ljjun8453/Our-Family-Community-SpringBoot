package com.community.config;

import com.community.constant.Role;
import com.community.entity.Member;
import com.community.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@PropertySource(value = "classpath:application-prod.properties", encoding = "UTF-8")
@RequiredArgsConstructor
public class GhostBootstrap implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ghost.name}")
    private String ghostName;

    @Value("${ghost.userId}")
    private String ghostUserId;

    @Value("${ghost.password}")
    private String ghostPassword;

    @Value("${ghost.email}")
    private String ghostEmail;

    @Value("${ghost.birthday}")
    private LocalDate ghostBirthday;

    @Value("${ghost.privacyAgree}")
    private boolean ghostPrivacyAgree;

    @Override
    public void run(ApplicationArguments args) {
        memberRepository.findByUserId("ghost").ifPresentOrElse(m -> {}, () -> {
            Member ghost = new Member();
            ghost.setName(ghostName);
            ghost.setUserId(ghostUserId);
            ghost.setPassword(passwordEncoder.encode(ghostPassword));
            ghost.setEmail(ghostEmail);
            ghost.setBirthday(ghostBirthday);
            ghost.setCreatedBy(ghost.getUserId());
            ghost.setModifiedBy(ghost.getUserId());
            ghost.setPrivacyAgree(ghostPrivacyAgree);
            ghost.setRole(Role.GHOST); // ★ 탈퇴한 사용자 권한
            ghost.setDeleted(false);
            memberRepository.save(ghost);
        });
    }
}
