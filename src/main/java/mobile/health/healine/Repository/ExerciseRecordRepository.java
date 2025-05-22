package mobile.health.healine.Repository;

import mobile.health.healine.Entity.ExerciseRecord;
import mobile.health.healine.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExerciseRecordRepository extends JpaRepository<ExerciseRecord, Long> {
    List<ExerciseRecord> findByMemberAndExerciseNameAndDate(Member member, String exerciseName, LocalDate date);


    Optional<ExerciseRecord> findByMemberAndExerciseNameAndDateAndSetCount(Member member, String exerciseName, LocalDate date, int setNumber);

    List<ExerciseRecord> findByMemberAndExerciseNameAndDateAndSetCountGreaterThan(Member member, String exerciseName, LocalDate date, Integer setCount);

    List<ExerciseRecord> findByMemberAndDate(Member member, LocalDate date);
}
