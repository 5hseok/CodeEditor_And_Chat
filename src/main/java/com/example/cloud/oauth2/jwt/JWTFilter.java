package com.example.cloud.oauth2.jwt;

import com.example.cloud.oauth2.dto.CustomOAuth2User;
import com.example.cloud.oauth2.dto.SocialUserDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. cookie에 접근해 Authorization의 값(순수토큰)을 꺼내온다.
        String authorization = null;
        Cookie[] cookies = request.getCookies();

        if(cookies != null){
            for (Cookie cookie : cookies) {
                System.out.println(cookie.getName());
                if (cookie.getName().equals("Authorization")) {
                    authorization = cookie.getValue();
                }
            }
        }

        // 2. 얻은 순수 토큰을 검증한다.

        //      ====== 토큰에 이상이 있을 경우 ======
        //      - 토큰이 발견되지 않았다면 로그를 남기고 다음 필터에 그대로 request와 response만 넘긴 후 아무것도 하지 않고 이 필터를 종료한다.
        //      - 토큰이 발견되었다면 토큰의 소멸시간을 확인하여 만료되었다면 로그를 남기고 다음 필터에 그대로 request와 response만 넘긴 후 아무것도 하지 않고 이 필터를 종료한다.

        //      ====== 토큰에 이상이 없을 경우 ======
        //      - 토큰에서 username과 role을 획득하여 새 UserDTO를 만들어 얻은 username과 role을 넣은 후 UserDetails에 담는다.
        //      - 스프링 시큐리티 인증토큰을 생성하고 세션에 사용자를 등록한다.
        //      - 정상적으로 등록 후 다음 필터로 넘긴다.

        //  ============================================================================================================

        //      - 토큰이 발견되지 않았다면 로그를 남기고 다음 필터에 그대로 request와 response만 넘긴 후 아무것도 하지 않고 이 필터를 종료한다.
        if(authorization == null) {
            System.out.println("token null");
            filterChain.doFilter(request, response);

            return; // 반드시 종료해줘야 함.
        }

        //      - 토큰이 발견되었다면 토큰의 소멸시간을 확인하여 만료되었다면 로그를 남기고 다음 필터에 그대로 request와 response만 넘긴 후 아무것도 하지 않고 이 필터를 종료한다.
        String token = authorization;
        if (jwtUtil.isExpired(token)) {
            System.out.println("token expired");
            filterChain.doFilter(request, response);

            return; // 반드시 종료해줘야 함.
        }

        //      - 토큰에서 username과 role을 획득하여 새 UserDTO를 만들어 얻은 username과 role을 넣은 후 UserDetails에 담는다.
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        SocialUserDTO socialUserDTO = new SocialUserDTO();
        socialUserDTO.setUsername(username);
        socialUserDTO.setRole(role);

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(socialUserDTO);

        //      - 스프링 시큐리티 인증토큰을 생성하고 세션에 사용자를 등록한다.
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        //      - 정상적으로 등록한 후 다음필터로 넘긴다.
        filterChain.doFilter(request, response);

    }
}
