package com.ldif.delivery.global.infrastructure.config.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthorizationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsServiceImpl userDetailsService){
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected  void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {

        String tokenValue = jwtTokenProvider.getTokenFromHeader(req);

        if (StringUtils.hasText(tokenValue)){
            // JWT 토큰 substring
            tokenValue = jwtTokenProvider.substringToken(tokenValue);

            if (!jwtTokenProvider.validateToken(tokenValue)){
                log.error("Token error");
                return;
            }

            Claims info = jwtTokenProvider.getUserInfoFromToken(tokenValue);

            try{
                // 1. JWT에서 role 추출
                String tokenRole = (String) info.get("auth");

                // 2. DB에서 role 조회
                UserDetails userDetails = userDetailsService.loadUserByUsername(info.getSubject());
                String dbRole = userDetails.getAuthorities().stream()
                        .findFirst()
                        .map(GrantedAuthority::getAuthority)
                        .orElseThrow(() -> new RuntimeException("권한 정보가 없습니다."));

                // 3. 불일치 시 차단 대신 재로그인 유도
                if (!tokenRole.equals(dbRole)) {
                    log.warn("권한 불일치 - JWT: {}, DB: {}", tokenRole, dbRole, info.getSubject());
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.setContentType("application/json");
                    res.setCharacterEncoding("UTF-8");
                    res.getWriter().write(
                            "{\"status\": 401, " +
                                    "\"message\": \"권한이 변경되었습니다. 다시 로그인해주세요.\"}"
                    );
                    return;
                }

                setAuthentiaction(info.getSubject());

            }catch (Exception e){
                log.error(e.getMessage());
                return;
            }
        }

        filterChain.doFilter(req, res);
    }

    // 인증 처리
    public void setAuthentiaction(String username){
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username){
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

}
