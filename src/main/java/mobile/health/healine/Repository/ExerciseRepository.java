package mobile.health.healine.Repository;

import mobile.health.healine.Entity.BodyPart;
import mobile.health.healine.Entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    Exercise findByExerciseName(String name);

    // 카테고리(부위)만으로 검색
    List<Exercise> findByCategory(BodyPart category);

    // 운동 이름만으로 검색
    List<Exercise> findByExerciseNameContainingIgnoreCase(String exerciseName);
    // 부위 + 이름 부분 매칭 검색 (대소문자 무시)
    List<Exercise> findByCategoryAndExerciseNameContainingIgnoreCase(BodyPart category, String exerciseName);
}
