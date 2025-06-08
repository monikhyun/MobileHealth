package mobile.health.healine.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mobile.health.healine.Entity.dto.DailyLogDto;
import mobile.health.healine.Service.DailyLogService;
import mobile.health.healine.Service.HomeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
@Tag(name = "home", description = "홈 페이지 관련 API")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    // 오늘 활동량 정보 가져오기
    @GetMapping("/activity/{userId}")
    public ResponseEntity<DailyLogDto> getDailyLog(@PathVariable String userId) {
        DailyLogDto dailyLogDto = homeService.getDailyLog(userId);
        return new ResponseEntity<>(dailyLogDto, HttpStatus.OK);
    }
}
