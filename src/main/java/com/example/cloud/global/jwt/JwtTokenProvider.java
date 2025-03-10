package com.example.cloud.global.jwt;

import com.example.cloud.member.dto.JwtTokenDto;
import com.example.cloud.member.service.GeneralMemberService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String USER_ID = "id";

    private static final Long ACCESS_TOKEN_EXPIRATION_TIME = 24 * 60 * 60 * 1000L * 3;
    private static final Long REFRESH_TOKEN_EXPIRATION_TIME = 24 * 60 * 60 * 1000L * 14;
    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${spring.jwt.secret}")
    private String JWT_SECRET;

    public String issueAccessToken(Long id) {
        return generateToken(id, ACCESS_TOKEN_EXPIRATION_TIME);
    }

    public String issueRefreshToken(Long id) {
        return generateToken(id, REFRESH_TOKEN_EXPIRATION_TIME);
    }

    public String generateToken(Long id, Long tokenExpirationTime) {
        Claims claims = Jwts.claims();
        Date now = new Date();

        claims.put(USER_ID, id);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenExpirationTime))
                .signWith(getSigningKey())
                .setIssuer("Codeable")
                .compact();
    }

    public JwtTokenDto generateToken(Long id) {
        return new JwtTokenDto(issueAccessToken(id), issueRefreshToken(id));
    }

    public JwtValidationType validateToken(String token) {
        try {
            final Claims claims = getBody(token);
            return JwtValidationType.VALID_JWT;
        } catch (MalformedJwtException ex) {
            return JwtValidationType.INVALID_JWT_TOKEN;
        } catch (ExpiredJwtException ex) {
            return JwtValidationType.EXPIRED_JWT_TOKEN;
        } catch (UnsupportedJwtException ex) {
            return JwtValidationType.UNSUPPORTED_JWT_TOKEN;
        } catch (IllegalArgumentException ex) {
            return JwtValidationType.EMPTY_JWT;
        }
    }

    private SecretKey getSigningKey() {
        String encodedKey = Base64.getEncoder().encodeToString(JWT_SECRET.getBytes()); //SecretKey 통해 서명 생성
        return Keys.hmacShaKeyFor(encodedKey.getBytes());   //일반적으로 HMAC (Hash-based Message Authentication Code) 알고리즘 사용 -> 암호화 키 생성
    }

    private Claims getBody(String token) {
        final JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(getSigningKey()).build();
        token = GeneralMemberService.refineToken(token);
        log.info(jwtParser.parseClaimsJws(token).getBody().toString());
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public Long getUserFromJwt(String token) {
        Claims claims = getBody(token);
        return Long.valueOf(claims.get(USER_ID).toString());
    }
}