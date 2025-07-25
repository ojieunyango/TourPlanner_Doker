package com.example.tour_backend.config;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {
    private final String secretKey = "4XGd0F6phiouDCLWtZCSdR2TipfvvGG+s9tpYBkabiU="; // 실제 서비스에선 환경변수나 config로 관리하세요
    private final long validityInMilliseconds = 3600000; // 1시간

    /**
     * 토큰 생성
     * username과 role을 JWT의 Claim에 포함시킴
     */
    public String createToken(String username, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(username)           // 사용자 이름 (식별자)
                .claim("role", role)         // ✅ role도 claim에 추가
                .setIssuedAt(now)               // 토큰 발급 시간
                .setExpiration(expiry)          // 만료 시간
                .signWith(SignatureAlgorithm.HS256, secretKey) // 서명
                .compact();
    }

    /**
     * 토큰 유효성 검사
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | SignatureException e) {
            // 토큰 만료 또는 서명 불일치
            return false;
        } catch (JwtException e) {
            // 기타 JWT 관련 오류
            return false;
        }
    }

    /**
     * 토큰에서 username 추출
     */
    public String getUsername(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * 토큰에서 role 추출
     */
    public String getRole(String token) {
        return (String) Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("role");
    }
}
