package mobile.health.healine.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mobile.health.healine.Entity.dto.*;
import mobile.health.healine.Service.DailyLogService;
import mobile.health.healine.Service.HomeService;
import mobile.health.healine.Service.InBodyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/home")
@Tag(name = "home", description = "홈 페이지 관련 API")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;
    private final InBodyService inBodyService;

    // 오늘 활동량 정보 가져오기
    @GetMapping("/activity/{userId}")
    public ResponseEntity<DailyLogDto> getDailyLog(@PathVariable String userId) {
        DailyLogDto dailyLogDto = homeService.getDailyLog(userId);
        return new ResponseEntity<>(dailyLogDto, HttpStatus.OK);
    }

    // 회원정보 수정
    @PutMapping(
            value    = "/edit/profile/{userId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> updateProfile(@PathVariable String userId, @ModelAttribute ProfileDto profileDto) {
        homeService.updateProfile(userId, profileDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    // 등급정보 불러오기
    @GetMapping("/grade/{userId}")
    public ResponseEntity<GradeDto> getGrade(@PathVariable String userId) {
        GradeDto gradeDto = homeService.getGrade(userId);
        return new ResponseEntity<>(gradeDto, HttpStatus.OK);
    }

    // 회원 기본정보 가져오기
    @GetMapping("/profile/{userId}")
    public ResponseEntity<ProfileResponseDto> getProfile(@PathVariable String userId) {
        ProfileResponseDto profileDto = homeService.getProfile(userId);
        return new ResponseEntity<>(profileDto, HttpStatus.OK);
    }

    // 인바디 목록 불러오기
    @GetMapping("/profile/inbody/{userId}")
    public ResponseEntity<List<InBodyResponseDto>> getInBodyList(@PathVariable String userId) {
        return ResponseEntity.ok(inBodyService.getInBodyByUserId(userId));
    }

    // 인바디 기록 하기
    @PostMapping("/profile/inbody/{userId}")
    public ResponseEntity<Map<String, String>> recordInBody(
            @PathVariable String userId,
            @RequestBody InBodyResponseDto inBodyResponseDto) {

        inBodyService.recordInBody(userId, inBodyResponseDto);

        Map<String, String> response = new HashMap<>();
        response.put("message", "ok");

        return ResponseEntity.ok(response);
    }

    // 인바디 기록 수정
    @PutMapping("/profile/inbody/{userId}")
    public ResponseEntity<Map<String, String>> updateInBody(@PathVariable String userId, @RequestBody InBodyResponseDto inBodyResponseDto) {
        inBodyService.editInBody(userId, inBodyResponseDto);
        Map<String, String> response = new HashMap<>();
        response.put("message", "ok");

        return ResponseEntity.ok(response);
    }

    // 인바디 기록 한개 불러오기
    @GetMapping("/profile/inbody/{userId}/{date}")
    public ResponseEntity<InBodyResponseDto> getInBodyByDate(@PathVariable String userId, @PathVariable LocalDate date) {
        InBodyResponseDto dto = inBodyService.getInBody(userId, date);
        return ResponseEntity.ok(dto);
    }
}
