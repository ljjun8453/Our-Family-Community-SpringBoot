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
@PropertySource(value = "classpath:application-password.properties", encoding = "UTF-8")
@RequiredArgsConstructor
public class AdminBootstrap implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.name}")
    private String adminName;

    @Value("${admin.userId}")
    private String adminUserId;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.birthday}")
    private LocalDate adminBirthday;

    @Value("${admin.privacyAgree}")
    private boolean adminPrivacyAgree;

    @Override
    public void run(ApplicationArguments args) {
        memberRepository.findByUserId("admin").ifPresentOrElse(m -> {}, () -> {
            Member admin = new Member();
            admin.setName(adminName);
            admin.setUserId(adminUserId);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setEmail(adminEmail);
            admin.setBirthday(adminBirthday);
            admin.setCreatedBy(admin.getUserId());
            admin.setModifiedBy(admin.getUserId());
            admin.setPrivacyAgree(adminPrivacyAgree);
            admin.setRole(Role.ADMIN); // ★ 관리자 권한
            admin.setDeleted(false);
            memberRepository.save(admin);
        });
    }
}
