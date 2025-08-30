package com.community.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public LoginSuccessHandler() {
        // 기본 리다이렉트 목적지 (원래 가려던 곳 유지하려면 주석)
        setDefaultTargetUrl("/home");
        setAlwaysUseDefaultTargetUrl(false);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        // 로그 찍어보기
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        log.info(">> LOGIN OK user={}, roles={}", authentication.getName(), roles);

        // FlashMap에 alert 저장 (다음 요청에서 1회성 노출)
        SessionFlashMapManager flashMapManager = new SessionFlashMapManager();
        FlashMap flashMap = new FlashMap();
        flashMap.put("alert", authentication.getName() + " 님, 즐거운 시간 되세요!");
        flashMapManager.saveOutputFlashMap(flashMap, request, response);

        // ✔ 딱 한 번만 호출
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
