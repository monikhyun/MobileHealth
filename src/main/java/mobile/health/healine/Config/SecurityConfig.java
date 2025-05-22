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

    // 4) SecurityFilterChain 설정, requestMatchers 부분은 그대로 유지
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CORS 설정
        http.cors(cors -> cors.configurationSource(new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest req) {
                CorsConfiguration cfg = new CorsConfiguration();
                cfg.setAllowedOrigins(Collections.singletonList("http://localhost:8080"));
                cfg.setAllowedMethods(Collections.singletonList("*"));
                cfg.setAllowCredentials(true);
                cfg.setAllowedHeaders(Collections.singletonList("*"));
                cfg.setMaxAge(3600L);
                return cfg;
            }
        }));

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
/*                        // POST /api/auth/register/validate/** 명시적으로 풀어주기
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register/validate/**").permitAll()

                        // POST /api/auth/login 와
                        // POST /api/auth/login/validate/** 도 풀어주기
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login/validate/**").permitAll()
                        *//*.requestMatchers("/api/**").hasAuthority("ROLE_USER")*//*
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("api/exercise/**").permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")*/
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form.disable())
                .logout(logout -> logout.permitAll())
                // 5) 분리한 AuthenticationProvider 등록
                .authenticationProvider(authenticationProvider());

        // JWT 필터 등록
        http.addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}