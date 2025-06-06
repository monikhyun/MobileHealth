package mobile.health.healine.Config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import mobile.health.healine.Filter.JwtAuthenticationFilter;
import mobile.health.healine.Config.JwtTokenProvider;
import mobile.health.healine.Service.MemberDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final @Lazy MemberDetailsService memberDetailsService;

    // 1) PasswordEncoder 빈
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2) UserDetailsService + PasswordEncoder 조합을 책임지는 AuthenticationProvider 빈
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(memberDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // 3) AuthenticationManager를 외부에서 주입받도록 변경
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    private CorsConfigurationSource corsConfigSource() {
        return request -> {
            CorsConfiguration cfg = new CorsConfiguration();
            // 개발 중에는 "*"로 모두 허용하고, 배포 시 실 도메인만 나열
            cfg.setAllowedOrigins(List.of("http://localhost:8080"));
            cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
            cfg.setAllowCredentials(true);
            cfg.setAllowedHeaders(List.of("*"));
            cfg.setMaxAge(3600L);
            return cfg;
        };
    }
    // 4) SecurityFilterChain 설정, requestMatchers 부분은 그대로 유지
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1) 인증 관련 엔드포인트만 모두 열어 둠
                        .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()

                        // 2) Swagger/OpenAPI용 경로(필요 시) 열어두기
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // 3) 그 외 모든 요청은 반드시 인증이 되어야 함
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form.disable())
                .logout(logout -> logout.permitAll())
                .authenticationProvider(authenticationProvider());

        // JWT 필터 등록 (폼 로그인 필터 앞)
        http.addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}