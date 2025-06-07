package mobile.health.healine.Repository;

import mobile.health.healine.Entity.DailyExerciseLog;
import mobile.health.healine.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyExerciseLogRepository extends JpaRepository<DailyExerciseLog, Long> {

    Optional<DailyExerciseLog> findByMemberAndDate(Member member, LocalDate date);
}
