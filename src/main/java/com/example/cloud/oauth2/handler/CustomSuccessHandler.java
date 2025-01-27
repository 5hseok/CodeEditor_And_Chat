package com.example.cloud.oauth2.handler;

import com.example.cloud.oauth2.dto.CustomOAuth2User;
import com.example.cloud.oauth2.jwt.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    public CustomSuccessHandler(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String username = customUserDetails.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();
        Long id = customUserDetails.getId();
        String email = customUserDetails.getEmail();

        String token = jwtUtil.createJwt(id, username, email, role, 60*100*60*60L);
        System.out.println("생성된 jwt 토큰 : " + token);

//        response.addCookie(createCookie("Authorization", token));
        response.setHeader("Authorization", token);
        response.sendRedirect("https://codablesite.netlify.app");
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60);
         cookie.setSecure(true); // <- https 적용 시 활성화
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setAttribute("SameSite", "None"); // Cross-Origin 요청을 허용하려면 필수

        System.out.println("Cookie: " + cookie.getValue());

        return cookie;
    }


}
