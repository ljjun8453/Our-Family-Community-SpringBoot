package com.community.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class MemberFormDto {

    @NotBlank(message = "이름은 필수 입력 값입니다!")
    private String name;

    @NotBlank(message = "아이디는 필수 입력 값입니다!")
    private String userId;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다!")
    @Length(min=8, max = 20, message = "비밀번호는 8자 이상, 20자 이하로 입력해주세요.")
    private String password;

    @NotBlank(message = "이메일은 필수 입력 값입니다!")
    @Email(message = "이메일 형식으로 입력해주세요.")
    private String email;

    @Pattern(regexp = "^\\d{8}$", message = "생년월일은 8자리 숫자(yyyyMMdd)로 입력")
    private String birthday;

    public LocalDate getBirthdayAsLocalDate() {
        return LocalDate.parse(birthday, DateTimeFormatter.BASIC_ISO_DATE); // yyyyMMdd
    }

    @NotNull(message = "\'개인정보 수집 및 이용\' 동의는 필수입니다!")
    @AssertTrue(message = "\'개인정보 수집 및 이용\'에 동의해주세요!")
    private Boolean privacyAgree;
}
