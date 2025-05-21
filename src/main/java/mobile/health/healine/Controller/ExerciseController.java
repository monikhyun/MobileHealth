package mobile.health.healine.Controller;

import lombok.RequiredArgsConstructor;
import mobile.health.healine.Entity.BodyPart;
import mobile.health.healine.Entity.Exercise;
import mobile.health.healine.Entity.dto.ExerciseDto;
import mobile.health.healine.Entity.dto.ExerciseRecordDto;
import mobile.health.healine.Service.ExerciseServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/exercise")
@RequiredArgsConstructor
public class ExerciseController {
    private final ExerciseServiceImpl exerciseService;

    // 해야할 운동 추가
    @PostMapping("/add/{userId}/{date}/{exerciseName}")
    public ResponseEntity<String> addExercise(@PathVariable String userId, @PathVariable LocalDate date, @PathVariable String exerciseName) {

        exerciseService.addExercise(userId, exerciseName, date);

        return ResponseEntity.ok("운동 추가 완료");
    }
    // 해당 운동 기록 조회
    @GetMapping("/add/{userId}/{date}/{exerciseName}")
    public ResponseEntity<List<ExerciseRecordDto>> getRecord(@PathVariable String userId, @PathVariable LocalDate date, @PathVariable String exerciseName) {
        return ResponseEntity.ok(exerciseService.findRecord(userId, exerciseName, date));
    }
    // 해당 운동 기록 저장
    @PostMapping("/add/{userId}/{date}/{exerciseName}/record")
    public ResponseEntity<String> recordExercise(@PathVariable String userId, @PathVariable LocalDate date, @PathVariable String exerciseName, @RequestBody ExerciseRecordDto recordDto) {
        exerciseService.saveExercise(userId, exerciseName, recordDto);
        return ResponseEntity.ok(recordDto.getSetCount().toString() +"세트 저장완료!");
    }
    // 해당 운동 기록 세트별 삭제
    @PutMapping("/add/{userId}/{date}/{exerciseName}/delete/{setCount}")
    public ResponseEntity<String> deleteRecord(@PathVariable String userId, @PathVariable LocalDate date, @PathVariable String exerciseName, @PathVariable Integer setCount) {
        exerciseService.deleteRecord(userId, exerciseName, setCount, date);
        return ResponseEntity.ok(setCount + "세트 기록삭제");
    }
    // 운동 전체 목록 조회
    @GetMapping("/list")
    public ResponseEntity<List<ExerciseDto>> getAllExercises() {
        return ResponseEntity.ok(exerciseService.findAllExercises());
    }
    // 운동 검색
    @GetMapping("/list/{bodypart}/{exerciseName}")
    public ResponseEntity<List<ExerciseDto>> getSearchExercises(@PathVariable BodyPart bodypart, @PathVariable String exerciseName) {
        return ResponseEntity.ok(exerciseService.searchExercise(bodypart, exerciseName));
    }

    // 찜한 운동 조회
    @GetMapping("/list/{userId}")
    public ResponseEntity<List<ExerciseDto>> getSearchFavoriteExercises(@PathVariable String userId) {
        return ResponseEntity.ok(exerciseService.findFavoriteExercise(userId));
    }

}
