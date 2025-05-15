package mobile.health.healine.Controller;

import lombok.RequiredArgsConstructor;
import mobile.health.healine.Entity.dto.LoginValidateDto;
import mobile.health.healine.Service.LoginServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginServiceImpl loginService;

    @PostMapping("/validate/{userid}/{userpassword}")
    public ResponseEntity<LoginValidateDto> validate(@PathVariable("userid") String userid, @PathVariable("userpassword") String userpassword){
        return ResponseEntity.ok(LoginValidateDto.builder().success(loginService.validate(userid, userpassword)).build());
    }
}
