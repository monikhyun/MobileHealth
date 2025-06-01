package mobile.health.healine.Controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mobile.health.healine.Entity.dto.DietDto;
import mobile.health.healine.Service.DietServiceImpl;
import mobile.health.healine.Service.RegisterServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/diet")
@Tag(name = "Diet", description = "식단 관련 API")
@RequiredArgsConstructor
public class DietController {
    private final DietServiceImpl dietService;
    private final RegisterServiceImpl resisterService;


    @PostMapping("/record/{userId}")
    public ResponseEntity<String> record(@PathVariable String userId, @RequestBody DietDto dietDto){
        if(resisterService.validate(userId)){
            return ResponseEntity.ok("등록된 회원이 아닙니다.");
        }
        dietService.dietSave(dietDto);
        return ResponseEntity.ok("정상적으로 등록되었습니다.");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<DietDto>> find(@PathVariable String userId, @RequestParam(required = false) LocalDate date){
        return ResponseEntity.ok(dietService.dietFind(userId, date));
    }

    @GetMapping("/record/{userId}/{dietId}")
    public ResponseEntity<DietDto> find(@PathVariable String userId, @PathVariable Long dietId){
        if(resisterService.validate(userId)){
            return ResponseEntity.ok(DietDto.builder().build());
        }
        return ResponseEntity.ok(dietService.dietFindOne(dietId));
    }

    @PutMapping("/record/{userId}/{dietId}/update")
    public ResponseEntity<String> update(@PathVariable String userId, @PathVariable Long dietId, @RequestBody DietDto dietDto){
        dietService.dietUpdate(dietDto, dietId);
        return ResponseEntity.ok("저장되었습니다");
    }
}
