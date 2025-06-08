package mobile.health.healine.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mobile.health.healine.Entity.Member;
import mobile.health.healine.Entity.dto.*;
import mobile.health.healine.Entity.dto.JwtToken;
import mobile.health.healine.Service.ExerciseService;
import mobile.health.healine.Service.ExerciseServiceImpl;
import mobile.health.healine.Service.LoginServiceImpl;
import mobile.health.healine.Service.RegisterServiceImpl;
import mobile.health.healine.Config.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "회원가입·로그인(JWT) API")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterServiceImpl registerService;
    private final LoginServiceImpl    loginService;
    private final JwtTokenProvider     jwtProvider;
    private final ExerciseServiceImpl exerciseService;

    /** 1) 가입 전 ID 중복 검증 (/api/auth/register/validate/{userid}) **/
    @PostMapping("/register/validate/{userid}")
    public ResponseEntity<ValidateDto> validateRegister(@PathVariable String userid) {
        boolean ok = registerService.validate(userid);
        return ResponseEntity.ok(
                ValidateDto.builder()
                        .newId(ok)
                        .userid(userid)
                        .build()
        );
    }

    @PostMapping("/register/validate2/{username}")
    public ResponseEntity<ValidateDto> validateRegister2(@PathVariable String username) {
        boolean ok = registerService.nickValidate(username);
        return ResponseEntity.ok(
                ValidateDto.builder()
                        .newId(ok)
                        .userid(username)
                        .build()
        );
    }

    /** 2) 실제 가입 (/api/auth/register) **/
    @PostMapping("/register")
    public ResponseEntity<RegisterValidateDto> doRegister(@ModelAttribute ResisterDto dto) {
        registerService.save(dto);
        return ResponseEntity.ok(
                RegisterValidateDto.builder()
                        .success(true)
                        .build()
        );
    }

    /** 3) 로그인 전 자격 검증 (/api/auth/login/validate/{userid}/{userpassword}) **/
    @PostMapping("/login/validate/{userid}/{userpassword}")
    public ResponseEntity<LoginValidateDto> validateLogin(
            @PathVariable String userid,
            @PathVariable String userpassword) {
        boolean ok = loginService.validate(userid, userpassword);
        JwtToken jwt = null;
        if (ok) {
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userid, null,
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );
            jwt = jwtProvider.generateToken(auth);
        }
        if (jwt != null) {
            return ResponseEntity.ok()
                .header("Authorization", jwt.getGrantType() + " " + jwt.getAccessToken())
                .body(LoginValidateDto.builder().success(ok).build());
        } else {
            return ResponseEntity.ok(LoginValidateDto.builder().success(ok).build());
        }
    }

    /** 4) 로그인 & JWT 발급 (/api/auth/login) **/
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> doLogin(@RequestBody AuthRequest req) {
        // 1) 기존 서비스로 비밀번호 체크
        if (!loginService.validate(req.getUsername(), req.getPassword())) {
            return ResponseEntity
                    .badRequest()
                    .body(new AuthResponse(false, null, null, "아이디/비밀번호 불일치"));
        }

        // 2) 권한은 모두 ROLE_USER 로 가정
        Authentication auth = new UsernamePasswordAuthenticationToken(
                req.getUsername(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // 3) JWT 생성
        JwtToken token = jwtProvider.generateToken(auth);

        // 로그인 성공 시 등급 자동 업데이트
        Member member = loginService.findMemberByUserId(req.getUsername());
        exerciseService.updateMemberGrade(member);
        return ResponseEntity.ok(
                AuthResponse.builder()
                        .success(true)
                        .accessToken(token.getGrantType() + " " + token.getAccessToken())
                        .refreshToken(token.getRefreshToken())
                        .message("로그인 성공")
                        .build()
        );
    }
}