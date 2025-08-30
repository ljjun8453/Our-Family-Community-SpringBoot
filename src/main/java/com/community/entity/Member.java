package com.community.entity;

import com.community.constant.Role;
import com.community.dto.MemberFormDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Member extends  BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50, unique = true)
    private String userId;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(unique = true, length = 100, nullable = false)
    private String email;

    @Column(length = 8)
    private LocalDate birthday;

    @Column(name = "created_by", updatable = false, nullable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(nullable = false)
    private Boolean privacyAgree = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @PrePersist
    public void setDefaultRole() {
        if (this.role == null)
            this.role = Role.USER1;
    }

    @Column
    private Boolean deleted = false;



    public static Member createMember(MemberFormDto memberFormDto,
                                      PasswordEncoder passwordEncoder) {
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setUserId(memberFormDto.getUserId());
        member.setPassword(passwordEncoder.encode(memberFormDto.getPassword()));
        member.setEmail(memberFormDto.getEmail());
        member.setBirthday(memberFormDto.getBirthdayAsLocalDate());
        member.setCreatedBy(memberFormDto.getUserId());
        member.setModifiedBy(memberFormDto.getUserId());
        member.setPrivacyAgree(memberFormDto.getPrivacyAgree());
        return member;
    }
}
