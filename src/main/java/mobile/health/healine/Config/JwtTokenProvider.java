package mobile.health.healine.Config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.health.healine.Entity.dto.JwtToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import mobile.health.healine.Entity.RefreshTokenInfo;
import org.springframework.security.core.GrantedAuthority;
import mobile.health.healine.Repository.RefreshTokenInfoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JWT 토큰 생성 및 검증을 담당하는 Provider
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final RefreshTokenInfoRepository refreshRepo;

    // Base64 인코딩된 32바이트 이상 시크릿 키 (application.yml에 정의)
    @Value("${security.jwt.secret}")
    private String secret;

    // 액세스 토큰 만료 시간 (밀리초)
    @Value("${security.jwt.expiration-ms}")
    private long expirationMs;

    // JWT 서명용 키
    private Key key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // Access / Refresh 토큰 생성
    public JwtToken generateToken(Authentication auth) {
        String username = auth.getName();
        String roles = auth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date accessExp = new Date(now.getTime() + expirationMs);
        // Refresh는 access의 24배 (24시간)
        Date refreshExp = new Date(now.getTime() + (expirationMs * 24));

        String accessToken = Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(accessExp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(refreshExp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // RefreshToken 저장
        refreshRepo.save(new RefreshTokenInfo(username, refreshToken));

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 토큰으로부터 Authentication 생성
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        var authorities = Arrays.stream(
                        claims.get("roles", String.class).split(",")
                ).map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(
                claims.getSubject(), token, authorities);
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            log.warn("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }

    // Refresh 토큰으로 Access/Refresh 재생성
    public JwtToken refreshToken(String refreshToken) {
        if (!validateToken(refreshToken)) throw new RuntimeException("Invalid Refresh Token");
        String user = Jwts.parserBuilder().setSigningKey(key)
                .build().parseClaimsJws(refreshToken)
                .getBody().getSubject();

        var info = refreshRepo.findById(user)
                .orElseThrow(() -> new RuntimeException("No Refresh Token Stored"));
        if (!info.getRefreshToken().equals(refreshToken))
            throw new RuntimeException("Refresh Token Mismatch");

        Authentication auth = getAuthentication(refreshToken);
        return generateToken(auth);
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}