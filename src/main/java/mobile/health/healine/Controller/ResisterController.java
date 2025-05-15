package mobile.health.healine.Controller;

import lombok.RequiredArgsConstructor;
import mobile.health.healine.Entity.dto.RegisterValidateDto;
import mobile.health.healine.Entity.dto.ResisterDto;
import mobile.health.healine.Entity.dto.ValidateDto;
import mobile.health.healine.Service.ResisterServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/register")
@RequiredArgsConstructor
public class ResisterController {

    private final ResisterServiceImpl resisterService;


    @PostMapping("/validate/{userid}")
    public ResponseEntity<ValidateDto> validation(@PathVariable String userid){
        boolean validate = resisterService.validate(userid);
        ValidateDto validateDto = ValidateDto.builder()
                .newId(validate)
                .userid(userid)
                .build();
        return ResponseEntity.ok(validateDto);
    }

    @PostMapping("/ok")
    public ResponseEntity<RegisterValidateDto> ok(@ModelAttribute ResisterDto resisterDto){
        resisterService.save(resisterDto);
        return ResponseEntity.ok(RegisterValidateDto.builder().success(true).build());
    }
}
