package org.demo.artExaminationInformationInquiry.config.SecurityConfig;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret; // Base64 编码的密钥

    @Value("${jwt.expiration}")
    private Long expiration; // 过期时间（秒）

    @Value("${jwt.refreshExpiration}")  // 新增refresh token有效期配置
    private Long refreshExpiration;

    // 获取签名密钥
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 生成 Token
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);

        return Jwts.builder()
                .setSubject(username)          // 用户名
                .setIssuedAt(now)              // 签发时间
                .setExpiration(expiryDate)     // 过期时间
                .signWith(getSigningKey(), SignatureAlgorithm.HS512) // 签名算法和密钥
                .compact();
    }

    // 新增refresh token生成方法
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // 从 Token 中获取用户名
    public String getUsernameFromToken(String token) {

            return getClaimsFromToken(token).getSubject();

    }

    // 验证 Token 是否有效
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = null;
            username = getUsernameFromToken(token);
        return (username != null) && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // 判断 Token 是否过期
    private boolean isTokenExpired(String token) {
        Date expiration = null;
            expiration = getClaimsFromToken(token).getExpiration();
        return expiration.before(new Date());
    }

    // 新增token刷新验证方法
    public boolean validateRefreshToken(String refreshToken) {
        try {
            getClaimsFromToken(refreshToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 从 Token 中获取 Claims
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()                 // 注意这里
                .setSigningKey(getSigningKey())     // 设置签名密钥
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
