package mobile.health.healine.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mobile.health.healine.Entity.BodyPart;
import mobile.health.healine.Entity.Exercise;
import mobile.health.healine.Entity.dto.AddedExerciseDto;
import mobile.health.healine.Entity.dto.ExerciseDto;
import mobile.health.healine.Entity.dto.ExerciseRecordDto;
import mobile.health.healine.Service.ExerciseServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/exercise")
@Tag(name = "Exercise", description = "운동 관련 API")
@RequiredArgsConstructor
public class ExerciseController {
    private final ExerciseServiceImpl exerciseService;


    // 해야할 운동 목록 조회
    @GetMapping("/add/todo/{userId}/{date}")
    public ResponseEntity<List<AddedExerciseDto>> getAddedExercise(@PathVariable String userId, @PathVariable LocalDate date) {
        return ResponseEntity.ok(exerciseService.findAddedExercise(userId, date));
    }

    // 해야할 운동 추가
    @PostMapping("/add/{userId}/{date}/{exerciseName}")
    public ResponseEntity<String> addExercise(@PathVariable String userId, @PathVariable LocalDate date, @PathVariable String exerciseName) {

        exerciseService.addExercise(userId, exerciseName, date);

        return ResponseEntity.ok("운동 추가 완료");
    }
    // 해당 운동 데이터 불러오기
    @GetMapping("/add/{userId}/{exerciseName}")
    public ResponseEntity<Exercise> getExercise(@PathVariable String exerciseName) {
        return ResponseEntity.ok(exerciseService.ExerciseData(exerciseName));
    }
    // 해당 운동 기록 조회
    @GetMapping("/add/{userId}/{date}/{exerciseName}")
    public ResponseEntity<List<ExerciseRecordDto>> getRecord(@PathVariable String userId, @PathVariable LocalDate date, @PathVariable String exerciseName) {
        return ResponseEntity.ok(exerciseService.findRecord(userId, exerciseName, date));
    }
    // 해당 운동 기록 저장
    @PostMapping("/add/{userId}/{date}/{exerciseName}/record")
    public ResponseEntity<String> recordExercise(@PathVariable String userId, @PathVariable String exerciseName, @RequestBody ExerciseRecordDto recordDto) {
        // "+" → " " 치환
        String decodedName = exerciseName.replace("+", " ");
        exerciseService.saveExercise(userId, decodedName, recordDto);
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
    @GetMapping("/list/search")
    public ResponseEntity<List<ExerciseDto>> getSearchExercises( @RequestParam(required = false) BodyPart bodypart,
                                                                 @RequestParam(required = false) String exerciseName
    ) {
        return ResponseEntity.ok(exerciseService.searchExercise(bodypart, exerciseName));
    }

    // 운동 찜하기
    @GetMapping("/list/favorite/{userId}")
    public ResponseEntity<String> likeExercise(@PathVariable String userId, @RequestParam String exerciseName) {
        exerciseService.likeExercise(userId, exerciseName);
        return ResponseEntity.ok(exerciseName+" 찜 완료!!");
    }
    // 운동 찜하기 취소
    @GetMapping("/list/unfavorite/{userId}")
    public ResponseEntity<String> unlikeExercise(@PathVariable String userId, @RequestParam String exerciseName) {
        exerciseService.unlikeExercise(userId, exerciseName);
        return ResponseEntity.ok(exerciseName+" 찜 취소");
    }
    // 찜한 운동 조회
    @GetMapping("/list/{userId}")
    public ResponseEntity<List<ExerciseDto>> getSearchFavoriteExercises(@PathVariable String userId) {
        return ResponseEntity.ok(exerciseService.findFavoriteExercise(userId));
    }

}
