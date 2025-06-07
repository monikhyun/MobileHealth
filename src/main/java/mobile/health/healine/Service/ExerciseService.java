package mobile.health.healine.Service;

import mobile.health.healine.Entity.BodyPart;
import mobile.health.healine.Entity.Exercise;
import mobile.health.healine.Entity.dto.AddedExerciseDto;
import mobile.health.healine.Entity.dto.ExerciseDto;
import mobile.health.healine.Entity.dto.ExerciseRecordDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface ExerciseService {

    // 추가한 운동 목록 조회
    List<AddedExerciseDto> findAddedExercise(String userId, LocalDate date);
    // 기록할 운동 추가
    void addExercise(String userId, String exerciseName, LocalDate date);
    // 기록할 운동 제거
    void removeExercise(String userId, String exerciseName, LocalDate date);
    // 해당 운동 데이터 불러오기
    Exercise ExerciseData(String exerciseName);

    // 운동 기록 저장
    void saveExercise(String userId, String exerciseName, ExerciseRecordDto exerciseRecordDto);
    // 운동 기록 조회
    List<ExerciseRecordDto> findRecord(String userId, String exerciseName, LocalDate date);
    // 운동 기록 삭제
    void deleteRecord(String userId, String exerciseName, Integer setCount,LocalDate date);


    // 전체 운동
    List<ExerciseDto> findAllExercises();
    // 운동 찜하기
    void likeExercise(String userId, String exerciseName);
    // 운동 찜 취소
    void unlikeExercise(String userId, String exerciseName);
    // 찜한 운동 조회
    List<ExerciseDto> findFavoriteExercise(String userId);
    // 운동 검색
    List<ExerciseDto> searchExercise(BodyPart bodyPart, String exerciseName);
}
