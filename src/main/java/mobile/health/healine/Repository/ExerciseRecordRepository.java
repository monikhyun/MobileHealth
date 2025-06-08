package mobile.health.healine.Repository;

import mobile.health.healine.Entity.ExerciseRecord;
import mobile.health.healine.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExerciseRecordRepository extends JpaRepository<ExerciseRecord, Long> {
    List<ExerciseRecord> findByMemberAndExerciseNameAndDate(Member member, String exerciseName, LocalDate date);


    Optional<ExerciseRecord> findByMemberAndExerciseNameAndDateAndSetCount(Member member, String exerciseName, LocalDate date, int setNumber);

    List<ExerciseRecord> findByMemberAndExerciseNameAndDateAndSetCountGreaterThan(Member member, String exerciseName, LocalDate date, Integer setCount);

    List<ExerciseRecord> findByMemberAndDate(Member member, LocalDate date);

    boolean existsByMemberAndExerciseNameAndDate(
            Member member,
            String exerciseName,
            LocalDate date
    );

    List<ExerciseRecord> findByMemberAndDateBetween(Member member, LocalDate start, LocalDate end);

    int countByMember(Member mjc);

    @Query("SELECT COUNT(DISTINCT e.date) FROM ExerciseRecord e WHERE e.member = :member")
    long countDistinctByDate(@Param("member") Member member);

    List<ExerciseRecord> findByMemberAndDateBetweenAndDoneTrue(Member member, LocalDate start, LocalDate end);
}
