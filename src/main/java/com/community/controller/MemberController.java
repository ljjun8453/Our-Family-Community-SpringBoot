package com.community.controller;

import com.community.config.MemberDetails;
import com.community.constant.Role;
import com.community.dto.MemberFormDto;
import com.community.entity.Member;
import com.community.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 페이지
    @GetMapping("/register")
    public String registerForm(Model model) {
        if (!model.containsAttribute("memberFormDto")) {
            model.addAttribute("memberFormDto", new MemberFormDto());
        }
        return "register";
    }

    // 회원가입 완료
    @PostMapping(value = "/register")
    public String newMember(@Valid @ModelAttribute("memberFormDto") MemberFormDto memberFormDto,
                            BindingResult bindingResult, Model model, RedirectAttributes rttr) {

        // ✅ 1) 아이디/이메일 중복을 ‘필드 에러’로 바로 달아준다 (예외 X)
        if (memberService.isUserIdTaken(memberFormDto.getUserId())) {
            bindingResult.rejectValue("userId", "duplicate", "이미 사용 중인 아이디입니다.");
        }
        if (memberService.isEmailTaken(memberFormDto.getEmail())) {
            bindingResult.rejectValue("email", "duplicate", "이미 가입된 이메일입니다.");
        }
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            if (member.getRole() == null) member.setRole(Role.USER1);
            memberService.saveMember(member);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // 경합 등으로 DB 유니크 위반 시에도 ‘필드 에러’로
            boolean userIdTaken = memberService.isUserIdTaken(memberFormDto.getUserId());
            boolean emailTaken  = memberService.isEmailTaken(memberFormDto.getEmail());
            if (userIdTaken)
                bindingResult.rejectValue("userId", "duplicate", "이미 사용 중인 아이디입니다.");
            if (emailTaken)
                bindingResult.rejectValue("email",  "duplicate", "이미 가입된 이메일입니다.");
            if (!userIdTaken && !emailTaken)
                bindingResult.reject("registerError", "가입 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
            return "register";
        }

        rttr.addFlashAttribute("alert", "회원가입이 완료되었습니다. 환영합니다!");
        return "redirect:/login";
    }

    // 로그인 페이지
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // 마이페이지
    @GetMapping("/mypage")
    public String mypage(@AuthenticationPrincipal MemberDetails memberDetails,
                         Model model) {
        Member member = memberService.findByUserId(memberDetails.getUsername());
        model.addAttribute("member", member);
        model.addAttribute("posts", memberService.findMyPosts(member.getId())); // 서비스에 맞게 구현
        return "mypage";
    }

    // 비밀번호 변경
    @PostMapping("/mypage/change-password")
    public String changePassword(@AuthenticationPrincipal MemberDetails memberDetails,
                                 String currentPassword, String newPassword, String confirmPassword,
                                 RedirectAttributes rttr) {
        // 길이 체크
        if (newPassword.length() < 8 || newPassword.length() > 20) {
            rttr.addFlashAttribute("alert", "비밀번호는 8자 이상 20자 이하로 입력해야 합니다.");
            return "redirect:/mypage";
        }
        // 일치 여부 체크
        if (!newPassword.equals(confirmPassword)) {
            rttr.addFlashAttribute("alert", "새 비밀번호가 일치하지 않습니다.");
            return "redirect:/mypage";
        }
        try {
            memberService.changePassword(memberDetails.getUsername(), currentPassword, newPassword);
            rttr.addFlashAttribute("alert", "비밀번호가 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            rttr.addFlashAttribute("alert", e.getMessage());
        }
        return "redirect:/mypage";
    }

    // 회원 탈퇴
    @PostMapping("/mypage/delete")
    public String deleteAccount(@AuthenticationPrincipal MemberDetails memberDetails,
                                RedirectAttributes rttr) {
        memberService.deleteAccount(memberDetails.getUsername());
        rttr.addFlashAttribute("alert", "회원 탈퇴가 완료되었습니다.");
        return "redirect:/logout"; // 또는 세션 무효화 후 홈으로
    }


//    @GetMapping("/login/error")
//    public String loginError(Model model) {
//        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
//        return "login";
//    }

//    @GetMapping("/logout/success")
//    public String logoutSuccess(RedirectAttributes rttr) {
//        rttr.addFlashAttribute("alert", "로그아웃이 완료되었습니다.");
//        return "redirect:/";
//    }
}
