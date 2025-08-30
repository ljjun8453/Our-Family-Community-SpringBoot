package com.community.config;

import com.community.service.MemberDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyAuthoritiesMapper;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class securityConfig {

    private final LoginSuccessHandler loginSuccessHandler;

    @Bean
    public RoleHierarchy roleHierarchy() {
        var impl = new RoleHierarchyImpl();
        impl.setHierarchy("ROLE_ADMIN > ROLE_USER3 > ROLE_USER2 > ROLE_USER1 > ROLE_GUEST > ROLE_GHOST");
        return impl;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthProvider(
            MemberDetailsService memberDetailsService,
            PasswordEncoder passwordEncoder,
            RoleHierarchy roleHierarchy
    ) {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(memberDetailsService);
        p.setPasswordEncoder(passwordEncoder);
        p.setAuthoritiesMapper(new RoleHierarchyAuthoritiesMapper(roleHierarchy));
        return p;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, DaoAuthenticationProvider daoAuthProvider) throws Exception {

        var csrfAttrHandler = new CsrfTokenRequestAttributeHandler(); // 지연 저장 비활성화

        http
                .authenticationProvider(daoAuthProvider)
                .csrf(csrf -> csrf
                        .csrfTokenRepository(new CookieCsrfTokenRepository())   // csrf 토큰 저장소를 http only cookie로 설정
                )
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                //로그인 처리
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(loginSuccessHandler)
                        .usernameParameter("userId")
                        .passwordParameter("password")
                        .failureUrl("/login?error")
                )
                //로그아웃 처리
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/?logout")
                )
                //페이지 접근권한 처리
                .authorizeHttpRequests(authorizeHttpRequestsCustomizer -> authorizeHttpRequestsCustomizer
                        // 관리자
                        .requestMatchers(HttpMethod.GET,
                                "/board/NOTICE/new",           // 글쓰기 폼
                                "/board/NOTICE/*/edit"         // 수정 폼
                        ).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,
                                "/board/NOTICE/new"            // 글 저장
                        ).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/board/NOTICE/*/edit"         // 글 수정
                        ).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,
                                "/board/NOTICE/*/delete"       // 글 삭제
                        ).hasRole("ADMIN")
                        .requestMatchers("/admin/**"
                        ).hasRole("ADMIN")
                        // 각 권한
                        .requestMatchers(HttpMethod.GET, "/board/**", "/mypage/**"
                        ).hasAnyRole("USER1", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/board/**", "/mypage/**", "/api/uploads"
                        ).hasAnyRole("USER1", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/board/**", "/mypage/**"
                        ).hasAnyRole("USER1", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/board/**", "/mypage/**"
                        ).hasAnyRole("USER1", "ADMIN")
                        // 누구나
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/favicon.ico","/", "/index", "/members/**", "/media/**", "/register", "/login", "/error/**"
                        ).permitAll()
                        // 그 외
                        .anyRequest().authenticated()
                // 접근권한 안내 페이지
                ).exceptionHandling(e -> e
                        .authenticationEntryPoint((req,res,ex)-> res.sendRedirect("/login"))
                        .accessDeniedHandler((req,res,ex)-> res.sendError(403))
                );

        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
        return auth.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
