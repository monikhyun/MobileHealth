package mobile.health.healine.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mobile.health.healine.Entity.dto.InBodyResponseDto;
import mobile.health.healine.Entity.dto.StatsResponseDto;
import mobile.health.healine.Service.StatsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/stats")
@Tag(name = "Stats", description = "통계 관련 API")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/daily/{userId}")
    public ResponseEntity<List<StatsResponseDto>> getDaily(@PathVariable String userId) {
        List<StatsResponseDto> data = statsService.getDailyStats(userId);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/weekly/{userId}/{date}")
    public ResponseEntity<List<StatsResponseDto>> getWeekly(@PathVariable String userId,
                                                            @PathVariable
                                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                            LocalDate date) {
        return ResponseEntity.ok(statsService.getWeeklyStats(userId, date));
    }

    @GetMapping("/monthly/{userId}/{date}")
    public ResponseEntity<List<StatsResponseDto>> getMonthly(@PathVariable String userId,
                                                             @PathVariable
                                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                             LocalDate date){
        return ResponseEntity.ok(statsService.getMonthlyStats(userId, date));
    }

    @GetMapping("/inbody/all/{userId}")
    public ResponseEntity<InBodyResponseDto> getAllInBody(@PathVariable String userId) {
        return ResponseEntity.ok(statsService.getInBodyResponse(userId));
    }

    @GetMapping("/inbody/weight/{userId}")
    public ResponseEntity<List<InBodyResponseDto>> getInBodyWeight(@PathVariable String userId) {
        return ResponseEntity.ok(statsService.getWeight(userId));
    }

    @GetMapping("/inbody/SMM/{userId}")
    public ResponseEntity<List<InBodyResponseDto>> getInBodySMM(@PathVariable String userId) {
        return ResponseEntity.ok(statsService.getSMM(userId));
    }

    @GetMapping("/inbody/LBM/{userId}")
    public ResponseEntity<List<InBodyResponseDto>> getInBodyLBM(@PathVariable String userId) {
        return ResponseEntity.ok(statsService.getLBM(userId));
    }

    @GetMapping("/inbody/BMI/{userId}")
    public ResponseEntity<List<InBodyResponseDto>> getInBodyBMI(@PathVariable String userId) {
        return ResponseEntity.ok(statsService.getBMI(userId));
    }

    @GetMapping("/inbody/fat/{userId}")
    public ResponseEntity<List<InBodyResponseDto>> getInBodyFatPercent(@PathVariable String userId) {
        return ResponseEntity.ok(statsService.getFatPercent(userId));
    }


}
